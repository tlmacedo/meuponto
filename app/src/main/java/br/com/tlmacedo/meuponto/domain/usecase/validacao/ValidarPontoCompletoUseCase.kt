// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/validacao/ValidarPontoCompletoUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.validacao

import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import br.com.tlmacedo.meuponto.domain.model.HorarioDiaSemana
import br.com.tlmacedo.meuponto.domain.model.InconsistenciaDetectada
import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.model.ResultadoValidacao
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Use Case orquestrador que executa todas as validações de ponto.
 *
 * Consolida os resultados de múltiplos validadores em um único resultado,
 * facilitando o uso nas camadas superiores (ViewModel/UI).
 *
 * Validações executadas:
 * 1. Sequência de entrada/saída
 * 2. Horários (futuro, retroativo, esperado)
 * 3. Jornada diária (limites, intervalos)
 *
 * @property validarSequencia Validador de sequência
 * @property validarHorario Validador de horários
 * @property validarJornada Validador de jornada
 *
 * @author Thiago
 * @since 2.0.0
 */
class ValidarPontoCompletoUseCase @Inject constructor(
    private val validarSequencia: ValidarSequenciaPontoUseCase,
    private val validarHorario: ValidarHorarioPontoUseCase,
    private val validarJornada: ValidarJornadaDiariaUseCase
) {
    /**
     * Executa todas as validações para um novo registro de ponto.
     *
     * @param empregoId ID do emprego
     * @param ponto Ponto a ser validado
     * @param configuracao Configuração do emprego
     * @param horarioEsperado Configuração de horário do dia (opcional)
     * @param dataHoraAtual Data/hora atual para validações temporais
     * @return ResultadoValidacao consolidado de todas as validações
     */
    suspend operator fun invoke(
        empregoId: Long,
        ponto: Ponto,
        configuracao: ConfiguracaoEmprego,
        horarioEsperado: HorarioDiaSemana? = null,
        dataHoraAtual: LocalDateTime = LocalDateTime.now()
    ): ResultadoValidacao {
        val todasInconsistencias = mutableListOf<InconsistenciaDetectada>()
        val mensagens = mutableListOf<String>()

        // 1. Validar sequência
        val resultadoSequencia = validarSequencia(empregoId, ponto)
        todasInconsistencias.addAll(resultadoSequencia.inconsistencias)

        // Se sequência falhou criticamente, retornar imediatamente
        if (resultadoSequencia.temInconsistenciasBloqueantes) {
            return ResultadoValidacao.falha(
                inconsistencias = todasInconsistencias,
                mensagem = "Sequência de ponto inválida"
            )
        }

        // 2. Validar horário
        val resultadoHorario = validarHorario(
            ponto = ponto,
            horarioEsperado = horarioEsperado,
            configuracao = configuracao,
            dataHoraAtual = dataHoraAtual
        )
        todasInconsistencias.addAll(resultadoHorario.inconsistencias)

        // Se horário falhou criticamente (ex: futuro), retornar
        if (resultadoHorario.temInconsistenciasBloqueantes) {
            return ResultadoValidacao.falha(
                inconsistencias = todasInconsistencias,
                mensagem = "Horário do registro inválido"
            )
        }

        // 3. Validar jornada (apenas se ponto de saída para verificar totais)
        if (ponto.isSaida) {
            val resultadoJornada = validarJornada(
                empregoId = empregoId,
                novoPonto = ponto,
                configuracao = configuracao,
                horarioEsperado = horarioEsperado
            )
            todasInconsistencias.addAll(resultadoJornada.inconsistencias)
        }

        // Consolidar resultado
        val temBloqueantes = todasInconsistencias.any { it.isBloqueante }
        val temAlertas = todasInconsistencias.any { !it.isBloqueante }

        return if (temBloqueantes) {
            ResultadoValidacao.falha(
                inconsistencias = todasInconsistencias,
                mensagem = "Registro com inconsistências que impedem o salvamento"
            )
        } else {
            ResultadoValidacao(
                isValido = true,
                inconsistencias = todasInconsistencias,
                pontoValidado = ponto,
                mensagens = if (temAlertas) {
                    listOf("Registro válido com ${todasInconsistencias.size} alerta(s)")
                } else {
                    listOf("Registro válido")
                }
            )
        }
    }

    /**
     * Executa validação rápida apenas de sequência.
     * Útil para validação em tempo real durante digitação.
     *
     * @param empregoId ID do emprego
     * @param ponto Ponto a ser validado
     * @return ResultadoValidacao apenas da sequência
     */
    suspend fun validacaoRapida(
        empregoId: Long,
        ponto: Ponto
    ): ResultadoValidacao {
        return validarSequencia(empregoId, ponto)
    }

    /**
     * Valida um dia completo (para relatórios/fechamentos).
     *
     * @param empregoId ID do emprego
     * @param data Data para validar
     * @param configuracao Configuração do emprego
     * @param horarioEsperado Configuração de horário do dia
     * @return ResultadoValidacao do dia completo
     */
    suspend fun validarDiaCompleto(
        empregoId: Long,
        data: java.time.LocalDate,
        configuracao: ConfiguracaoEmprego,
        horarioEsperado: HorarioDiaSemana? = null
    ): ResultadoValidacao {
        val todasInconsistencias = mutableListOf<InconsistenciaDetectada>()

        // Validar sequência do dia
        val inconsistenciasSequencia = validarSequencia.validarSequenciaDia(empregoId, data)
        todasInconsistencias.addAll(inconsistenciasSequencia)

        // Validar jornada do dia
        val resultadoJornada = validarJornada.validarDia(
            empregoId = empregoId,
            data = data,
            configuracao = configuracao,
            horarioEsperado = horarioEsperado
        )
        todasInconsistencias.addAll(resultadoJornada.inconsistencias)

        val temBloqueantes = todasInconsistencias.any { it.isBloqueante }

        return ResultadoValidacao(
            isValido = !temBloqueantes,
            inconsistencias = todasInconsistencias.distinctBy { 
                it.inconsistencia to it.pontoRelacionadoId 
            },
            mensagens = listOf(
                if (temBloqueantes) {
                    "Dia com ${todasInconsistencias.count { it.isBloqueante }} inconsistência(s) crítica(s)"
                } else if (todasInconsistencias.isNotEmpty()) {
                    "Dia válido com ${todasInconsistencias.size} alerta(s)"
                } else {
                    "Dia sem inconsistências"
                }
            )
        )
    }
}
