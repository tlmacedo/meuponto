// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/repository/ConfiguracaoEmpregoRepository.kt
package br.com.tlmacedo.meuponto.domain.repository

import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interface do repositório de configurações de emprego.
 *
 * Define o contrato para operações de persistência das configurações
 * específicas de cada emprego.
 *
 * @author Thiago
 * @since 2.0.0
 */
interface ConfiguracaoEmpregoRepository {

    // ========================================================================
    // Operações de Escrita (CRUD)
    // ========================================================================

    /**
     * Insere uma nova configuração.
     *
     * @param configuracao Configuração a ser inserida
     * @return ID gerado para o novo registro
     */
    suspend fun inserir(configuracao: ConfiguracaoEmprego): Long

    /**
     * Atualiza uma configuração existente.
     *
     * @param configuracao Configuração com os dados atualizados
     */
    suspend fun atualizar(configuracao: ConfiguracaoEmprego)

    /**
     * Remove uma configuração.
     *
     * @param configuracao Configuração a ser removida
     */
    suspend fun excluir(configuracao: ConfiguracaoEmprego)

    // ========================================================================
    // Operações de Leitura
    // ========================================================================

    /**
     * Busca uma configuração pelo ID.
     *
     * @param id Identificador único da configuração
     * @return Configuração encontrada ou null
     */
    suspend fun buscarPorId(id: Long): ConfiguracaoEmprego?

    /**
     * Busca a configuração de um emprego específico.
     *
     * @param empregoId Identificador do emprego
     * @return Configuração encontrada ou null
     */
    suspend fun buscarPorEmpregoId(empregoId: Long): ConfiguracaoEmprego?

    /**
     * Verifica se existe configuração para um emprego.
     *
     * @param empregoId Identificador do emprego
     * @return true se existe, false caso contrário
     */
    suspend fun existeParaEmprego(empregoId: Long): Boolean

    /**
     * Verifica se o NSR está habilitado para um emprego.
     *
     * @param empregoId Identificador do emprego
     * @return true se habilitado, false caso contrário
     */
    suspend fun isNsrHabilitado(empregoId: Long): Boolean

    /**
     * Verifica se a localização está habilitada para um emprego.
     *
     * @param empregoId Identificador do emprego
     * @return true se habilitada, false caso contrário
     */
    suspend fun isLocalizacaoHabilitada(empregoId: Long): Boolean

    /**
     * Busca o período do banco de horas em meses.
     *
     * @param empregoId Identificador do emprego
     * @return Período em meses (0 = sem banco de horas)
     */
    suspend fun buscarPeriodoBancoHoras(empregoId: Long): Int

    // ========================================================================
    // Operações Reativas (Flows)
    // ========================================================================

    /**
     * Observa a configuração de um emprego de forma reativa.
     *
     * @param empregoId Identificador do emprego
     * @return Flow que emite a configuração sempre que houver mudanças
     */
    fun observarPorEmpregoId(empregoId: Long): Flow<ConfiguracaoEmprego?>

    // ========================================================================
    // Atualizações Parciais
    // ========================================================================

    /**
     * Atualiza a data do último fechamento do banco de horas.
     *
     * @param empregoId Identificador do emprego
     * @param data Data do fechamento
     */
    suspend fun atualizarUltimoFechamentoBanco(empregoId: Long, data: LocalDate)
}
