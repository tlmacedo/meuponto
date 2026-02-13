// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/di/RepositoryModule.kt
package br.com.tlmacedo.meuponto.di

import br.com.tlmacedo.meuponto.data.repository.AjusteSaldoRepositoryImpl
import br.com.tlmacedo.meuponto.data.repository.AuditLogRepositoryImpl
import br.com.tlmacedo.meuponto.data.repository.ConfiguracaoEmpregoRepositoryImpl
import br.com.tlmacedo.meuponto.data.repository.EmpregoRepositoryImpl
import br.com.tlmacedo.meuponto.data.repository.FechamentoPeriodoRepositoryImpl
import br.com.tlmacedo.meuponto.data.repository.HorarioDiaSemanaRepositoryImpl
import br.com.tlmacedo.meuponto.data.repository.MarcadorRepositoryImpl
import br.com.tlmacedo.meuponto.data.repository.PontoRepositoryImpl
import br.com.tlmacedo.meuponto.domain.repository.AjusteSaldoRepository
import br.com.tlmacedo.meuponto.domain.repository.AuditLogRepository
import br.com.tlmacedo.meuponto.domain.repository.ConfiguracaoEmpregoRepository
import br.com.tlmacedo.meuponto.domain.repository.EmpregoRepository
import br.com.tlmacedo.meuponto.domain.repository.FechamentoPeriodoRepository
import br.com.tlmacedo.meuponto.domain.repository.HorarioDiaSemanaRepository
import br.com.tlmacedo.meuponto.domain.repository.MarcadorRepository
import br.com.tlmacedo.meuponto.domain.repository.PontoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para injeção de dependências dos repositórios.
 *
 * Vincula as interfaces de repositório às suas implementações concretas,
 * seguindo o princípio de inversão de dependência (DIP). Todas as dependências
 * são fornecidas como singletons no escopo da aplicação.
 *
 * @author Thiago
 * @since 1.0.0
 * @updated 2.0.0 - Adicionados repositórios para múltiplos empregos, configurações,
 *                  horários, marcadores, ajustes de saldo, fechamentos e auditoria
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // ========================================================================
    // Repositórios Principais
    // ========================================================================

    /**
     * Vincula a implementação do PontoRepository.
     *
     * @param impl Implementação concreta do repositório
     * @return Interface PontoRepository
     */
    @Binds
    @Singleton
    abstract fun bindPontoRepository(
        impl: PontoRepositoryImpl
    ): PontoRepository

    /**
     * Vincula a implementação do EmpregoRepository.
     *
     * @param impl Implementação concreta do repositório
     * @return Interface EmpregoRepository
     */
    @Binds
    @Singleton
    abstract fun bindEmpregoRepository(
        impl: EmpregoRepositoryImpl
    ): EmpregoRepository

    // ========================================================================
    // Repositórios de Configuração
    // ========================================================================

    /**
     * Vincula a implementação do ConfiguracaoEmpregoRepository.
     *
     * @param impl Implementação concreta do repositório
     * @return Interface ConfiguracaoEmpregoRepository
     */
    @Binds
    @Singleton
    abstract fun bindConfiguracaoEmpregoRepository(
        impl: ConfiguracaoEmpregoRepositoryImpl
    ): ConfiguracaoEmpregoRepository

    /**
     * Vincula a implementação do HorarioDiaSemanaRepository.
     *
     * @param impl Implementação concreta do repositório
     * @return Interface HorarioDiaSemanaRepository
     */
    @Binds
    @Singleton
    abstract fun bindHorarioDiaSemanaRepository(
        impl: HorarioDiaSemanaRepositoryImpl
    ): HorarioDiaSemanaRepository

    // ========================================================================
    // Repositórios de Banco de Horas
    // ========================================================================

    /**
     * Vincula a implementação do AjusteSaldoRepository.
     *
     * @param impl Implementação concreta do repositório
     * @return Interface AjusteSaldoRepository
     */
    @Binds
    @Singleton
    abstract fun bindAjusteSaldoRepository(
        impl: AjusteSaldoRepositoryImpl
    ): AjusteSaldoRepository

    /**
     * Vincula a implementação do FechamentoPeriodoRepository.
     *
     * @param impl Implementação concreta do repositório
     * @return Interface FechamentoPeriodoRepository
     */
    @Binds
    @Singleton
    abstract fun bindFechamentoPeriodoRepository(
        impl: FechamentoPeriodoRepositoryImpl
    ): FechamentoPeriodoRepository

    // ========================================================================
    // Repositórios Auxiliares
    // ========================================================================

    /**
     * Vincula a implementação do MarcadorRepository.
     *
     * @param impl Implementação concreta do repositório
     * @return Interface MarcadorRepository
     */
    @Binds
    @Singleton
    abstract fun bindMarcadorRepository(
        impl: MarcadorRepositoryImpl
    ): MarcadorRepository

    /**
     * Vincula a implementação do AuditLogRepository.
     *
     * @param impl Implementação concreta do repositório
     * @return Interface AuditLogRepository
     */
    @Binds
    @Singleton
    abstract fun bindAuditLogRepository(
        impl: AuditLogRepositoryImpl
    ): AuditLogRepository
}
