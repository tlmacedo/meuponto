// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/repository/PontoRepositoryImpl.kt
package br.com.tlmacedo.meuponto.data.repository

import br.com.tlmacedo.meuponto.data.local.database.dao.PontoDao
import br.com.tlmacedo.meuponto.data.local.database.entity.toDomain
import br.com.tlmacedo.meuponto.data.local.database.entity.toEntity
import br.com.tlmacedo.meuponto.data.local.database.util.DatabaseCheckpointManager
import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.repository.PontoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação concreta do repositório de pontos.
 *
 * Atua como intermediário entre a camada de domínio e a camada de dados,
 * realizando a conversão entre modelos de domínio (Ponto) e entidades
 * de banco de dados (PontoEntity).
 *
 * @property pontoDao DAO do Room para operações de banco de dados
 * @property checkpointManager Gerenciador de checkpoint WAL para persistência imediata
 *
 * @author Thiago
 * @since 1.0.0
 * @updated 2.0.0 - Adicionado suporte a múltiplos empregos e marcadores
 * @updated 9.1.0 - Adicionado checkpoint WAL após operações de escrita
 */
@Singleton
class PontoRepositoryImpl @Inject constructor(
    private val pontoDao: PontoDao,
    private val checkpointManager: DatabaseCheckpointManager
) : PontoRepository {

    // ========================================================================
    // Operações de Escrita (CRUD) - COM CHECKPOINT
    // ========================================================================

    override suspend fun inserir(ponto: Ponto): Long {
        val id = pontoDao.inserir(ponto.toEntity())

        // Checkpoint após inserção para garantir persistência imediata
        checkpointManager.checkpoint()

        return id
    }

    override suspend fun atualizar(ponto: Ponto) {
        pontoDao.atualizar(ponto.toEntity())

        // Checkpoint após atualização
        checkpointManager.checkpoint()
    }

    override suspend fun excluir(ponto: Ponto) {
        pontoDao.excluir(ponto.toEntity())

        // Checkpoint após exclusão
        checkpointManager.checkpoint()
    }

    override suspend fun excluirPorId(id: Long) {
        pontoDao.excluirPorId(id)

        // Checkpoint após exclusão
        checkpointManager.checkpoint()
    }

    override suspend fun buscarPrimeiraData(empregoId: Long): LocalDate? {
        return pontoDao.buscarPrimeiraData(empregoId)
    }

    override suspend fun atualizarFotoComprovante(pontoId: Long, fotoPath: String?) {
        val ponto = buscarPorId(pontoId) ?: return
        val pontoAtualizado = ponto.copy(
            fotoComprovantePath = fotoPath,
            atualizadoEm = LocalDateTime.now()
        )
        atualizar(pontoAtualizado)
        // Checkpoint já é chamado dentro de atualizar()
    }

    // ========================================================================
    // Operações de Leitura - Por ID (sem checkpoint necessário)
    // ========================================================================

    override suspend fun buscarPorId(id: Long): Ponto? {
        return pontoDao.buscarPorId(id)?.toDomain()
    }

    override fun observarPorId(id: Long): Flow<Ponto?> {
        return pontoDao.observarPorId(id).map { entity ->
            entity?.toDomain()
        }
    }

    // ========================================================================
    // Operações de Leitura - Legadas (retrocompatibilidade)
    // ========================================================================

    @Suppress("DEPRECATION")
    @Deprecated(
        message = "Use buscarPorEmpregoEData para suporte a múltiplos empregos",
        replaceWith = ReplaceWith("buscarPorEmpregoEData(empregoId, data)")
    )
    override suspend fun buscarPontosPorData(data: LocalDate): List<Ponto> {
        return pontoDao.listarPorData(data)
            .first()
            .map { it.toDomain() }
    }

    @Suppress("DEPRECATION")
    @Deprecated(
        message = "Use buscarUltimoPonto(empregoId) para suporte a múltiplos empregos",
        replaceWith = ReplaceWith("buscarUltimoPonto(empregoId)")
    )
    override suspend fun buscarUltimoPontoDoDia(data: LocalDate): Ponto? {
        return pontoDao.listarPorData(data)
            .first()
            .lastOrNull()
            ?.toDomain()
    }

    @Suppress("DEPRECATION")
    @Deprecated(
        message = "Use observarPorEmpregoEData para suporte a múltiplos empregos",
        replaceWith = ReplaceWith("observarPorEmpregoEData(empregoId, data)")
    )
    override fun observarPontosPorData(data: LocalDate): Flow<List<Ponto>> {
        return pontoDao.listarPorData(data).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated(
        message = "Use observarPorEmprego para suporte a múltiplos empregos",
        replaceWith = ReplaceWith("observarPorEmprego(empregoId)")
    )
    override fun observarTodos(): Flow<List<Ponto>> {
        return pontoDao.listarTodos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated(
        message = "Use observarPorEmpregoEPeriodo para suporte a múltiplos empregos",
        replaceWith = ReplaceWith("observarPorEmpregoEPeriodo(empregoId, dataInicio, dataFim)")
    )
    override fun observarPontosPorPeriodo(
        dataInicio: LocalDate,
        dataFim: LocalDate
    ): Flow<List<Ponto>> {
        return pontoDao.listarPorPeriodo(dataInicio, dataFim).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // ========================================================================
    // Operações de Leitura - Por Emprego (Novas)
    // ========================================================================

    override fun observarPorEmprego(empregoId: Long): Flow<List<Ponto>> {
        return pontoDao.listarPorEmprego(empregoId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observarPorEmpregoEData(empregoId: Long, data: LocalDate): Flow<List<Ponto>> {
        return pontoDao.listarPorEmpregoEData(empregoId, data).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun buscarPorEmpregoEData(empregoId: Long, data: LocalDate): List<Ponto> {
        return pontoDao.buscarPorEmpregoEData(empregoId, data).map { it.toDomain() }
    }

    override fun observarPorEmpregoEPeriodo(
        empregoId: Long,
        dataInicio: LocalDate,
        dataFim: LocalDate
    ): Flow<List<Ponto>> {
        return pontoDao.listarPorEmpregoEPeriodo(empregoId, dataInicio, dataFim).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun buscarPorEmpregoEPeriodo(
        empregoId: Long,
        dataInicio: LocalDate,
        dataFim: LocalDate
    ): List<Ponto> {
        return pontoDao.buscarPorEmpregoEPeriodo(empregoId, dataInicio, dataFim)
            .map { it.toDomain() }
    }

    // ========================================================================
    // Operações de Leitura - Por Marcador
    // ========================================================================

    override fun observarPorMarcador(marcadorId: Long): Flow<List<Ponto>> {
        return pontoDao.listarPorMarcador(marcadorId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // ========================================================================
    // Operações de Contagem
    // ========================================================================

    override suspend fun contarPorEmprego(empregoId: Long): Int {
        return pontoDao.contarPorEmprego(empregoId)
    }

    override suspend fun contarPorEmpregoEData(empregoId: Long, data: LocalDate): Int {
        return pontoDao.contarPorEmpregoEData(empregoId, data)
    }

    override suspend fun contarPorEmpregoEPeriodo(
        empregoId: Long,
        dataInicio: LocalDate,
        dataFim: LocalDate
    ): Int {
        return pontoDao.contarPorEmpregoEPeriodo(empregoId, dataInicio, dataFim)
    }

    // ========================================================================
    // Operações Auxiliares
    // ========================================================================

    override fun observarDatasComRegistro(empregoId: Long): Flow<List<LocalDate>> {
        return pontoDao.listarDatasComRegistro(empregoId)
    }

    override suspend fun buscarUltimoPonto(empregoId: Long): Ponto? {
        return pontoDao.buscarUltimoPonto(empregoId)?.toDomain()
    }

    override fun observarUltimoPonto(empregoId: Long): Flow<Ponto?> {
        return pontoDao.observarUltimoPonto(empregoId).map { entity ->
            entity?.toDomain()
        }
    }

    // ========================================================================
    // Operações de Migração
    // ========================================================================

    override suspend fun migrarParaEmprego(empregoIdOrigem: Long, empregoIdDestino: Long): Int {
        val count = pontoDao.migrarParaEmprego(empregoIdOrigem, empregoIdDestino)

        // Checkpoint após migração em lote
        checkpointManager.checkpoint()

        return count
    }
}
