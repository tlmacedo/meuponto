// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/entity/AuditLogEntity.kt
package br.com.tlmacedo.meuponto.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.tlmacedo.meuponto.domain.model.AcaoAuditoria
import java.time.LocalDateTime

/**
 * Entidade Room que representa um log de auditoria no banco de dados.
 *
 * Esta entidade armazena registros de alterações realizadas nas entidades
 * do sistema para fins de auditoria e rastreabilidade.
 *
 * @property id Identificador único gerado automaticamente
 * @property entidade Nome da entidade afetada (ex: "Ponto", "Emprego")
 * @property entidadeId ID do registro afetado
 * @property acao Tipo de ação realizada (CRIAR, ATUALIZAR, EXCLUIR)
 * @property dadosAnteriores JSON com os dados antes da alteração
 * @property dadosNovos JSON com os dados após a alteração
 * @property criadoEm Data/hora da ação
 *
 * @author Thiago
 * @since 2.0.0
 */
@Entity(
    tableName = "audit_logs",
    indices = [
        Index(value = ["entidade", "entidade_id"]),
        Index(value = ["criado_em"]),
        Index(value = ["acao"])
    ]
)
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "entidade")
    val entidade: String,

    @ColumnInfo(name = "entidade_id")
    val entidadeId: Long,

    @ColumnInfo(name = "acao")
    val acao: AcaoAuditoria,

    @ColumnInfo(name = "dados_anteriores")
    val dadosAnteriores: String? = null,

    @ColumnInfo(name = "dados_novos")
    val dadosNovos: String? = null,

    @ColumnInfo(name = "criado_em")
    val criadoEm: LocalDateTime = LocalDateTime.now()
)

// ============================================================================
// Funções de Mapeamento (Mapper Extensions)
// ============================================================================

/**
 * Converte AuditLogEntity (camada de dados) para AuditLog (camada de domínio).
 */
fun AuditLogEntity.toDomain(): br.com.tlmacedo.meuponto.domain.model.AuditLog =
    br.com.tlmacedo.meuponto.domain.model.AuditLog(
        id = id,
        entidade = entidade,
        entidadeId = entidadeId,
        acao = acao,
        dadosAnteriores = dadosAnteriores,
        dadosNovos = dadosNovos,
        criadoEm = criadoEm
    )

/**
 * Converte AuditLog (camada de domínio) para AuditLogEntity (camada de dados).
 */
fun br.com.tlmacedo.meuponto.domain.model.AuditLog.toEntity(): AuditLogEntity =
    AuditLogEntity(
        id = id,
        entidade = entidade,
        entidadeId = entidadeId,
        acao = acao,
        dadosAnteriores = dadosAnteriores,
        dadosNovos = dadosNovos,
        criadoEm = criadoEm
    )
