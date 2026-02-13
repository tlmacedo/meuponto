// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/repository/MarcadorRepository.kt
package br.com.tlmacedo.meuponto.domain.repository

import br.com.tlmacedo.meuponto.domain.model.Marcador
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de marcadores.
 *
 * Define o contrato para operações de persistência de marcadores/tags
 * que categorizam os registros de ponto.
 *
 * @author Thiago
 * @since 2.0.0
 */
interface MarcadorRepository {

    // ========================================================================
    // Operações de Escrita (CRUD)
    // ========================================================================

    /**
     * Insere um novo marcador.
     *
     * @param marcador Marcador a ser inserido
     * @return ID gerado para o novo registro
     */
    suspend fun inserir(marcador: Marcador): Long

    /**
     * Insere múltiplos marcadores de uma vez.
     *
     * @param marcadores Lista de marcadores a serem inseridos
     * @return Lista de IDs gerados
     */
    suspend fun inserirTodos(marcadores: List<Marcador>): List<Long>

    /**
     * Atualiza um marcador existente.
     *
     * @param marcador Marcador com os dados atualizados
     */
    suspend fun atualizar(marcador: Marcador)

    /**
     * Remove um marcador.
     *
     * @param marcador Marcador a ser removido
     */
    suspend fun excluir(marcador: Marcador)

    /**
     * Remove um marcador pelo ID.
     *
     * @param id Identificador do marcador
     */
    suspend fun excluirPorId(id: Long)

    // ========================================================================
    // Operações de Leitura
    // ========================================================================

    /**
     * Busca um marcador pelo ID.
     *
     * @param id Identificador único do marcador
     * @return Marcador encontrado ou null
     */
    suspend fun buscarPorId(id: Long): Marcador?

    /**
     * Busca um marcador pelo nome em um emprego.
     *
     * @param empregoId Identificador do emprego
     * @param nome Nome do marcador
     * @return Marcador encontrado ou null
     */
    suspend fun buscarPorNome(empregoId: Long, nome: String): Marcador?

    /**
     * Busca os marcadores ativos de um emprego.
     *
     * @param empregoId Identificador do emprego
     * @return Lista de marcadores ativos
     */
    suspend fun buscarAtivosPorEmprego(empregoId: Long): List<Marcador>

    /**
     * Verifica se já existe um marcador com o nome especificado.
     *
     * @param empregoId Identificador do emprego
     * @param nome Nome a verificar
     * @param excludeId ID a excluir da verificação (para edição)
     * @return true se já existe, false caso contrário
     */
    suspend fun existeComNome(empregoId: Long, nome: String, excludeId: Long = 0): Boolean

    // ========================================================================
    // Operações Reativas (Flows)
    // ========================================================================

    /**
     * Observa todos os marcadores de um emprego de forma reativa.
     *
     * @param empregoId Identificador do emprego
     * @return Flow que emite a lista sempre que houver mudanças
     */
    fun observarPorEmprego(empregoId: Long): Flow<List<Marcador>>

    /**
     * Observa apenas os marcadores ativos de um emprego de forma reativa.
     *
     * @param empregoId Identificador do emprego
     * @return Flow que emite a lista de ativos
     */
    fun observarAtivosPorEmprego(empregoId: Long): Flow<List<Marcador>>

    // ========================================================================
    // Operações de Status
    // ========================================================================

    /**
     * Atualiza o status ativo/inativo de um marcador.
     *
     * @param id Identificador do marcador
     * @param ativo Novo status
     */
    suspend fun atualizarStatus(id: Long, ativo: Boolean)
}
