// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/migration/Migration_21_22.kt
package br.com.tlmacedo.meuponto.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migração 21 → 22: Adiciona configuração de foto obrigatória no emprego.
 *
 * @author Thiago
 * @since 9.0.0
 */
val MIGRATION_21_22 = object : Migration(21, 22) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE configuracoes_emprego ADD COLUMN fotoObrigatoria INTEGER NOT NULL DEFAULT 0")
    }
}
