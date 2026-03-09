// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/migration/Migration_20_21.kt
package br.com.tlmacedo.meuponto.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migração 20 → 21: Adiciona suporte a foto do comprovante de ponto.
 *
 * Adiciona coluna para armazenar o caminho da foto do comprovante
 * associada a cada registro de ponto.
 *
 * @author Thiago
 * @since 9.0.0
 */
val MIGRATION_20_21 = object : Migration(20, 21) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Adiciona coluna para caminho da foto do comprovante (nullable)
        db.execSQL("ALTER TABLE pontos ADD COLUMN fotoComprovantePath TEXT DEFAULT NULL")
    }
}
