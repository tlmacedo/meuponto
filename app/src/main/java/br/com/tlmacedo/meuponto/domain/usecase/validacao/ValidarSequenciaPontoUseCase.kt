// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/validacao/ValidarSequenciaPontoUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.validacao

import br.com.tlmacedo.meuponto.domain.model.Inconsistencia
import br.com.tlmacedo.meuponto.domain.model.InconsistenciaDetectada
import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.model.ResultadoValidacao
import br.com.tlmacedo.meuponto.domain.model.TipoPonto
import br.com.tlmacedo.meuponto.domain.repository.PontoRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use Case responsável por validar a sequência de registros de ponto.
 *
 * Verifica se o novo ponto a ser registrado segue a sequência correta
 * de entrada/saída, evitando inconsistências como:
 * - Saída sem entrada correspondente
 * - Duas entradas consecutivas
 * - Duas saídas consecutivas
 *
 * @property pontoRepository Repositório para consulta de pontos existentes
 *
 * @author Thiago
 * @since 2.0.0
 */
class ValidarSequenciaPontoUseCase @Inject constructor(
    private val pontoRepository: PontoRepository
) {
    /**
     * Valida se um novo ponto pode ser registrado na sequência atual.
     *
     * @param empregoId ID do emprego
     * @param novoPonto Ponto a ser validado
     * @return ResultadoValidacao com o resultado da validação
     */
    suspend operator fun invoke(
        empregoId: Long,
        novoPonto: Ponto
    ): ResultadoValidacao {
        val data = novoPonto.dataHora.toLocalDate()
        val pontosDoDia = pontoRepository.buscarPorEmpregoEData(empregoId, data)
            .sortedBy { it.dataHora }

        return validarSequencia(novoPonto, pontosDoDia)
    }

    /**
     * Valida a sequência considerando os pontos já existentes no dia.
     *
     * @param novoPonto Ponto a ser validado
     * @param pontosExistentes Pontos já registrados no dia (ordenados por hora)
     * @return ResultadoValidacao com o resultado da validação
     */
    fun validarSequencia(
        novoPonto: Ponto,
        pontosExistentes: List<Ponto>
    ): ResultadoValidacao {
        val inconsistencias = mutableListOf<InconsistenciaDetectada>()

        // Se não há pontos no dia, o primeiro deve ser ENTRADA
        if (pontosExistentes.isEmpty()) {
            if (novoPonto.tipo == TipoPonto.SAIDA) {
                inconsistencias.add(
                    InconsistenciaDetectada(
                        inconsistencia = Inconsistencia.SAIDA_SEM_ENTRADA,
                        detalhes = "Não há entrada registrada para este dia"
                    )
                )
            }
            return criarResultado(novoPonto, inconsistencias)
        }

        // Encontrar a posição onde o novo ponto seria inserido
        val pontosComNovo = (pontosExistentes + novoPonto).sortedBy { it.dataHora }
        val posicaoNovoPonto = pontosComNovo.indexOf(novoPonto)

        // Ponto anterior (se existir)
        val pontoAnterior = if (posicaoNovoPonto > 0) {
            pontosComNovo[posicaoNovoPonto - 1]
        } else null

        // Ponto posterior (se existir)
        val pontoPosterior = if (posicaoNovoPonto < pontosComNovo.size - 1) {
            pontosComNovo[posicaoNovoPonto + 1]
        } else null

        // Validar sequência com ponto anterior
        if (pontoAnterior != null) {
            when {
                // Duas entradas consecutivas
                pontoAnterior.tipo == TipoPonto.ENTRADA && novoPonto.tipo == TipoPonto.ENTRADA -> {
                    inconsistencias.add(
                        InconsistenciaDetectada(
                            inconsistencia = Inconsistencia.ENTRADA_DUPLICADA,
                            detalhes = "Já existe entrada às ${pontoAnterior.horaFormatada}",
                            pontoRelacionadoId = pontoAnterior.id
                        )
                    )
                }
                // Duas saídas consecutivas
                pontoAnterior.tipo == TipoPonto.SAIDA && novoPonto.tipo == TipoPonto.SAIDA -> {
                    inconsistencias.add(
                        InconsistenciaDetectada(
                            inconsistencia = Inconsistencia.SAIDA_DUPLICADA,
                            detalhes = "Já existe saída às ${pontoAnterior.horaFormatada}",
                            pontoRelacionadoId = pontoAnterior.id
                        )
                    )
                }
            }
        } else {
            // Não há ponto anterior - novo ponto será o primeiro
            if (novoPonto.tipo == TipoPonto.SAIDA) {
                inconsistencias.add(
                    InconsistenciaDetectada(
                        inconsistencia = Inconsistencia.SAIDA_SEM_ENTRADA,
                        detalhes = "Saída registrada antes de qualquer entrada"
                    )
                )
            }
        }

        // Validar sequência com ponto posterior
        if (pontoPosterior != null) {
            when {
                // Nova entrada seguida de entrada existente
                novoPonto.tipo == TipoPonto.ENTRADA && pontoPosterior.tipo == TipoPonto.ENTRADA -> {
                    inconsistencias.add(
                        InconsistenciaDetectada(
                            inconsistencia = Inconsistencia.ENTRADA_DUPLICADA,
                            detalhes = "Já existe entrada às ${pontoPosterior.horaFormatada}",
                            pontoRelacionadoId = pontoPosterior.id
                        )
                    )
                }
                // Nova saída seguida de saída existente
                novoPonto.tipo == TipoPonto.SAIDA && pontoPosterior.tipo == TipoPonto.SAIDA -> {
                    inconsistencias.add(
                        InconsistenciaDetectada(
                            inconsistencia = Inconsistencia.SAIDA_DUPLICADA,
                            detalhes = "Já existe saída às ${pontoPosterior.horaFormatada}",
                            pontoRelacionadoId = pontoPosterior.id
                        )
                    )
                }
            }
        }

        return criarResultado(novoPonto, inconsistencias)
    }

    /**
     * Valida se a sequência do dia está completa (sem entradas abertas).
     *
     * @param empregoId ID do emprego
     * @param data Data para validar
     * @return Lista de inconsistências encontradas
     */
    suspend fun validarSequenciaDia(
        empregoId: Long,
        data: LocalDate
    ): List<InconsistenciaDetectada> {
        val pontosDoDia = pontoRepository.buscarPorEmpregoEData(empregoId, data)
            .sortedBy { it.dataHora }

        val inconsistencias = mutableListOf<InconsistenciaDetectada>()

        // Verificar número ímpar de registros
        if (pontosDoDia.size % 2 != 0) {
            inconsistencias.add(
                InconsistenciaDetectada(
                    inconsistencia = Inconsistencia.REGISTROS_IMPARES,
                    detalhes = "${pontosDoDia.size} registro(s) - falta entrada ou saída"
                )
            )
        }

        // Verificar sequência de tipos
        pontosDoDia.forEachIndexed { index, ponto ->
            val tipoEsperado = if (index % 2 == 0) TipoPonto.ENTRADA else TipoPonto.SAIDA
            if (ponto.tipo != tipoEsperado) {
                val inconsistencia = if (ponto.tipo == TipoPonto.ENTRADA) {
                    Inconsistencia.ENTRADA_DUPLICADA
                } else {
                    Inconsistencia.SAIDA_DUPLICADA
                }
                inconsistencias.add(
                    InconsistenciaDetectada(
                        inconsistencia = inconsistencia,
                        detalhes = "Posição ${index + 1}: esperado ${tipoEsperado.descricao}, encontrado ${ponto.tipo.descricao}",
                        pontoRelacionadoId = ponto.id
                    )
                )
            }
        }

        // Verificar se último ponto é saída (dia fechado)
        val ultimoPonto = pontosDoDia.lastOrNull()
        if (ultimoPonto != null && ultimoPonto.tipo == TipoPonto.ENTRADA) {
            inconsistencias.add(
                InconsistenciaDetectada(
                    inconsistencia = Inconsistencia.ENTRADA_SEM_SAIDA,
                    detalhes = "Entrada às ${ultimoPonto.horaFormatada} sem saída correspondente",
                    pontoRelacionadoId = ultimoPonto.id
                )
            )
        }

        return inconsistencias.distinctBy { it.inconsistencia to it.pontoRelacionadoId }
    }

    private fun criarResultado(
        ponto: Ponto,
        inconsistencias: List<InconsistenciaDetectada>
    ): ResultadoValidacao {
        val temBloqueantes = inconsistencias.any { it.isBloqueante }
        return if (temBloqueantes) {
            ResultadoValidacao.falha(inconsistencias)
        } else {
            ResultadoValidacao.sucesso(ponto, inconsistencias)
        }
    }
}
