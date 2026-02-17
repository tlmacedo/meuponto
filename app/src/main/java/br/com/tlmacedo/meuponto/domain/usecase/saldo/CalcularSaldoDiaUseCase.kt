// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/saldo/CalcularSaldoDiaUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.saldo

import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.repository.PontoRepository
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject

/**
 * Caso de uso para calcular saldo de um dia.
 *
 * IMPORTANTE: Utiliza horaEfetiva (horaConsiderada se existir, senão hora original)
 * para considerar tolerâncias aplicadas nos intervalos.
 *
 * @author Thiago
 * @since 1.0.0
 * @updated 2.1.0 - Tipo calculado por posição (índice par = entrada)
 * @updated 2.6.0 - Usa horaEfetiva para considerar tolerâncias
 */
class CalcularSaldoDiaUseCase @Inject constructor(
    private val pontoRepository: PontoRepository
) {
    data class SaldoDia(
        val data: LocalDate,
        val trabalhadoMinutos: Long,
        val esperadoMinutos: Long,
        val saldoMinutos: Long,
        val intervaloRealMinutos: Long,
        val intervaloConsideradoMinutos: Long,
        val isDiaUtil: Boolean,
        val temAjusteTolerancia: Boolean
    ) {
        val saldoFormatado: String
            get() {
                val horas = kotlin.math.abs(saldoMinutos) / 60
                val minutos = kotlin.math.abs(saldoMinutos) % 60
                val sinal = if (saldoMinutos >= 0) "+" else "-"
                return "$sinal${horas}h${minutos}min"
            }

        val trabalhadoFormatado: String
            get() {
                val horas = trabalhadoMinutos / 60
                val minutos = trabalhadoMinutos % 60
                return "${horas}h${minutos}min"
            }

        val intervaloRealFormatado: String
            get() {
                val horas = intervaloRealMinutos / 60
                val minutos = intervaloRealMinutos % 60
                return "${horas}h${minutos}min"
            }

        val intervaloConsideradoFormatado: String
            get() {
                val horas = intervaloConsideradoMinutos / 60
                val minutos = intervaloConsideradoMinutos % 60
                return "${horas}h${minutos}min"
            }

        // Retrocompatibilidade
        val intervaloMinutos: Long get() = intervaloRealMinutos
    }

    /**
     * Calcula saldo buscando pontos do repositório.
     */
    suspend operator fun invoke(
        empregoId: Long,
        data: LocalDate,
        cargaHorariaDiariaMinutos: Long = 480L
    ): SaldoDia {
        val pontos = pontoRepository.buscarPorEmpregoEData(empregoId, data)
        return calcular(pontos, data, cargaHorariaDiariaMinutos)
    }

    /**
     * Calcula saldo a partir de uma lista de pontos.
     */
    fun calcularComPontos(
        pontos: List<Ponto>,
        cargaHorariaDiariaMinutos: Long = 480L
    ): SaldoDia {
        val data = pontos.firstOrNull()?.dataHora?.toLocalDate() ?: LocalDate.now()
        return calcular(pontos, data, cargaHorariaDiariaMinutos)
    }

    private fun calcular(
        pontos: List<Ponto>,
        data: LocalDate,
        cargaHorariaDiariaMinutos: Long
    ): SaldoDia {
        val trabalhado = calcularTempoTrabalhado(pontos)
        val intervaloReal = calcularIntervaloReal(pontos)
        val intervaloConsiderado = calcularIntervaloConsiderado(pontos)
        val isDiaUtil = isDiaUtil(data)
        val esperado = if (isDiaUtil) cargaHorariaDiariaMinutos else 0L
        val temAjuste = pontos.any { it.temAjusteTolerancia }

        return SaldoDia(
            data = data,
            trabalhadoMinutos = trabalhado,
            esperadoMinutos = esperado,
            saldoMinutos = trabalhado - esperado,
            intervaloRealMinutos = intervaloReal,
            intervaloConsideradoMinutos = intervaloConsiderado,
            isDiaUtil = isDiaUtil,
            temAjusteTolerancia = temAjuste
        )
    }

    private fun isDiaUtil(data: LocalDate): Boolean {
        return data.dayOfWeek !in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    }

    /**
     * Calcula o tempo trabalhado usando horaEfetiva.
     */
    private fun calcularTempoTrabalhado(pontos: List<Ponto>): Long {
        if (pontos.size < 2) return 0L

        val ordenados = pontos.sortedBy { it.dataHora }
        var totalMinutos = 0L
        var i = 0

        while (i < ordenados.size - 1) {
            val entrada = ordenados[i]
            val saida = ordenados[i + 1]
            // Usar horaEfetiva (considera tolerância)
            totalMinutos += Duration.between(entrada.horaEfetiva, saida.horaEfetiva).toMinutes()
            i += 2
        }

        return totalMinutos
    }

    /**
     * Calcula o intervalo REAL (usando hora original, sem considerar tolerância).
     */
    private fun calcularIntervaloReal(pontos: List<Ponto>): Long {
        if (pontos.size < 4) return 0L

        val ordenados = pontos.sortedBy { it.dataHora }
        var totalIntervalo = 0L
        var i = 1

        while (i < ordenados.size - 1) {
            val saida = ordenados[i]
            val entrada = ordenados[i + 1]
            // Usar hora original para mostrar o intervalo real
            totalIntervalo += Duration.between(saida.hora, entrada.hora).toMinutes()
            i += 2
        }

        return totalIntervalo
    }

    /**
     * Calcula o intervalo CONSIDERADO (usando horaEfetiva, com tolerância aplicada).
     */
    private fun calcularIntervaloConsiderado(pontos: List<Ponto>): Long {
        if (pontos.size < 4) return 0L

        val ordenados = pontos.sortedBy { it.dataHora }
        var totalIntervalo = 0L
        var i = 1

        while (i < ordenados.size - 1) {
            val saida = ordenados[i]
            val entrada = ordenados[i + 1]
            // Usar horaEfetiva para o cálculo considerado
            totalIntervalo += Duration.between(saida.horaEfetiva, entrada.horaEfetiva).toMinutes()
            i += 2
        }

        return totalIntervalo
    }
}
