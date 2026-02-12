// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/Ponto.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Modelo de domínio que representa um registro de ponto.
 *
 * Contém todas as informações relacionadas a uma batida de ponto,
 * incluindo data, hora, tipo e metadados de auditoria.
 *
 * @property id Identificador único do registro (0 para novos registros)
 * @property dataHora Data e hora exata da batida de ponto
 * @property tipo Tipo da batida (ENTRADA ou SAIDA)
 * @property isEditadoManualmente Indica se o registro foi editado após criação
 * @property observacao Observação opcional do usuário sobre o registro
 * @property criadoEm Data e hora de criação do registro no sistema
 * @property atualizadoEm Data e hora da última atualização do registro
 *
 * @author Thiago
 * @since 1.0.0
 */
data class Ponto(
    val id: Long = 0,
    val dataHora: LocalDateTime,
    val tipo: TipoPonto,
    val isEditadoManualmente: Boolean = false,
    val observacao: String? = null,
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Retorna apenas a data do registro.
     */
    val data: LocalDate
        get() = dataHora.toLocalDate()

    /**
     * Retorna apenas a hora do registro.
     */
    val hora: LocalTime
        get() = dataHora.toLocalTime()

    /**
     * Retorna a hora formatada no padrão HH:mm.
     */
    val horaFormatada: String
        get() = String.format("%02d:%02d", hora.hour, hora.minute)

    /**
     * Verifica se este ponto é do tipo entrada.
     */
    val isEntrada: Boolean
        get() = tipo.isEntrada

    /**
     * Verifica se este ponto é do tipo saída.
     */
    val isSaida: Boolean
        get() = !tipo.isEntrada
}
