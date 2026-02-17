// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/entity/VersaoJornadaEntity.kt
package br.com.tlmacedo.meuponto.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Entidade Room que representa uma versão de jornada de trabalho.
 *
 * @author Thiago
 * @since 2.7.0
 */
@Entity(
    tableName = "versoes_jornada",
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
        Index(value = ["empregoId", "dataInicio"]),
        Index(value = ["empregoId", "dataFim"])
    ]
)
data class VersaoJornadaEntity(
    @PrimaryKey(autoGenerate = true)
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
)

fun VersaoJornadaEntity.toDomain(): br.com.tlmacedo.meuponto.domain.model.VersaoJornada =
    br.com.tlmacedo.meuponto.domain.model.VersaoJornada(
        id = id,
        empregoId = empregoId,
        dataInicio = dataInicio,
        dataFim = dataFim,
        descricao = descricao,
        numeroVersao = numeroVersao,
        vigente = vigente,
        jornadaMaximaDiariaMinutos = jornadaMaximaDiariaMinutos,
        intervaloMinimoInterjornadaMinutos = intervaloMinimoInterjornadaMinutos,
        toleranciaIntervaloMaisMinutos = toleranciaIntervaloMaisMinutos,
        criadoEm = criadoEm,
        atualizadoEm = atualizadoEm
    )

fun br.com.tlmacedo.meuponto.domain.model.VersaoJornada.toEntity(): VersaoJornadaEntity =
    VersaoJornadaEntity(
        id = id,
        empregoId = empregoId,
        dataInicio = dataInicio,
        dataFim = dataFim,
        descricao = descricao,
        numeroVersao = numeroVersao,
        vigente = vigente,
        jornadaMaximaDiariaMinutos = jornadaMaximaDiariaMinutos,
        intervaloMinimoInterjornadaMinutos = intervaloMinimoInterjornadaMinutos,
        toleranciaIntervaloMaisMinutos = toleranciaIntervaloMaisMinutos,
        criadoEm = criadoEm,
        atualizadoEm = atualizadoEm
    )
