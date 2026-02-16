// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/migration/Migration_5_6.kt
package br.com.tlmacedo.meuponto.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration da versão 5 para 6 do banco de dados.
 * Adiciona a coluna zerarBancoAntesPeriodo na tabela configuracoes_emprego.
 *
 * @author Thiago
 * @since 2.3.2
 */
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        if (!columnExists(db, "configuracoes_emprego", "zerarBancoAntesPeriodo")) {
            db.execSQL("ALTER TABLE configuracoes_emprego ADD COLUMN zerarBancoAntesPeriodo INTEGER NOT NULL DEFAULT 0")
        }
    }
}
