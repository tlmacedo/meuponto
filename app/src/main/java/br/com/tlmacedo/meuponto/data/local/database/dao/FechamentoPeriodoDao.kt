// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/dao/FechamentoPeriodoDao.kt
package br.com.tlmacedo.meuponto.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.tlmacedo.meuponto.data.local.database.entity.FechamentoPeriodoEntity
import br.com.tlmacedo.meuponto.domain.model.TipoFechamento
import kotlinx.coroutines.flow.Flow

@Dao
interface FechamentoPeriodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(fechamento: FechamentoPeriodoEntity): Long

    @Update
    suspend fun atualizar(fechamento: FechamentoPeriodoEntity)

    @Delete
    suspend fun excluir(fechamento: FechamentoPeriodoEntity)

    @Query("DELETE FROM fechamentos_periodo WHERE id = :id")
    suspend fun excluirPorId(id: Long)

    @Query("SELECT * FROM fechamentos_periodo WHERE id = :id")
    suspend fun buscarPorId(id: Long): FechamentoPeriodoEntity?

    @Query("SELECT * FROM fechamentos_periodo WHERE emprego_id = :empregoId ORDER BY data_fim_periodo DESC")
    fun listarPorEmprego(empregoId: Long): Flow<List<FechamentoPeriodoEntity>>

    @Query("SELECT * FROM fechamentos_periodo WHERE emprego_id = :empregoId ORDER BY data_fim_periodo DESC")
    suspend fun buscarPorEmprego(empregoId: Long): List<FechamentoPeriodoEntity>

    @Query("SELECT * FROM fechamentos_periodo WHERE emprego_id = :empregoId ORDER BY data_fim_periodo DESC LIMIT :limite")
    fun listarUltimosPorEmprego(empregoId: Long, limite: Int): Flow<List<FechamentoPeriodoEntity>>

    @Query("SELECT * FROM fechamentos_periodo WHERE emprego_id = :empregoId AND tipo = :tipo ORDER BY data_fim_periodo DESC")
    fun listarPorTipo(empregoId: Long, tipo: TipoFechamento): Flow<List<FechamentoPeriodoEntity>>

    @Query("SELECT * FROM fechamentos_periodo WHERE emprego_id = :empregoId AND tipo = :tipo ORDER BY data_fim_periodo DESC")
    suspend fun buscarPorTipo(empregoId: Long, tipo: TipoFechamento): List<FechamentoPeriodoEntity>

    @Query("SELECT * FROM fechamentos_periodo WHERE emprego_id = :empregoId AND data_inicio_periodo = :dataInicio AND data_fim_periodo = :dataFim")
    suspend fun buscarPorPeriodo(empregoId: Long, dataInicio: String, dataFim: String): FechamentoPeriodoEntity?

    @Query("SELECT * FROM fechamentos_periodo WHERE emprego_id = :empregoId ORDER BY data_fim_periodo DESC LIMIT 1")
    suspend fun buscarUltimoFechamento(empregoId: Long): FechamentoPeriodoEntity?

    @Query("SELECT * FROM fechamentos_periodo WHERE emprego_id = :empregoId ORDER BY data_fim_periodo DESC LIMIT 1")
    fun observarUltimoFechamento(empregoId: Long): Flow<FechamentoPeriodoEntity?>

    @Query("SELECT * FROM fechamentos_periodo WHERE emprego_id = :empregoId AND tipo = :tipo ORDER BY data_fim_periodo DESC LIMIT 1")
    suspend fun buscarUltimoFechamentoPorTipo(empregoId: Long, tipo: TipoFechamento): FechamentoPeriodoEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM fechamentos_periodo WHERE emprego_id = :empregoId AND (data_inicio_periodo <= :dataFim AND data_fim_periodo >= :dataInicio))")
    suspend fun existeFechamentoNoPeriodo(empregoId: Long, dataInicio: String, dataFim: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM fechamentos_periodo WHERE emprego_id = :empregoId)")
    suspend fun existeFechamentoPorEmprego(empregoId: Long): Boolean

    @Query("SELECT saldo_anterior_minutos FROM fechamentos_periodo WHERE emprego_id = :empregoId ORDER BY data_fim_periodo DESC LIMIT 1")
    suspend fun buscarUltimoSaldoAnterior(empregoId: Long): Int?

    @Query("SELECT COALESCE(SUM(saldo_anterior_minutos), 0) FROM fechamentos_periodo WHERE emprego_id = :empregoId")
    suspend fun somarSaldosAnteriores(empregoId: Long): Int

    @Query("SELECT COUNT(*) FROM fechamentos_periodo WHERE emprego_id = :empregoId")
    suspend fun contarPorEmprego(empregoId: Long): Int
}
