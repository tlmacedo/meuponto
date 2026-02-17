// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/VersaoJornada.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Modelo de domínio que representa uma versão de jornada de trabalho.
 *
 * @author Thiago
 * @since 2.7.0
 */
data class VersaoJornada(
    val id: Long = 0,
    val empregoId: Long,
    val dataInicio: LocalDate,
    val dataFim: LocalDate? = null,
    val descricao: String? = null,
    val numeroVersao: Int = 1,
    val vigente: Boolean = true,
    val jornadaMaximaDiariaMinutos: Int = 600,
    val intervaloMinimoInterjornadaMinutos: Int = 660,
    val toleranciaIntervaloMaisMinutos: Int = 0,
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        private val FORMATTER_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }

    fun contemData(data: LocalDate): Boolean {
        val aposInicio = !data.isBefore(dataInicio)
        val antesFim = dataFim == null || !data.isAfter(dataFim)
        return aposInicio && antesFim
    }

    val periodoFormatado: String
        get() {
            val inicio = dataInicio.format(FORMATTER_DATA)
            return if (dataFim != null) {
                "$inicio até ${dataFim.format(FORMATTER_DATA)}"
            } else {
                "$inicio em diante"
            }
        }

    val titulo: String
        get() = if (descricao != null) "Versão $numeroVersao - $descricao" else "Versão $numeroVersao"

    val jornadaMaximaFormatada: String
        get() {
            val horas = jornadaMaximaDiariaMinutos / 60
            val minutos = jornadaMaximaDiariaMinutos % 60
            return String.format("%02d:%02d", horas, minutos)
        }

    val intervaloInterjornadaFormatado: String
        get() {
            val horas = intervaloMinimoInterjornadaMinutos / 60
            val minutos = intervaloMinimoInterjornadaMinutos % 60
            return String.format("%02d:%02d", horas, minutos)
        }
}
