// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/entity/FechamentoPeriodoEntity.kt
package br.com.tlmacedo.meuponto.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.tlmacedo.meuponto.domain.model.TipoFechamento
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Entidade Room que representa um fechamento de período no banco de dados.
 *
 * Esta entidade armazena os registros de fechamento de banco de horas,
 * permitindo o controle histórico de saldos anteriores e períodos fechados.
 *
 * @property id Identificador único gerado automaticamente
 * @property empregoId ID do emprego associado (FK)
 * @property dataFechamento Data em que o fechamento foi realizado
 * @property dataInicioPeriodo Data de início do período fechado
 * @property dataFimPeriodo Data de fim do período fechado
 * @property saldoAnteriorMinutos Saldo em minutos transportado do período anterior
 * @property tipo Tipo de fechamento (MENSAL, SEMESTRAL, ANUAL, MANUAL)
 * @property observacao Observação opcional sobre o fechamento
 * @property criadoEm Data/hora de criação do registro
 *
 * @author Thiago
 * @since 2.0.0
 */
@Entity(
    tableName = "fechamentos_periodo",
    foreignKeys = [
        ForeignKey(
            entity = EmpregoEntity::class,
            parentColumns = ["id"],
            childColumns = ["emprego_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["emprego_id"]),
        Index(value = ["data_fechamento"]),
        Index(value = ["tipo"]),
        Index(value = ["emprego_id", "data_inicio_periodo", "data_fim_periodo"], unique = true)
    ]
)
data class FechamentoPeriodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "emprego_id")
    val empregoId: Long,

    @ColumnInfo(name = "data_fechamento")
    val dataFechamento: LocalDate,

    @ColumnInfo(name = "data_inicio_periodo")
    val dataInicioPeriodo: LocalDate,

    @ColumnInfo(name = "data_fim_periodo")
    val dataFimPeriodo: LocalDate,

    @ColumnInfo(name = "saldo_anterior_minutos")
    val saldoAnteriorMinutos: Int = 0,

    @ColumnInfo(name = "tipo")
    val tipo: TipoFechamento = TipoFechamento.MENSAL,

    @ColumnInfo(name = "observacao")
    val observacao: String? = null,

    @ColumnInfo(name = "criado_em")
    val criadoEm: LocalDateTime = LocalDateTime.now()
)

// ============================================================================
// Funções de Mapeamento (Mapper Extensions)
// ============================================================================

/**
 * Converte FechamentoPeriodoEntity (camada de dados) para FechamentoPeriodo (camada de domínio).
 */
fun FechamentoPeriodoEntity.toDomain(): br.com.tlmacedo.meuponto.domain.model.FechamentoPeriodo =
    br.com.tlmacedo.meuponto.domain.model.FechamentoPeriodo(
        id = id,
        empregoId = empregoId,
        dataFechamento = dataFechamento,
        dataInicioPeriodo = dataInicioPeriodo,
        dataFimPeriodo = dataFimPeriodo,
        saldoAnteriorMinutos = saldoAnteriorMinutos,
        tipo = tipo,
        observacao = observacao,
        criadoEm = criadoEm
    )

/**
 * Converte FechamentoPeriodo (camada de domínio) para FechamentoPeriodoEntity (camada de dados).
 */
fun br.com.tlmacedo.meuponto.domain.model.FechamentoPeriodo.toEntity(): FechamentoPeriodoEntity =
    FechamentoPeriodoEntity(
        id = id,
        empregoId = empregoId,
        dataFechamento = dataFechamento,
        dataInicioPeriodo = dataInicioPeriodo,
        dataFimPeriodo = dataFimPeriodo,
        saldoAnteriorMinutos = saldoAnteriorMinutos,
        tipo = tipo,
        observacao = observacao,
        criadoEm = criadoEm
    )
