// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/migration/Migration_6_7.kt
package br.com.tlmacedo.meuponto.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration da versão 6 para 7 do banco de dados.
 * Adiciona a coluna dataInicioTrabalho na tabela empregos.
 *
 * @author Thiago
 * @since 2.3.3
 */
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        if (!columnExists(db, "empregos", "dataInicioTrabalho")) {
            db.execSQL("ALTER TABLE empregos ADD COLUMN dataInicioTrabalho TEXT DEFAULT NULL")
        }
    }
}
