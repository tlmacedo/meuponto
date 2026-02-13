// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/repository/AjusteSaldoRepositoryImpl.kt
package br.com.tlmacedo.meuponto.data.repository

import br.com.tlmacedo.meuponto.data.local.database.dao.AjusteSaldoDao
import br.com.tlmacedo.meuponto.data.local.database.entity.toDomain
import br.com.tlmacedo.meuponto.data.local.database.entity.toEntity
import br.com.tlmacedo.meuponto.domain.model.AjusteSaldo
import br.com.tlmacedo.meuponto.domain.repository.AjusteSaldoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação concreta do repositório de ajustes de saldo.
 *
 * @property ajusteSaldoDao DAO do Room para operações de banco de dados
 *
 * @author Thiago
 * @since 2.0.0
 */
@Singleton
class AjusteSaldoRepositoryImpl @Inject constructor(
    private val ajusteSaldoDao: AjusteSaldoDao
) : AjusteSaldoRepository {

    // ========================================================================
    // Operações de Escrita (CRUD)
    // ========================================================================

    override suspend fun inserir(ajuste: AjusteSaldo): Long {
        return ajusteSaldoDao.inserir(ajuste.toEntity())
    }

    override suspend fun atualizar(ajuste: AjusteSaldo) {
        ajusteSaldoDao.atualizar(ajuste.toEntity())
    }

    override suspend fun excluir(ajuste: AjusteSaldo) {
        ajusteSaldoDao.excluir(ajuste.toEntity())
    }

    override suspend fun excluirPorId(id: Long) {
        ajusteSaldoDao.excluirPorId(id)
    }

    // ========================================================================
    // Operações de Leitura
    // ========================================================================

    override suspend fun buscarPorId(id: Long): AjusteSaldo? {
        return ajusteSaldoDao.buscarPorId(id)?.toDomain()
    }

    override suspend fun buscarPorEmprego(empregoId: Long): List<AjusteSaldo> {
        return ajusteSaldoDao.buscarPorEmprego(empregoId).map { it.toDomain() }
    }

    override suspend fun buscarPorData(empregoId: Long, data: LocalDate): List<AjusteSaldo> {
        return ajusteSaldoDao.buscarPorData(empregoId, data.toString()).map { it.toDomain() }
    }

    override suspend fun buscarPorPeriodo(
        empregoId: Long,
        dataInicio: LocalDate,
        dataFim: LocalDate
    ): List<AjusteSaldo> {
        return ajusteSaldoDao.buscarPorPeriodo(
            empregoId,
            dataInicio.toString(),
            dataFim.toString()
        ).map { it.toDomain() }
    }

    // ========================================================================
    // Cálculos
    // ========================================================================

    override suspend fun somarTotalPorEmprego(empregoId: Long): Int {
        return ajusteSaldoDao.somarTotalPorEmprego(empregoId)
    }

    override suspend fun somarPorPeriodo(
        empregoId: Long,
        dataInicio: LocalDate,
        dataFim: LocalDate
    ): Int {
        return ajusteSaldoDao.somarPorPeriodo(
            empregoId,
            dataInicio.toString(),
            dataFim.toString()
        )
    }

    override suspend fun contarPorEmprego(empregoId: Long): Int {
        return ajusteSaldoDao.contarPorEmprego(empregoId)
    }

    // ========================================================================
    // Operações Reativas (Flows)
    // ========================================================================

    override fun observarPorEmprego(empregoId: Long): Flow<List<AjusteSaldo>> {
        return ajusteSaldoDao.listarPorEmprego(empregoId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observarPorPeriodo(
        empregoId: Long,
        dataInicio: LocalDate,
        dataFim: LocalDate
    ): Flow<List<AjusteSaldo>> {
        return ajusteSaldoDao.listarPorPeriodo(
            empregoId,
            dataInicio.toString(),
            dataFim.toString()
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observarUltimos(empregoId: Long, limite: Int): Flow<List<AjusteSaldo>> {
        return ajusteSaldoDao.listarUltimosPorEmprego(empregoId, limite).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
