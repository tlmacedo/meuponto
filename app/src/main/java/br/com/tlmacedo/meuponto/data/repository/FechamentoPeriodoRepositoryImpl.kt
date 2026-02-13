// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/repository/FechamentoPeriodoRepositoryImpl.kt
package br.com.tlmacedo.meuponto.data.repository

import br.com.tlmacedo.meuponto.data.local.database.dao.FechamentoPeriodoDao
import br.com.tlmacedo.meuponto.data.local.database.entity.toDomain
import br.com.tlmacedo.meuponto.data.local.database.entity.toEntity
import br.com.tlmacedo.meuponto.domain.model.FechamentoPeriodo
import br.com.tlmacedo.meuponto.domain.model.TipoFechamento
import br.com.tlmacedo.meuponto.domain.repository.FechamentoPeriodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação concreta do repositório de fechamentos de período.
 *
 * @property fechamentoPeriodoDao DAO do Room para operações de banco de dados
 *
 * @author Thiago
 * @since 2.0.0
 */
@Singleton
class FechamentoPeriodoRepositoryImpl @Inject constructor(
    private val fechamentoPeriodoDao: FechamentoPeriodoDao
) : FechamentoPeriodoRepository {

    // ========================================================================
    // Operações de Escrita (CRUD)
    // ========================================================================

    override suspend fun inserir(fechamento: FechamentoPeriodo): Long {
        return fechamentoPeriodoDao.inserir(fechamento.toEntity())
    }

    override suspend fun atualizar(fechamento: FechamentoPeriodo) {
        fechamentoPeriodoDao.atualizar(fechamento.toEntity())
    }

    override suspend fun excluir(fechamento: FechamentoPeriodo) {
        fechamentoPeriodoDao.excluir(fechamento.toEntity())
    }

    override suspend fun excluirPorId(id: Long) {
        fechamentoPeriodoDao.excluirPorId(id)
    }

    // ========================================================================
    // Operações de Leitura
    // ========================================================================

    override suspend fun buscarPorId(id: Long): FechamentoPeriodo? {
        return fechamentoPeriodoDao.buscarPorId(id)?.toDomain()
    }

    override suspend fun buscarPorEmprego(empregoId: Long): List<FechamentoPeriodo> {
        return fechamentoPeriodoDao.buscarPorEmprego(empregoId).map { it.toDomain() }
    }

    override suspend fun buscarPorTipo(empregoId: Long, tipo: TipoFechamento): List<FechamentoPeriodo> {
        return fechamentoPeriodoDao.buscarPorTipo(empregoId, tipo).map { it.toDomain() }
    }

    override suspend fun buscarPorPeriodo(
        empregoId: Long,
        dataInicio: LocalDate,
        dataFim: LocalDate
    ): FechamentoPeriodo? {
        return fechamentoPeriodoDao.buscarPorPeriodo(
            empregoId,
            dataInicio.toString(),
            dataFim.toString()
        )?.toDomain()
    }

    override suspend fun buscarUltimoFechamento(empregoId: Long): FechamentoPeriodo? {
        return fechamentoPeriodoDao.buscarUltimoFechamento(empregoId)?.toDomain()
    }

    override suspend fun buscarUltimoFechamentoPorTipo(
        empregoId: Long,
        tipo: TipoFechamento
    ): FechamentoPeriodo? {
        return fechamentoPeriodoDao.buscarUltimoFechamentoPorTipo(empregoId, tipo)?.toDomain()
    }

    // ========================================================================
    // Verificações
    // ========================================================================

    override suspend fun existeFechamentoNoPeriodo(
        empregoId: Long,
        dataInicio: LocalDate,
        dataFim: LocalDate
    ): Boolean {
        return fechamentoPeriodoDao.existeFechamentoNoPeriodo(
            empregoId,
            dataInicio.toString(),
            dataFim.toString()
        )
    }

    override suspend fun existeFechamentoPorEmprego(empregoId: Long): Boolean {
        return fechamentoPeriodoDao.existeFechamentoPorEmprego(empregoId)
    }

    // ========================================================================
    // Cálculos
    // ========================================================================

    override suspend fun buscarUltimoSaldoAnterior(empregoId: Long): Int? {
        return fechamentoPeriodoDao.buscarUltimoSaldoAnterior(empregoId)
    }

    override suspend fun somarSaldosAnteriores(empregoId: Long): Int {
        return fechamentoPeriodoDao.somarSaldosAnteriores(empregoId)
    }

    override suspend fun contarPorEmprego(empregoId: Long): Int {
        return fechamentoPeriodoDao.contarPorEmprego(empregoId)
    }

    // ========================================================================
    // Operações Reativas (Flows)
    // ========================================================================

    override fun observarPorEmprego(empregoId: Long): Flow<List<FechamentoPeriodo>> {
        return fechamentoPeriodoDao.listarPorEmprego(empregoId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observarPorTipo(empregoId: Long, tipo: TipoFechamento): Flow<List<FechamentoPeriodo>> {
        return fechamentoPeriodoDao.listarPorTipo(empregoId, tipo).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observarUltimoFechamento(empregoId: Long): Flow<FechamentoPeriodo?> {
        return fechamentoPeriodoDao.observarUltimoFechamento(empregoId).map { it?.toDomain() }
    }

    override fun observarUltimos(empregoId: Long, limite: Int): Flow<List<FechamentoPeriodo>> {
        return fechamentoPeriodoDao.listarUltimosPorEmprego(empregoId, limite).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
