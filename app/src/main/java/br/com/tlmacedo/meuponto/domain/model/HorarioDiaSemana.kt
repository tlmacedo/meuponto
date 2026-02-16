// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/HorarioDiaSemana.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Modelo de domínio que representa a configuração de horários para um dia da semana.
 *
 * Permite configurar horários ideais, carga horária e tolerâncias específicas
 * para cada dia da semana de forma independente por emprego.
 *
 * As tolerâncias específicas do dia (quando não nulas) sobrescrevem as
 * tolerâncias globais definidas em [ConfiguracaoEmprego].
 *
 * @author Thiago
 * @since 2.0.0
 * @updated 2.1.0 - Adicionadas tolerâncias específicas por dia
 * @updated 2.3.0 - Restauradas tolerâncias de entrada e saída
 * @updated 2.3.1 - Removida tolerância de redução de intervalo
 */
data class HorarioDiaSemana(
    val id: Long = 0,
    val empregoId: Long,
    val diaSemana: DiaSemana,
    val ativo: Boolean = true,
    
    // Carga Horária
    val cargaHorariaMinutos: Int = 492,
    
    // Horários Ideais (nullable = não configurado)
    val entradaIdeal: LocalTime? = null,
    val saidaIntervaloIdeal: LocalTime? = null,
    val voltaIntervaloIdeal: LocalTime? = null,
    val saidaIdeal: LocalTime? = null,
    
    // Intervalo
    val intervaloMinimoMinutos: Int = 60,
    val toleranciaIntervaloMaisMinutos: Int = 0,

    // Tolerâncias Específicas do Dia (null = usa global de ConfiguracaoEmprego)
    val toleranciaEntradaMinutos: Int? = null,
    val toleranciaSaidaMinutos: Int? = null,
    
    // Auditoria
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        private val FORMATTER_HORA = DateTimeFormatter.ofPattern("HH:mm")

        fun criarPadrao(empregoId: Long, diaSemana: DiaSemana): HorarioDiaSemana {
            val ehDiaUtil = diaSemana.isDiaUtil
            return HorarioDiaSemana(
                empregoId = empregoId,
                diaSemana = diaSemana,
                ativo = ehDiaUtil,
                cargaHorariaMinutos = if (ehDiaUtil) 492 else 0
            )
        }

        fun criarTodosPadrao(empregoId: Long): List<HorarioDiaSemana> {
            return DiaSemana.entries.map { dia -> criarPadrao(empregoId, dia) }
        }
    }

    /** Verifica se tem horário ideal de entrada configurado */
    val temHorarioIdeal: Boolean
        get() = entradaIdeal != null

    /** Verifica se tem pelo menos um horário ideal configurado */
    val temHorariosIdeais: Boolean
        get() = entradaIdeal != null || saidaIntervaloIdeal != null ||
                voltaIntervaloIdeal != null || saidaIdeal != null

    /** Verifica se tem todos os horários ideais configurados */
    val temHorariosCompletos: Boolean
        get() = entradaIdeal != null && saidaIntervaloIdeal != null &&
                voltaIntervaloIdeal != null && saidaIdeal != null

    /** Verifica se o dia é dia útil (ativo) */
    val isDiaUtil: Boolean
        get() = ativo

    /**
     * Verifica se tem tolerância de entrada customizada (específica do dia).
     */
    val temToleranciaEntradaCustomizada: Boolean
        get() = toleranciaEntradaMinutos != null

    /**
     * Verifica se tem tolerância de saída customizada (específica do dia).
     */
    val temToleranciaSaidaCustomizada: Boolean
        get() = toleranciaSaidaMinutos != null

    /**
     * Verifica se tem alguma tolerância específica configurada.
     */
    val temToleranciasCustomizadas: Boolean
        get() = temToleranciaEntradaCustomizada || temToleranciaSaidaCustomizada

    /** Carga horária formatada (ex: "08:12") */
    val cargaHorariaFormatada: String
        get() {
            val horas = cargaHorariaMinutos / 60
            val minutos = cargaHorariaMinutos % 60
            return String.format("%02d:%02d", horas, minutos)
        }

    /** Intervalo mínimo formatado (ex: "01:00") */
    val intervaloMinimoFormatado: String
        get() {
            val horas = intervaloMinimoMinutos / 60
            val minutos = intervaloMinimoMinutos % 60
            return String.format("%02d:%02d", horas, minutos)
        }

    /** Duração esperada do intervalo em minutos */
    val duracaoIntervaloIdealMinutos: Int?
        get() {
            if (saidaIntervaloIdeal != null && voltaIntervaloIdeal != null) {
                return Duration.between(saidaIntervaloIdeal, voltaIntervaloIdeal)
                    .toMinutes().toInt()
            }
            return null
        }

    /** Resumo dos horários ideais para exibição */
    val resumoHorariosIdeais: String
        get() = buildString {
            append(entradaIdeal?.format(FORMATTER_HORA) ?: "--:--")
            append(" → ")
            append(saidaIdeal?.format(FORMATTER_HORA) ?: "--:--")
        }

    /** Descrição completa do dia para exibição */
    val descricaoCompleta: String
        get() = buildString {
            append(diaSemana.descricao)
            if (!ativo) {
                append(" (Folga)")
            } else {
                append(" - $cargaHorariaFormatada")
                if (temHorarioIdeal) {
                    append(" ($resumoHorariosIdeais)")
                }
            }
        }
}
