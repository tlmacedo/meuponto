// Arquivo: ResumoDia.kt
package br.com.tlmacedo.meuponto.domain.model

import br.com.tlmacedo.meuponto.util.minutosParaDuracaoCompacta
import br.com.tlmacedo.meuponto.util.minutosParaIntervalo
import br.com.tlmacedo.meuponto.util.minutosParaTurno
import java.time.Duration
import java.time.LocalDate
import kotlin.math.abs

/**
 * Modelo que representa o resumo de um dia de trabalho.
 *
 * @author Thiago
 * @since 1.0.0
 * @updated 2.1.0 - Removida dependência de TipoPonto
 * @updated 2.4.0 - Adicionado cálculo de pausas entre turnos
 */
data class ResumoDia(
    val data: LocalDate,
    val pontos: List<Ponto> = emptyList(),
    val horasTrabalhadas: Duration = Duration.ZERO,
    val cargaHorariaDiaria: Duration = Duration.ofHours(8)
) {
    /** Saldo do dia (positivo = hora extra, negativo = deve horas) */
    val saldoDia: Duration
        get() = horasTrabalhadas.minus(cargaHorariaDiaria)

    /** Verifica se o dia tem saldo positivo */
    val temSaldoPositivo: Boolean
        get() = !saldoDia.isNegative && !saldoDia.isZero

    /** Verifica se o dia tem saldo negativo */
    val temSaldoNegativo: Boolean
        get() = saldoDia.isNegative

    /** Verifica se a jornada está completa (número par de pontos) */
    val jornadaCompleta: Boolean
        get() = pontos.isNotEmpty() && pontos.size % 2 == 0

    /** Próximo tipo de ponto esperado (true = entrada, false = saída) */
    val proximoIsEntrada: Boolean
        get() = proximoPontoIsEntrada(pontos.size)

    /** Descrição do próximo tipo esperado */
    val proximoTipoDescricao: String
        get() = proximoPontoDescricao(pontos.size)

    /** Lista de intervalos entre pontos de entrada e saída */
    val intervalos: List<IntervaloPonto>
        get() {
            val pontosOrdenados = pontos.sortedBy { it.dataHora }
            val lista = mutableListOf<IntervaloPonto>()

            var i = 0
            while (i < pontosOrdenados.size) {
                val entrada = pontosOrdenados.getOrNull(i)
                val saida = pontosOrdenados.getOrNull(i + 1)

                if (entrada != null) {
                    // Calcular pausa antes deste turno (tempo desde a saída anterior)
                    val pausaAntesMinutos = if (i >= 2) {
                        val saidaAnterior = pontosOrdenados.getOrNull(i - 1)
                        saidaAnterior?.let {
                            Duration.between(it.dataHora, entrada.dataHora).toMinutes().toInt()
                        }
                    } else null

                    lista.add(
                        IntervaloPonto(
                            entrada = entrada,
                            saida = saida,
                            duracao = saida?.let { Duration.between(entrada.dataHora, it.dataHora) },
                            pausaAntesMinutos = pausaAntesMinutos
                        )
                    )
                }
                i += 2
            }
            return lista
        }
}

/**
 * Representa um intervalo entre entrada e saída (turno de trabalho).
 *
 * @property entrada Ponto de entrada do turno
 * @property saida Ponto de saída do turno (null se ainda aberto)
 * @property duracao Duração do turno
 * @property pausaAntesMinutos Tempo de pausa antes deste turno (intervalo desde o turno anterior)
 *
 * @author Thiago
 * @since 1.0.0
 * @updated 2.4.0 - Adicionada pausaAntesMinutos e novos formatadores
 */
data class IntervaloPonto(
    val entrada: Ponto,
    val saida: Ponto?,
    val duracao: Duration?,
    val pausaAntesMinutos: Int? = null
) {
    /** Verifica se o intervalo está aberto (sem saída) */
    val aberto: Boolean get() = saida == null

    /** Duração em minutos */
    val duracaoMinutos: Int?
        get() = duracao?.toMinutes()?.toInt()

    /** Verifica se tem pausa antes (intervalo entre turnos) */
    val temPausaAntes: Boolean
        get() = pausaAntesMinutos != null && pausaAntesMinutos > 0

    /**
     * Formata a duração do turno.
     * @return String formatada (ex: "Turno de 05h 04m")
     */
    fun formatarDuracao(): String {
        return duracaoMinutos?.minutosParaTurno() ?: "Em andamento..."
    }

    /**
     * Formata a duração do turno de forma compacta.
     * @return String formatada (ex: "05h 04m")
     */
    fun formatarDuracaoCompacta(): String {
        return duracaoMinutos?.minutosParaDuracaoCompacta() ?: "..."
    }

    /**
     * Formata a pausa antes do turno (intervalo).
     * @return String formatada (ex: "Intervalo de 01h 14m") ou null se não houver pausa
     */
    fun formatarPausaAntes(): String? {
        return pausaAntesMinutos?.minutosParaIntervalo()
    }

    /**
     * Formata a pausa antes de forma compacta.
     * @return String formatada (ex: "01h 14m") ou null se não houver pausa
     */
    fun formatarPausaAntesCompacta(): String? {
        return pausaAntesMinutos?.minutosParaDuracaoCompacta()
    }
}
