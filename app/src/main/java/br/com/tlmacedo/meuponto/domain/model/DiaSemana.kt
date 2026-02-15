// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/DiaSemana.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.DayOfWeek

/**
 * Representação dos dias da semana com suporte a internacionalização.
 * 
 * Mapeia para [DayOfWeek] do Java Time API.
 *
 * @property dayOfWeek Dia da semana correspondente do Java Time
 * @property nomeResumido Abreviação do dia (3 letras)
 * @property nomeCompleto Nome completo do dia
 *
 * @author Thiago
 * @since 1.0.0
 */
enum class DiaSemana(
    val dayOfWeek: DayOfWeek,
    val nomeResumido: String,
    val nomeCompleto: String
) {
    SEGUNDA(DayOfWeek.MONDAY, "Seg", "Segunda-feira"),
    TERCA(DayOfWeek.TUESDAY, "Ter", "Terça-feira"),
    QUARTA(DayOfWeek.WEDNESDAY, "Qua", "Quarta-feira"),
    QUINTA(DayOfWeek.THURSDAY, "Qui", "Quinta-feira"),
    SEXTA(DayOfWeek.FRIDAY, "Sex", "Sexta-feira"),
    SABADO(DayOfWeek.SATURDAY, "Sáb", "Sábado"),
    DOMINGO(DayOfWeek.SUNDAY, "Dom", "Domingo");

    /**
     * Descrição do dia (alias para nomeCompleto).
     */
    val descricao: String
        get() = nomeCompleto

    companion object {
        /**
         * Converte DayOfWeek do Java para DiaSemana.
         *
         * @param dayOfWeek Dia da semana do Java Time
         * @return DiaSemana correspondente
         */
        fun fromDayOfWeek(dayOfWeek: DayOfWeek): DiaSemana {
            return entries.first { it.dayOfWeek == dayOfWeek }
        }
    }
}
