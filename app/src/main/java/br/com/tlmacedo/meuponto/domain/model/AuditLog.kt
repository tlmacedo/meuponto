// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/AuditLog.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.LocalDateTime

/**
 * Modelo de domínio que representa um registro de auditoria.
 *
 * Permite rastrear quem alterou o quê e quando, possibilitando
 * histórico completo e reversão de ações.
 *
 * @property id Identificador único do log
 * @property entidade Nome da tabela/entidade afetada (ex: "pontos", "empregos")
 * @property entidadeId ID do registro afetado
 * @property acao Tipo de ação (INSERT, UPDATE, DELETE)
 * @property dadosAnteriores JSON com os dados antes da alteração (null para INSERT)
 * @property dadosNovos JSON com os dados após a alteração (null para DELETE)
 * @property criadoEm Timestamp do registro da ação
 *
 * @author Thiago
 * @since 2.0.0
 */
data class AuditLog(
    val id: Long = 0,
    val entidade: String,
    val entidadeId: Long,
    val acao: AcaoAuditoria,
    val dadosAnteriores: String? = null,
    val dadosNovos: String? = null,
    val criadoEm: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Verifica se é uma inserção.
     */
    val isInsert: Boolean
        get() = acao == AcaoAuditoria.INSERT

    /**
     * Verifica se é uma atualização.
     */
    val isUpdate: Boolean
        get() = acao == AcaoAuditoria.UPDATE

    /**
     * Verifica se é uma exclusão.
     */
    val isDelete: Boolean
        get() = acao == AcaoAuditoria.DELETE
}
