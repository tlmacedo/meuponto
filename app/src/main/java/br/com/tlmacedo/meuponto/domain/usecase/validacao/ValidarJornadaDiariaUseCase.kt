// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/validacao/ValidarJornadaDiariaUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.validacao

import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import br.com.tlmacedo.meuponto.domain.model.HorarioDiaSemana
import br.com.tlmacedo.meuponto.domain.model.Inconsistencia
import br.com.tlmacedo.meuponto.domain.model.InconsistenciaDetectada
import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.model.ResultadoValidacao
import br.com.tlmacedo.meuponto.domain.model.TipoPonto
import br.com.tlmacedo.meuponto.domain.repository.PontoRepository
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use Case responsável por validar a jornada diária de trabalho.
 *
 * Verifica se a jornada está dentro dos limites legais e configurados,
 * considerando:
 * - Jornada máxima diária (CLT: 10h com horas extras)
 * - Intervalos obrigatórios
 * - Carga horária esperada
 *
 * @property pontoRepository Repositório para consulta de pontos
 *
 * @author Thiago
 * @since 2.0.0
 */
class ValidarJornadaDiariaUseCase @Inject constructor(
    private val pontoRepository: PontoRepository
) {
    companion object {
        /** Jornada máxima legal em minutos (10 horas) */
        private const val JORNADA_MAXIMA_LEGAL_MINUTOS = 600

        /** Intervalo mínimo obrigatório em minutos para jornada > 6h */
        private const val INTERVALO_MINIMO_JORNADA_LONGA = 60

        /** Limite de jornada para obrigatoriedade de intervalo (6h) */
        private const val LIMITE_JORNADA_INTERVALO_MINUTOS = 360
    }

    /**
     * Valida a jornada diária após incluir um novo ponto.
     *
     * @param empregoId ID do emprego
     * @param novoPonto Ponto a ser adicionado
     * @param configuracao Configuração do emprego
     * @param horarioEsperado Configuração de horário do dia (opcional)
     * @return ResultadoValidacao com o resultado da validação
     */
    suspend operator fun invoke(
        empregoId: Long,
        novoPonto: Ponto,
        configuracao: ConfiguracaoEmprego,
        horarioEsperado: HorarioDiaSemana? = null
    ): ResultadoValidacao {
        val data = novoPonto.dataHora.toLocalDate()
        val pontosExistentes = pontoRepository.buscarPorEmpregoEData(empregoId, data)
            .sortedBy { it.dataHora }

        // Simular adição do novo ponto
        val todosPontos = (pontosExistentes + novoPonto).sortedBy { it.dataHora }

        return validarJornada(todosPontos, configuracao, horarioEsperado)
    }

    /**
     * Valida a jornada de um dia específico.
     *
     * @param empregoId ID do emprego
     * @param data Data para validar
     * @param configuracao Configuração do emprego
     * @param horarioEsperado Configuração de horário do dia (opcional)
     * @return ResultadoValidacao com o resultado da validação
     */
    suspend fun validarDia(
        empregoId: Long,
        data: LocalDate,
        configuracao: ConfiguracaoEmprego,
        horarioEsperado: HorarioDiaSemana? = null
    ): ResultadoValidacao {
        val pontos = pontoRepository.buscarPorEmpregoEData(empregoId, data)
            .sortedBy { it.dataHora }

        return validarJornada(pontos, configuracao, horarioEsperado)
    }

    /**
     * Valida a jornada com base na lista de pontos.
     */
    private fun validarJornada(
        pontos: List<Ponto>,
        configuracao: ConfiguracaoEmprego,
        horarioEsperado: HorarioDiaSemana?
    ): ResultadoValidacao {
        if (pontos.isEmpty()) {
            return ResultadoValidacao.sucesso(
                ponto = pontos.firstOrNull() ?: return ResultadoValidacao(isValido = true)
            )
        }

        val inconsistencias = mutableListOf<InconsistenciaDetectada>()

        // Calcular tempo trabalhado
        val tempoTrabalhado = calcularTempoTrabalhado(pontos)

        // Validar jornada máxima
        val jornadaMaxima = configuracao.jornadaMaximaDiariaMinutos
        if (tempoTrabalhado > jornadaMaxima) {
            inconsistencias.add(
                InconsistenciaDetectada(
                    inconsistencia = Inconsistencia.JORNADA_EXCEDIDA,
                    detalhes = buildString {
                        append("Jornada de ${formatarDuracao(tempoTrabalhado)} ")
                        append("excede limite de ${formatarDuracao(jornadaMaxima.toLong())}")
                    }
                )
            )
        }

        // Validar limite legal
        if (tempoTrabalhado > JORNADA_MAXIMA_LEGAL_MINUTOS) {
            inconsistencias.add(
                InconsistenciaDetectada(
                    inconsistencia = Inconsistencia.JORNADA_EXCEDIDA,
                    detalhes = "Jornada excede limite legal de 10 horas"
                )
            )
        }

        // Validar intervalo obrigatório para jornadas longas
        if (tempoTrabalhado > LIMITE_JORNADA_INTERVALO_MINUTOS) {
            val intervaloRealizado = calcularIntervaloRealizado(pontos)
            val intervaloMinimo = horarioEsperado?.intervaloMinimoMinutos
                ?: INTERVALO_MINIMO_JORNADA_LONGA

            if (intervaloRealizado < intervaloMinimo) {
                inconsistencias.add(
                    InconsistenciaDetectada(
                        inconsistencia = Inconsistencia.INTERVALO_ALMOCO_INSUFICIENTE,
                        detalhes = buildString {
                            append("Jornada de ${formatarDuracao(tempoTrabalhado)} ")
                            append("requer intervalo mínimo de ${intervaloMinimo}min, ")
                            append("realizado: ${intervaloRealizado}min")
                        }
                    )
                )
            }
        }

        val ultimoPonto = pontos.lastOrNull()
        return if (inconsistencias.any { it.isBloqueante }) {
            ResultadoValidacao.falha(inconsistencias)
        } else {
            ResultadoValidacao.sucesso(
                ponto = ultimoPonto ?: pontos.first(),
                alertas = inconsistencias
            )
        }
    }

    /**
     * Calcula o tempo total trabalhado em minutos.
     */
    private fun calcularTempoTrabalhado(pontos: List<Ponto>): Long {
        var total = 0L
        val pontosOrdenados = pontos.sortedBy { it.dataHora }

        var i = 0
        while (i < pontosOrdenados.size - 1) {
            val atual = pontosOrdenados[i]
            val proximo = pontosOrdenados[i + 1]

            // Par entrada/saída
            if (atual.tipo == TipoPonto.ENTRADA && proximo.tipo == TipoPonto.SAIDA) {
                total += Duration.between(atual.dataHora, proximo.dataHora).toMinutes()
                i += 2
            } else {
                i++
            }
        }

        return total
    }

    /**
     * Calcula o intervalo realizado (tempo entre saída e entrada no meio do expediente).
     */
    private fun calcularIntervaloRealizado(pontos: List<Ponto>): Long {
        if (pontos.size < 4) return 0 // Precisa de pelo menos 4 pontos para ter intervalo

        val pontosOrdenados = pontos.sortedBy { it.dataHora }
        var maiorIntervalo = 0L

        var i = 0
        while (i < pontosOrdenados.size - 1) {
            val atual = pontosOrdenados[i]
            val proximo = pontosOrdenados[i + 1]

            // Intervalo = saída seguida de entrada
            if (atual.tipo == TipoPonto.SAIDA && proximo.tipo == TipoPonto.ENTRADA) {
                val intervalo = Duration.between(atual.dataHora, proximo.dataHora).toMinutes()
                if (intervalo > maiorIntervalo) {
                    maiorIntervalo = intervalo
                }
            }
            i++
        }

        return maiorIntervalo
    }

    /**
     * Calcula estatísticas da jornada do dia.
     *
     * @param empregoId ID do emprego
     * @param data Data para calcular
     * @return Estatísticas da jornada
     */
    suspend fun calcularEstatisticas(
        empregoId: Long,
        data: LocalDate
    ): EstatisticasJornada {
        val pontos = pontoRepository.buscarPorEmpregoEData(empregoId, data)
            .sortedBy { it.dataHora }

        return EstatisticasJornada(
            tempoTrabalhadoMinutos = calcularTempoTrabalhado(pontos),
            intervaloRealizadoMinutos = calcularIntervaloRealizado(pontos),
            quantidadePontos = pontos.size,
            primeiraEntrada = pontos.firstOrNull { it.isEntrada }?.dataHora,
            ultimaSaida = pontos.lastOrNull { it.isSaida }?.dataHora,
            jornadaCompleta = pontos.size >= 2 && pontos.size % 2 == 0
        )
    }

    private fun formatarDuracao(minutos: Long): String {
        val horas = minutos / 60
        val mins = minutos % 60
        return "${horas}h${mins}min"
    }
}

/**
 * Estatísticas calculadas da jornada de um dia.
 */
data class EstatisticasJornada(
    val tempoTrabalhadoMinutos: Long,
    val intervaloRealizadoMinutos: Long,
    val quantidadePontos: Int,
    val primeiraEntrada: java.time.LocalDateTime?,
    val ultimaSaida: java.time.LocalDateTime?,
    val jornadaCompleta: Boolean
) {
    /**
     * Tempo trabalhado formatado (ex: "8h12min").
     */
    val tempoTrabalhadoFormatado: String
        get() {
            val horas = tempoTrabalhadoMinutos / 60
            val mins = tempoTrabalhadoMinutos % 60
            return "${horas}h${mins}min"
        }

    /**
     * Intervalo formatado (ex: "1h00min").
     */
    val intervaloFormatado: String
        get() {
            val horas = intervaloRealizadoMinutos / 60
            val mins = intervaloRealizadoMinutos % 60
            return "${horas}h${mins}min"
        }
}
