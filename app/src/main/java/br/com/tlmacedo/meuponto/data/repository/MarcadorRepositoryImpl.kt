// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/repository/MarcadorRepositoryImpl.kt
package br.com.tlmacedo.meuponto.data.repository

import br.com.tlmacedo.meuponto.data.local.database.dao.MarcadorDao
import br.com.tlmacedo.meuponto.data.local.database.entity.toDomain
import br.com.tlmacedo.meuponto.data.local.database.entity.toEntity
import br.com.tlmacedo.meuponto.domain.model.Marcador
import br.com.tlmacedo.meuponto.domain.repository.MarcadorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação concreta do repositório de marcadores.
 *
 * @property marcadorDao DAO do Room para operações de banco de dados
 *
 * @author Thiago
 * @since 2.0.0
 */
@Singleton
class MarcadorRepositoryImpl @Inject constructor(
    private val marcadorDao: MarcadorDao
) : MarcadorRepository {

    // ========================================================================
    // Operações de Escrita (CRUD)
    // ========================================================================

    override suspend fun inserir(marcador: Marcador): Long {
        return marcadorDao.inserir(marcador.toEntity())
    }

    override suspend fun inserirTodos(marcadores: List<Marcador>): List<Long> {
        return marcadorDao.inserirTodos(marcadores.map { it.toEntity() })
    }

    override suspend fun atualizar(marcador: Marcador) {
        marcadorDao.atualizar(marcador.toEntity())
    }

    override suspend fun excluir(marcador: Marcador) {
        marcadorDao.excluir(marcador.toEntity())
    }

    override suspend fun excluirPorId(id: Long) {
        marcadorDao.excluirPorId(id)
    }

    // ========================================================================
    // Operações de Leitura
    // ========================================================================

    override suspend fun buscarPorId(id: Long): Marcador? {
        return marcadorDao.buscarPorId(id)?.toDomain()
    }

    override suspend fun buscarPorNome(empregoId: Long, nome: String): Marcador? {
        return marcadorDao.buscarPorNome(empregoId, nome)?.toDomain()
    }

    override suspend fun buscarAtivosPorEmprego(empregoId: Long): List<Marcador> {
        return marcadorDao.buscarAtivosPorEmprego(empregoId).map { it.toDomain() }
    }

    override suspend fun existeComNome(empregoId: Long, nome: String, excludeId: Long): Boolean {
        return marcadorDao.existeComNome(empregoId, nome, excludeId)
    }

    // ========================================================================
    // Operações Reativas (Flows)
    // ========================================================================

    override fun observarPorEmprego(empregoId: Long): Flow<List<Marcador>> {
        return marcadorDao.listarPorEmprego(empregoId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observarAtivosPorEmprego(empregoId: Long): Flow<List<Marcador>> {
        return marcadorDao.listarAtivosPorEmprego(empregoId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // ========================================================================
    // Operações de Status
    // ========================================================================

    override suspend fun atualizarStatus(id: Long, ativo: Boolean) {
        marcadorDao.atualizarStatus(id, ativo, LocalDateTime.now().toString())
    }
}
