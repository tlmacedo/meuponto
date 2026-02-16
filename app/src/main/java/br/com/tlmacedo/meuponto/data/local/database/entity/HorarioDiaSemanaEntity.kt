// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/entity/HorarioDiaSemanaEntity.kt
package br.com.tlmacedo.meuponto.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.tlmacedo.meuponto.domain.model.DiaSemana
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Entidade Room que armazena a configuração de horários para cada dia da semana.
 */
@Entity(
    tableName = "horarios_dia_semana",
    foreignKeys = [
        ForeignKey(
            entity = EmpregoEntity::class,
            parentColumns = ["id"],
            childColumns = ["empregoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["empregoId", "diaSemana"], unique = true)
    ]
)
data class HorarioDiaSemanaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val empregoId: Long,
    val diaSemana: DiaSemana,
    val ativo: Boolean = true,
    
    // CARGA HORÁRIA
    val cargaHorariaMinutos: Int = 492,
    
    // HORÁRIOS IDEAIS
    val entradaIdeal: LocalTime? = null,
    val saidaIntervaloIdeal: LocalTime? = null,
    val voltaIntervaloIdeal: LocalTime? = null,
    val saidaIdeal: LocalTime? = null,
    
    // INTERVALO
    val intervaloMinimoMinutos: Int = 60,
    val toleranciaIntervaloMaisMinutos: Int = 0,

    // TOLERÂNCIAS ESPECÍFICAS
    val toleranciaEntradaMinutos: Int? = null,
    val toleranciaSaidaMinutos: Int? = null,
    
    // AUDITORIA
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now()
)

fun HorarioDiaSemanaEntity.toDomain(): br.com.tlmacedo.meuponto.domain.model.HorarioDiaSemana =
    br.com.tlmacedo.meuponto.domain.model.HorarioDiaSemana(
        id = id,
        empregoId = empregoId,
        diaSemana = diaSemana,
        ativo = ativo,
        cargaHorariaMinutos = cargaHorariaMinutos,
        entradaIdeal = entradaIdeal,
        saidaIntervaloIdeal = saidaIntervaloIdeal,
        voltaIntervaloIdeal = voltaIntervaloIdeal,
        saidaIdeal = saidaIdeal,
        intervaloMinimoMinutos = intervaloMinimoMinutos,
        toleranciaIntervaloMaisMinutos = toleranciaIntervaloMaisMinutos,
        toleranciaEntradaMinutos = toleranciaEntradaMinutos,
        toleranciaSaidaMinutos = toleranciaSaidaMinutos,
        criadoEm = criadoEm,
        atualizadoEm = atualizadoEm
    )

fun br.com.tlmacedo.meuponto.domain.model.HorarioDiaSemana.toEntity(): HorarioDiaSemanaEntity =
    HorarioDiaSemanaEntity(
        id = id,
        empregoId = empregoId,
        diaSemana = diaSemana,
        ativo = ativo,
        cargaHorariaMinutos = cargaHorariaMinutos,
        entradaIdeal = entradaIdeal,
        saidaIntervaloIdeal = saidaIntervaloIdeal,
        voltaIntervaloIdeal = voltaIntervaloIdeal,
        saidaIdeal = saidaIdeal,
        intervaloMinimoMinutos = intervaloMinimoMinutos,
        toleranciaIntervaloMaisMinutos = toleranciaIntervaloMaisMinutos,
        toleranciaEntradaMinutos = toleranciaEntradaMinutos,
        toleranciaSaidaMinutos = toleranciaSaidaMinutos,
        criadoEm = criadoEm,
        atualizadoEm = atualizadoEm
    )
