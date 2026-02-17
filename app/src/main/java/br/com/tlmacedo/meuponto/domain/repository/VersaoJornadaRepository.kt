// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/repository/VersaoJornadaRepository.kt
package br.com.tlmacedo.meuponto.domain.repository

import br.com.tlmacedo.meuponto.domain.model.VersaoJornada
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interface do repositório de versões de jornada.
 *
 * @author Thiago
 * @since 2.7.0
 */
interface VersaoJornadaRepository {

    // ========================================================================
    // Operações de Escrita (CRUD)
    // ========================================================================

    suspend fun inserir(versao: VersaoJornada): Long
    suspend fun atualizar(versao: VersaoJornada)
    suspend fun excluir(versao: VersaoJornada)
    suspend fun excluirPorId(id: Long)

    // ========================================================================
    // Operações de Leitura
    // ========================================================================

    suspend fun buscarPorId(id: Long): VersaoJornada?
    fun observarPorId(id: Long): Flow<VersaoJornada?>
    suspend fun buscarPorEmprego(empregoId: Long): List<VersaoJornada>
    fun observarPorEmprego(empregoId: Long): Flow<List<VersaoJornada>>
    suspend fun buscarVigente(empregoId: Long): VersaoJornada?
    fun observarVigente(empregoId: Long): Flow<VersaoJornada?>

    // ========================================================================
    // Consultas por Data
    // ========================================================================

    /**
     * Busca a versão de jornada vigente para uma data específica.
     */
    suspend fun buscarPorEmpregoEData(empregoId: Long, data: LocalDate): VersaoJornada?
    fun observarPorEmpregoEData(empregoId: Long, data: LocalDate): Flow<VersaoJornada?>

    // ========================================================================
    // Operações de Versionamento
    // ========================================================================

    /**
     * Cria uma nova versão de jornada.
     * Automaticamente fecha a versão anterior (define dataFim).
     *
     * @return ID da nova versão criada
     */
    suspend fun criarNovaVersao(
        empregoId: Long,
        dataInicio: LocalDate,
        descricao: String? = null,
        copiarDaVersaoAnterior: Boolean = true
    ): Long

    /**
     * Verifica se existe sobreposição de períodos.
     */
    suspend fun existeSobreposicao(
        empregoId: Long,
        dataInicio: LocalDate,
        dataFim: LocalDate?,
        excluirId: Long = 0
    ): Boolean

    /**
     * Busca a versão anterior a uma data.
     */
    suspend fun buscarVersaoAnterior(empregoId: Long, data: LocalDate): VersaoJornada?

    /**
     * Busca a próxima versão após uma data.
     */
    suspend fun buscarProximaVersao(empregoId: Long, data: LocalDate): VersaoJornada?

    // ========================================================================
    // Contagens
    // ========================================================================

    suspend fun contarPorEmprego(empregoId: Long): Int
}
