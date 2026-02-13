// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/repository/AuditLogRepository.kt
package br.com.tlmacedo.meuponto.domain.repository

import br.com.tlmacedo.meuponto.domain.model.AcaoAuditoria
import br.com.tlmacedo.meuponto.domain.model.AuditLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Interface do repositório de logs de auditoria.
 *
 * Define o contrato para operações de persistência dos registros de auditoria,
 * permitindo rastreabilidade completa das alterações no sistema.
 *
 * @author Thiago
 * @since 2.0.0
 */
interface AuditLogRepository {

    // ========================================================================
    // Operações de Escrita
    // ========================================================================

    /**
     * Insere um novo log de auditoria.
     *
     * @param log Log a ser inserido
     * @return ID gerado para o novo registro
     */
    suspend fun inserir(log: AuditLog): Long

    /**
     * Insere múltiplos logs de uma vez.
     *
     * @param logs Lista de logs a serem inseridos
     * @return Lista de IDs gerados
     */
    suspend fun inserirTodos(logs: List<AuditLog>): List<Long>

    // ========================================================================
    // Operações de Leitura
    // ========================================================================

    /**
     * Busca os logs de uma entidade específica.
     *
     * @param entidade Nome da entidade (ex: "pontos", "empregos")
     * @param entidadeId ID do registro
     * @return Lista de logs ordenada por data decrescente
     */
    suspend fun buscarPorEntidade(entidade: String, entidadeId: Long): List<AuditLog>

    /**
     * Conta o total de logs no sistema.
     *
     * @return Quantidade total de logs
     */
    suspend fun contarTodos(): Int

    /**
     * Conta os logs de uma entidade.
     *
     * @param entidade Nome da entidade
     * @return Quantidade de logs da entidade
     */
    suspend fun contarPorEntidade(entidade: String): Int

    /**
     * Conta os logs de um registro específico.
     *
     * @param entidade Nome da entidade
     * @param entidadeId ID do registro
     * @return Quantidade de logs do registro
     */
    suspend fun contarPorEntidadeEId(entidade: String, entidadeId: Long): Int

    // ========================================================================
    // Operações de Limpeza
    // ========================================================================

    /**
     * Exclui logs anteriores a uma data limite.
     *
     * @param dataLimite Data limite para exclusão
     * @return Quantidade de registros excluídos
     */
    suspend fun excluirAnterioresA(dataLimite: LocalDateTime): Int

    /**
     * Exclui todos os logs de uma entidade específica.
     *
     * @param entidade Nome da entidade
     * @param entidadeId ID do registro
     */
    suspend fun excluirPorEntidade(entidade: String, entidadeId: Long)

    // ========================================================================
    // Operações Reativas (Flows)
    // ========================================================================

    /**
     * Observa os logs de uma entidade de forma reativa.
     *
     * @param entidade Nome da entidade
     * @param entidadeId ID do registro
     * @return Flow que emite a lista sempre que houver mudanças
     */
    fun observarPorEntidade(entidade: String, entidadeId: Long): Flow<List<AuditLog>>

    /**
     * Observa os logs de um período de forma reativa.
     *
     * @param dataInicio Data inicial (inclusive)
     * @param dataFim Data final (inclusive)
     * @return Flow que emite a lista do período
     */
    fun observarPorPeriodo(dataInicio: LocalDateTime, dataFim: LocalDateTime): Flow<List<AuditLog>>

    /**
     * Observa os logs de uma ação específica de forma reativa.
     *
     * @param acao Tipo de ação (INSERT, UPDATE, DELETE)
     * @return Flow que emite a lista da ação
     */
    fun observarPorAcao(acao: AcaoAuditoria): Flow<List<AuditLog>>

    /**
     * Observa os últimos logs do sistema.
     *
     * @param limite Quantidade máxima de logs
     * @return Flow que emite os últimos logs
     */
    fun observarUltimos(limite: Int): Flow<List<AuditLog>>

    /**
     * Observa os últimos logs de uma entidade.
     *
     * @param entidade Nome da entidade
     * @param limite Quantidade máxima de logs
     * @return Flow que emite os últimos logs da entidade
     */
    fun observarUltimosPorEntidade(entidade: String, limite: Int): Flow<List<AuditLog>>
}
