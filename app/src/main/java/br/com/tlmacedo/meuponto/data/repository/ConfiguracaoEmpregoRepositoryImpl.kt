// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/repository/ConfiguracaoEmpregoRepositoryImpl.kt
package br.com.tlmacedo.meuponto.data.repository

import br.com.tlmacedo.meuponto.data.local.database.dao.ConfiguracaoEmpregoDao
import br.com.tlmacedo.meuponto.data.local.database.entity.toDomain
import br.com.tlmacedo.meuponto.data.local.database.entity.toEntity
import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import br.com.tlmacedo.meuponto.domain.repository.ConfiguracaoEmpregoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação concreta do repositório de configurações de emprego.
 *
 * @property configuracaoEmpregoDao DAO do Room para operações de banco de dados
 *
 * @author Thiago
 * @since 2.0.0
 */
@Singleton
class ConfiguracaoEmpregoRepositoryImpl @Inject constructor(
    private val configuracaoEmpregoDao: ConfiguracaoEmpregoDao
) : ConfiguracaoEmpregoRepository {

    // ========================================================================
    // Operações de Escrita (CRUD)
    // ========================================================================

    override suspend fun inserir(configuracao: ConfiguracaoEmprego): Long {
        return configuracaoEmpregoDao.inserir(configuracao.toEntity())
    }

    override suspend fun atualizar(configuracao: ConfiguracaoEmprego) {
        configuracaoEmpregoDao.atualizar(configuracao.toEntity())
    }

    override suspend fun excluir(configuracao: ConfiguracaoEmprego) {
        configuracaoEmpregoDao.excluir(configuracao.toEntity())
    }

    // ========================================================================
    // Operações de Leitura
    // ========================================================================

    override suspend fun buscarPorId(id: Long): ConfiguracaoEmprego? {
        return configuracaoEmpregoDao.buscarPorId(id)?.toDomain()
    }

    override suspend fun buscarPorEmpregoId(empregoId: Long): ConfiguracaoEmprego? {
        return configuracaoEmpregoDao.buscarPorEmpregoId(empregoId)?.toDomain()
    }

    override suspend fun existeParaEmprego(empregoId: Long): Boolean {
        return configuracaoEmpregoDao.existeParaEmprego(empregoId)
    }

    override suspend fun isNsrHabilitado(empregoId: Long): Boolean {
        return configuracaoEmpregoDao.isNsrHabilitado(empregoId) ?: false
    }

    override suspend fun isLocalizacaoHabilitada(empregoId: Long): Boolean {
        return configuracaoEmpregoDao.isLocalizacaoHabilitada(empregoId) ?: false
    }

    override suspend fun buscarPeriodoBancoHoras(empregoId: Long): Int {
        return configuracaoEmpregoDao.buscarPeriodoBancoHoras(empregoId) ?: 0
    }

    // ========================================================================
    // Operações Reativas (Flows)
    // ========================================================================

    override fun observarPorEmpregoId(empregoId: Long): Flow<ConfiguracaoEmprego?> {
        return configuracaoEmpregoDao.observarPorEmpregoId(empregoId).map { it?.toDomain() }
    }

    // ========================================================================
    // Atualizações Parciais
    // ========================================================================

    override suspend fun atualizarUltimoFechamentoBanco(empregoId: Long, data: LocalDate) {
        configuracaoEmpregoDao.atualizarUltimoFechamentoBanco(
            empregoId = empregoId,
            data = data.toString(),
            atualizadoEm = LocalDateTime.now().toString()
        )
    }
}
