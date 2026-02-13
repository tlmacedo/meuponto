// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/AjusteSaldo.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.abs

/**
 * Modelo de domínio que representa um ajuste manual no banco de horas.
 *
 * Permite adicionar ou subtrair minutos do saldo de forma controlada,
 * com justificativa obrigatória para auditoria.
 *
 * @property id Identificador único do ajuste
 * @property empregoId FK para o emprego associado
 * @property data Data à qual o ajuste está vinculado
 * @property minutos Quantidade de minutos a ajustar (positivo = adicionar, negativo = subtrair)
 * @property justificativa Justificativa obrigatória para o ajuste
 * @property criadoEm Timestamp de criação
 *
 * @author Thiago
 * @since 2.0.0
 */
data class AjusteSaldo(
    val id: Long = 0,
    val empregoId: Long,
    val data: LocalDate,
    val minutos: Int,
    val justificativa: String,
    val criadoEm: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Verifica se é um ajuste positivo (adiciona horas).
     */
    val isPositivo: Boolean
        get() = minutos > 0

    /**
     * Verifica se é um ajuste negativo (subtrai horas).
     */
    val isNegativo: Boolean
        get() = minutos < 0

    /**
     * Retorna o ajuste formatado com sinal (ex: "+01:30" ou "-00:45").
     */
    val minutosFormatados: String
        get() {
            val sinal = if (minutos >= 0) "+" else "-"
            val totalMinutos = abs(minutos)
            val horas = totalMinutos / 60
            val mins = totalMinutos % 60
            return "$sinal${String.format("%02d:%02d", horas, mins)}"
        }
}
