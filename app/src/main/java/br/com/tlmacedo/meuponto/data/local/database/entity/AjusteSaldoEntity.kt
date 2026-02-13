// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/entity/AjusteSaldoEntity.kt
package br.com.tlmacedo.meuponto.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Entidade Room que representa um ajuste manual no banco de horas.
 * 
 * Permite adicionar ou subtrair minutos do saldo de forma controlada,
 * com justificativa obrigatória para auditoria.
 *
 * @property id Identificador único auto-gerado
 * @property empregoId FK para o emprego associado
 * @property data Data à qual o ajuste está vinculado
 * @property minutos Quantidade de minutos a ajustar (positivo = adicionar, negativo = subtrair)
 * @property justificativa Justificativa obrigatória para o ajuste
 * @property criadoEm Timestamp de criação
 *
 * @author Thiago
 * @since 2.0.0
 */
@Entity(
    tableName = "ajustes_saldo",
    foreignKeys = [
        ForeignKey(
            entity = EmpregoEntity::class,
            parentColumns = ["id"],
            childColumns = ["empregoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["empregoId"]),
        Index(value = ["data"]),
        Index(value = ["empregoId", "data"])
    ]
)
data class AjusteSaldoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val empregoId: Long,
    val data: LocalDate,
    val minutos: Int, // Positivo = adiciona, Negativo = subtrai
    val justificativa: String,
    val criadoEm: LocalDateTime = LocalDateTime.now()
)

// ============================================================================
// Funções de Mapeamento (Mapper Extensions)
// ============================================================================

/**
 * Converte AjusteSaldoEntity (camada de dados) para AjusteSaldo (camada de domínio).
 *
 * @return Instância de [AjusteSaldo] com os dados mapeados
 */
fun AjusteSaldoEntity.toDomain(): br.com.tlmacedo.meuponto.domain.model.AjusteSaldo =
    br.com.tlmacedo.meuponto.domain.model.AjusteSaldo(
        id = id,
        empregoId = empregoId,
        data = data,
        minutos = minutos,
        justificativa = justificativa,
        criadoEm = criadoEm
    )

/**
 * Converte AjusteSaldo (camada de domínio) para AjusteSaldoEntity (camada de dados).
 *
 * @return Instância de [AjusteSaldoEntity] pronta para persistência
 */
fun br.com.tlmacedo.meuponto.domain.model.AjusteSaldo.toEntity(): AjusteSaldoEntity =
    AjusteSaldoEntity(
        id = id,
        empregoId = empregoId,
        data = data,
        minutos = minutos,
        justificativa = justificativa,
        criadoEm = criadoEm
    )

