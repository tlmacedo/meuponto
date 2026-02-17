// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/ponto/CalcularResumoDiaUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.ponto

import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.model.ResumoDia
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Caso de uso para calcular o resumo de um dia de trabalho.
 *
 * Calcula as horas trabalhadas considerando pares de entrada/saída
 * baseado na posição dos pontos ordenados por dataHora.
 *
 * IMPORTANTE: Utiliza horaEfetiva (horaConsiderada se existir, senão dataHora)
 * para considerar tolerâncias aplicadas.
 *
 * @author Thiago
 * @since 1.0.0
 * @updated 2.1.0 - Simplificado (tipo calculado por posição)
 * @updated 2.6.0 - Usa horaEfetiva para considerar tolerâncias
 */
class CalcularResumoDiaUseCase @Inject constructor() {

    operator fun invoke(
        pontos: List<Ponto>,
        data: LocalDate = LocalDate.now(),
        cargaHorariaDiariaMinutos: Int = 480
    ): ResumoDia {
        val pontosOrdenados = pontos.sortedBy { it.dataHora }
        val totalTrabalhado = calcularTempoTrabalhado(pontosOrdenados)

        return ResumoDia(
            data = data,
            pontos = pontosOrdenados,
            horasTrabalhadas = totalTrabalhado,
            cargaHorariaDiaria = Duration.ofMinutes(cargaHorariaDiariaMinutos.toLong())
        )
    }

    /**
     * Calcula o tempo trabalhado usando horaEfetiva de cada ponto.
     * horaEfetiva retorna horaConsiderada se existir (tolerância aplicada),
     * caso contrário retorna a hora original.
     */
    private fun calcularTempoTrabalhado(pontosOrdenados: List<Ponto>): Duration {
        if (pontosOrdenados.size < 2) return Duration.ZERO

        var totalTrabalhado = Duration.ZERO
        var i = 0

        while (i < pontosOrdenados.size - 1) {
            val entrada = pontosOrdenados[i]
            val saida = pontosOrdenados[i + 1]

            // Usar horaEfetiva (considera horaConsiderada se existir)
            val horaEntrada = obterDataHoraEfetiva(entrada)
            val horaSaida = obterDataHoraEfetiva(saida)

            totalTrabalhado = totalTrabalhado.plus(
                Duration.between(horaEntrada, horaSaida)
            )
            i += 2
        }

        return totalTrabalhado
    }

    /**
     * Obtém o LocalDateTime efetivo do ponto.
     * Se horaConsiderada existir, usa ela; senão usa dataHora original.
     */
    private fun obterDataHoraEfetiva(ponto: Ponto): LocalDateTime {
        return ponto.horaConsiderada ?: ponto.dataHora
    }
}
