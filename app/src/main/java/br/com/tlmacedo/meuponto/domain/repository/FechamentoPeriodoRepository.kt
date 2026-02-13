// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/repository/FechamentoPeriodoRepository.kt
package br.com.tlmacedo.meuponto.domain.repository

import br.com.tlmacedo.meuponto.domain.model.FechamentoPeriodo
import br.com.tlmacedo.meuponto.domain.model.TipoFechamento
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interface do repositório de fechamentos de período.
 *
 * Define o contrato para operações de persistência dos fechamentos de período,
 * onde o saldo é zerado e registrado para histórico.
 *
 * @author Thiago
 * @since 2.0.0
 */
interface FechamentoPeriodoRepository {

    // ========================================================================
    // Operações de Escrita (CRUD)
    // ========================================================================

    /**
     * Insere um novo fechamento de período.
     *
     * @param fechamento Fechamento a ser inserido
     * @return ID gerado para o novo registro
     */
    suspend fun inserir(fechamento: FechamentoPeriodo): Long

    /**
     * Atualiza um fechamento existente.
     *
     * @param fechamento Fechamento com os dados atualizados
     */
    suspend fun atualizar(fechamento: FechamentoPeriodo)

    /**
     * Remove um fechamento.
     *
     * @param fechamento Fechamento a ser removido
     */
    suspend fun excluir(fechamento: FechamentoPeriodo)

    /**
     * Remove um fechamento pelo ID.
     *
     * @param id Identificador do fechamento
     */
    suspend fun excluirPorId(id: Long)

    // ========================================================================
    // Operações de Leitura
    // ========================================================================

    /**
     * Busca um fechamento pelo ID.
     *
     * @param id Identificador único do fechamento
     * @return Fechamento encontrado ou null
     */
    suspend fun buscarPorId(id: Long): FechamentoPeriodo?

    /**
     * Busca todos os fechamentos de um emprego.
     *
     * @param empregoId Identificador do emprego
     * @return Lista de fechamentos ordenada por data decrescente
     */
    suspend fun buscarPorEmprego(empregoId: Long): List<FechamentoPeriodo>

    /**
     * Busca os fechamentos de um tipo específico.
     *
     * @param empregoId Identificador do emprego
     * @param tipo Tipo de fechamento
     * @return Lista de fechamentos do tipo
     */
    suspend fun buscarPorTipo(empregoId: Long, tipo: TipoFechamento): List<FechamentoPeriodo>

    /**
     * Busca um fechamento por período específico.
     *
     * @param empregoId Identificador do emprego
     * @param dataInicio Data inicial do período
     * @param dataFim Data final do período
     * @return Fechamento encontrado ou null
     */
    suspend fun buscarPorPeriodo(empregoId: Long, dataInicio: LocalDate, dataFim: LocalDate): FechamentoPeriodo?

    /**
     * Busca o último fechamento de um emprego.
     *
     * @param empregoId Identificador do emprego
     * @return Último fechamento ou null
     */
    suspend fun buscarUltimoFechamento(empregoId: Long): FechamentoPeriodo?

    /**
     * Busca o último fechamento de um tipo específico.
     *
     * @param empregoId Identificador do emprego
     * @param tipo Tipo de fechamento
     * @return Último fechamento do tipo ou null
     */
    suspend fun buscarUltimoFechamentoPorTipo(empregoId: Long, tipo: TipoFechamento): FechamentoPeriodo?

    // ========================================================================
    // Verificações
    // ========================================================================

    /**
     * Verifica se existe fechamento em um período específico.
     *
     * @param empregoId Identificador do emprego
     * @param dataInicio Data inicial do período
     * @param dataFim Data final do período
     * @return true se existe, false caso contrário
     */
    suspend fun existeFechamentoNoPeriodo(empregoId: Long, dataInicio: LocalDate, dataFim: LocalDate): Boolean

    /**
     * Verifica se existe algum fechamento para um emprego.
     *
     * @param empregoId Identificador do emprego
     * @return true se existe, false caso contrário
     */
    suspend fun existeFechamentoPorEmprego(empregoId: Long): Boolean

    // ========================================================================
    // Cálculos
    // ========================================================================

    /**
     * Busca o saldo anterior do último fechamento.
     *
     * @param empregoId Identificador do emprego
     * @return Saldo em minutos ou null se não houver fechamento
     */
    suspend fun buscarUltimoSaldoAnterior(empregoId: Long): Int?

    /**
     * Soma todos os saldos anteriores dos fechamentos.
     *
     * @param empregoId Identificador do emprego
     * @return Total de saldos anteriores
     */
    suspend fun somarSaldosAnteriores(empregoId: Long): Int

    /**
     * Conta o número de fechamentos de um emprego.
     *
     * @param empregoId Identificador do emprego
     * @return Quantidade de fechamentos
     */
    suspend fun contarPorEmprego(empregoId: Long): Int

    // ========================================================================
    // Operações Reativas (Flows)
    // ========================================================================

    /**
     * Observa os fechamentos de um emprego de forma reativa.
     *
     * @param empregoId Identificador do emprego
     * @return Flow que emite a lista sempre que houver mudanças
     */
    fun observarPorEmprego(empregoId: Long): Flow<List<FechamentoPeriodo>>

    /**
     * Observa os fechamentos de um tipo específico de forma reativa.
     *
     * @param empregoId Identificador do emprego
     * @param tipo Tipo de fechamento
     * @return Flow que emite a lista do tipo
     */
    fun observarPorTipo(empregoId: Long, tipo: TipoFechamento): Flow<List<FechamentoPeriodo>>

    /**
     * Observa o último fechamento de um emprego de forma reativa.
     *
     * @param empregoId Identificador do emprego
     * @return Flow que emite o último fechamento
     */
    fun observarUltimoFechamento(empregoId: Long): Flow<FechamentoPeriodo?>

    /**
     * Observa os últimos fechamentos de um emprego.
     *
     * @param empregoId Identificador do emprego
     * @param limite Quantidade máxima de fechamentos
     * @return Flow que emite os últimos fechamentos
     */
    fun observarUltimos(empregoId: Long, limite: Int): Flow<List<FechamentoPeriodo>>
}
