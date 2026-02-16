// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/di/DatabaseModule.kt
package br.com.tlmacedo.meuponto.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.tlmacedo.meuponto.data.local.database.MeuPontoDatabase
import br.com.tlmacedo.meuponto.data.local.database.dao.*
import br.com.tlmacedo.meuponto.data.local.database.migration.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.LocalDateTime
import javax.inject.Singleton

/**
 * Módulo Hilt para injeção de dependências relacionadas ao banco de dados.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMeuPontoDatabase(
        @ApplicationContext context: Context
    ): MeuPontoDatabase {
        return Room.databaseBuilder(
            context,
            MeuPontoDatabase::class.java,
            MeuPontoDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
            .addCallback(createDatabaseCallback())
            .build()
    }

    private fun createDatabaseCallback(): RoomDatabase.Callback {
        return object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                inserirDadosIniciais(db)
            }
        }
    }

    private fun inserirDadosIniciais(db: SupportSQLiteDatabase) {
        val now = LocalDateTime.now().toString()

        // Inserir emprego padrão
        db.execSQL(
            """
            INSERT INTO empregos (
                id, nome, descricao, ativo, arquivado, ordem, criadoEm, atualizadoEm
            ) VALUES (
                1, 'Meu Emprego', 'Emprego padrão', 1, 0, 0, '$now', '$now'
            )
            """.trimIndent()
        )

        // Inserir configuração padrão do emprego
        db.execSQL(
            """
            INSERT INTO configuracoes_emprego (
                empregoId,
                cargaHorariaDiariaMinutos,
                jornadaMaximaDiariaMinutos,
                intervaloMinimoInterjornadaMinutos,
                intervaloMinimoMinutos,
                toleranciaEntradaMinutos,
                toleranciaSaidaMinutos,
                toleranciaIntervaloMaisMinutos,
                exigeJustificativaInconsistencia,
                habilitarNsr,
                tipoNsr,
                habilitarLocalizacao,
                localizacaoAutomatica,
                exibirLocalizacaoDetalhes,
                exibirDuracaoTurno,
                exibirDuracaoIntervalo,
                primeiroDiaSemana,
                primeiroDiaMes,
                zerarSaldoSemanal,
                zerarSaldoMensal,
                ocultarSaldoTotal,
                periodoBancoHorasMeses,
                diasUteisLembreteFechamento,
                habilitarSugestaoAjuste,
                zerarBancoAntesPeriodo,
                criadoEm,
                atualizadoEm
            ) VALUES (
                1, 492, 600, 660, 60, 10, 10, 0, 0, 0, 'NUMERICO', 0, 0, 1, 1, 1, 'SEGUNDA', 1, 0, 0, 0, 0, 3, 0, 0, '$now', '$now'
            )
            """.trimIndent()
        )
    }

    @Provides
    @Singleton
    fun providePontoDao(database: MeuPontoDatabase): PontoDao = database.pontoDao()

    @Provides
    @Singleton
    fun provideEmpregoDao(database: MeuPontoDatabase): EmpregoDao = database.empregoDao()

    @Provides
    @Singleton
    fun provideConfiguracaoEmpregoDao(database: MeuPontoDatabase): ConfiguracaoEmpregoDao = database.configuracaoEmpregoDao()

    @Provides
    @Singleton
    fun provideHorarioDiaSemanaDao(database: MeuPontoDatabase): HorarioDiaSemanaDao = database.horarioDiaSemanaDao()

    @Provides
    @Singleton
    fun provideAjusteSaldoDao(database: MeuPontoDatabase): AjusteSaldoDao = database.ajusteSaldoDao()

    @Provides
    @Singleton
    fun provideFechamentoPeriodoDao(database: MeuPontoDatabase): FechamentoPeriodoDao = database.fechamentoPeriodoDao()

    @Provides
    @Singleton
    fun provideMarcadorDao(database: MeuPontoDatabase): MarcadorDao = database.marcadorDao()

    @Provides
    @Singleton
    fun provideAuditLogDao(database: MeuPontoDatabase): AuditLogDao = database.auditLogDao()
}
