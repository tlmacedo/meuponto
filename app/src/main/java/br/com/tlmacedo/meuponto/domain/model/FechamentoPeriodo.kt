// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/FechamentoPeriodo.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.abs

/**
 * Modelo de domínio que representa um fechamento de período (zerar saldo).
 *
 * Quando um período é fechado, o saldo anterior é registrado e zerado,
 * criando um marco para cálculos futuros.
 *
 * @property id Identificador único do fechamento
 * @property empregoId FK para o emprego associado
 * @property dataFechamento Data em que o fechamento foi realizado
 * @property dataInicioPeriodo Data de início do período fechado
 * @property dataFimPeriodo Data de fim do período fechado
 * @property saldoAnteriorMinutos Saldo em minutos antes do fechamento
 * @property tipo Tipo de fechamento (SEMANAL, MENSAL, BANCO_HORAS)
 * @property observacao Observação opcional
 * @property criadoEm Timestamp de criação
 *
 * @author Thiago
 * @since 2.0.0
 */
data class FechamentoPeriodo(
    val id: Long = 0,
    val empregoId: Long,
    val dataFechamento: LocalDate,
    val dataInicioPeriodo: LocalDate,
    val dataFimPeriodo: LocalDate,
    val saldoAnteriorMinutos: Int,
    val tipo: TipoFechamento,
    val observacao: String? = null,
    val criadoEm: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Retorna o saldo anterior formatado (ex: "+05:30" ou "-02:15").
     */
    val saldoAnteriorFormatado: String
        get() {
            val sinal = if (saldoAnteriorMinutos >= 0) "+" else "-"
            val totalMinutos = abs(saldoAnteriorMinutos)
            val horas = totalMinutos / 60
            val mins = totalMinutos % 60
            return "$sinal${String.format("%02d:%02d", horas, mins)}"
        }

    /**
     * Verifica se o saldo era positivo no fechamento.
     */
    val saldoPositivo: Boolean
        get() = saldoAnteriorMinutos > 0

    /**
     * Verifica se o saldo era negativo no fechamento.
     */
    val saldoNegativo: Boolean
        get() = saldoAnteriorMinutos < 0
}
