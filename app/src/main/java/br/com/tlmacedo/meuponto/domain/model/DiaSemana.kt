// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/DiaSemana.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.DayOfWeek

/**
 * Enum que representa os dias da semana.
 *
 * Utilizado para configuração de horários e cálculos de jornada
 * independentes por dia da semana.
 *
 * @property descricao Nome completo do dia em português
 * @property descricaoCurta Abreviação de 3 letras
 * @property javaDayOfWeek Equivalente em java.time.DayOfWeek
 *
 * @author Thiago
 * @since 2.0.0
 * @updated 2.1.0 - Adicionado método fromJavaDayOfWeek e propriedades auxiliares
 */
enum class DiaSemana(
    val descricao: String,
    val descricaoCurta: String,
    val javaDayOfWeek: DayOfWeek
) {
    DOMINGO("Domingo", "Dom", DayOfWeek.SUNDAY),
    SEGUNDA("Segunda-feira", "Seg", DayOfWeek.MONDAY),
    TERCA("Terça-feira", "Ter", DayOfWeek.TUESDAY),
    QUARTA("Quarta-feira", "Qua", DayOfWeek.WEDNESDAY),
    QUINTA("Quinta-feira", "Qui", DayOfWeek.THURSDAY),
    SEXTA("Sexta-feira", "Sex", DayOfWeek.FRIDAY),
    SABADO("Sábado", "Sáb", DayOfWeek.SATURDAY);

    companion object {
        /**
         * Converte java.time.DayOfWeek para DiaSemana.
         *
         * @param dayOfWeek Dia da semana do Java
         * @return DiaSemana correspondente
         */
        fun fromJavaDayOfWeek(dayOfWeek: DayOfWeek): DiaSemana {
            return entries.first { it.javaDayOfWeek == dayOfWeek }
        }

        /**
         * Alias para fromJavaDayOfWeek (compatibilidade).
         */
        fun fromDayOfWeek(dayOfWeek: DayOfWeek): DiaSemana = fromJavaDayOfWeek(dayOfWeek)

        /**
         * Retorna os dias úteis padrão (segunda a sexta).
         */
        val DIAS_UTEIS = listOf(SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA)

        /**
         * Retorna o fim de semana (sábado e domingo).
         */
        val FIM_DE_SEMANA = listOf(SABADO, DOMINGO)

        /**
         * Retorna todos os dias ordenados começando por domingo.
         */
        val TODOS_ORDENADOS = listOf(DOMINGO, SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO)
    }

    /**
     * Verifica se é dia útil (segunda a sexta).
     */
    val isDiaUtil: Boolean
        get() = this in DIAS_UTEIS

    /**
     * Verifica se é fim de semana.
     */
    val isFimDeSemana: Boolean
        get() = this in FIM_DE_SEMANA

    /**
     * Retorna o próximo dia da semana.
     */
    val proximoDia: DiaSemana
        get() = when (this) {
            DOMINGO -> SEGUNDA
            SEGUNDA -> TERCA
            TERCA -> QUARTA
            QUARTA -> QUINTA
            QUINTA -> SEXTA
            SEXTA -> SABADO
            SABADO -> DOMINGO
        }

    /**
     * Retorna o dia anterior da semana.
     */
    val diaAnterior: DiaSemana
        get() = when (this) {
            DOMINGO -> SABADO
            SEGUNDA -> DOMINGO
            TERCA -> SEGUNDA
            QUARTA -> TERCA
            QUINTA -> QUARTA
            SEXTA -> QUINTA
            SABADO -> SEXTA
        }
}
