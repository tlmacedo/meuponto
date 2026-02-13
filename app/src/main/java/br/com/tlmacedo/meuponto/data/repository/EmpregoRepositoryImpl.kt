// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/repository/EmpregoRepositoryImpl.kt
package br.com.tlmacedo.meuponto.data.repository

import br.com.tlmacedo.meuponto.data.local.database.dao.EmpregoDao
import br.com.tlmacedo.meuponto.data.local.database.entity.toDomain
import br.com.tlmacedo.meuponto.data.local.database.entity.toEntity
import br.com.tlmacedo.meuponto.domain.model.Emprego
import br.com.tlmacedo.meuponto.domain.repository.EmpregoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação concreta do repositório de empregos.
 *
 * Atua como intermediário entre a camada de domínio e a camada de dados,
 * realizando a conversão entre modelos de domínio e entidades de banco de dados.
 *
 * @property empregoDao DAO do Room para operações de banco de dados
 *
 * @author Thiago
 * @since 2.0.0
 */
@Singleton
class EmpregoRepositoryImpl @Inject constructor(
    private val empregoDao: EmpregoDao
) : EmpregoRepository {

    // ========================================================================
    // Operações de Escrita (CRUD)
    // ========================================================================

    override suspend fun inserir(emprego: Emprego): Long {
        return empregoDao.inserir(emprego.toEntity())
    }

    override suspend fun atualizar(emprego: Emprego) {
        empregoDao.atualizar(emprego.toEntity())
    }

    override suspend fun excluir(emprego: Emprego) {
        empregoDao.excluir(emprego.toEntity())
    }

    override suspend fun excluirPorId(id: Long) {
        empregoDao.excluirPorId(id)
    }

    // ========================================================================
    // Operações de Leitura
    // ========================================================================

    override suspend fun buscarPorId(id: Long): Emprego? {
        return empregoDao.buscarPorId(id)?.toDomain()
    }

    override suspend fun buscarAtivos(): List<Emprego> {
        return empregoDao.buscarAtivos().map { it.toDomain() }
    }

    override suspend fun contarAtivos(): Int {
        return empregoDao.contarAtivos()
    }

    override suspend fun contarTodos(): Int {
        return empregoDao.contarTodos()
    }

    override suspend fun existe(id: Long): Boolean {
        return empregoDao.existe(id)
    }

    // ========================================================================
    // Operações Reativas (Flows)
    // ========================================================================

    override fun observarPorId(id: Long): Flow<Emprego?> {
        return empregoDao.observarPorId(id).map { it?.toDomain() }
    }

    override fun observarTodos(): Flow<List<Emprego>> {
        return empregoDao.listarTodos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observarAtivos(): Flow<List<Emprego>> {
        return empregoDao.listarAtivos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observarArquivados(): Flow<List<Emprego>> {
        return empregoDao.listarArquivados().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // ========================================================================
    // Operações de Status
    // ========================================================================

    override suspend fun atualizarStatus(id: Long, ativo: Boolean) {
        empregoDao.atualizarStatus(id, ativo, LocalDateTime.now().toString())
    }

    override suspend fun arquivar(id: Long) {
        empregoDao.atualizarArquivado(id, true, LocalDateTime.now().toString())
    }

    override suspend fun desarquivar(id: Long) {
        empregoDao.atualizarArquivado(id, false, LocalDateTime.now().toString())
    }

    override suspend fun atualizarOrdem(id: Long, ordem: Int) {
        empregoDao.atualizarOrdem(id, ordem, LocalDateTime.now().toString())
    }

    override suspend fun buscarProximaOrdem(): Int {
        return (empregoDao.buscarMaiorOrdem() ?: 0) + 1
    }
}
