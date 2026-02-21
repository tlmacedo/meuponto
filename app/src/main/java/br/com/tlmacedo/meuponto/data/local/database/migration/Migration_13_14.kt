// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/migration/Migration_13_14.kt
package br.com.tlmacedo.meuponto.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migração da versão 13 para 14.
 *
 * Adiciona novos campos na tabela ausencias para suportar:
 * - DECLARACAO: horaInicio, duracaoDeclaracaoMinutos, duracaoAbonoMinutos
 * - FOLGA: subTipoFolga
 * - FERIAS: periodoAquisitivo
 * - Anexos: imagemUri
 *
 * @author Thiago
 * @since 5.4.0
 */
val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Adicionar coluna horaInicio (LocalTime armazenado como TEXT)
        db.execSQL(
            "ALTER TABLE ausencias ADD COLUMN horaInicio TEXT DEFAULT NULL"
        )

        // Adicionar coluna duracaoDeclaracaoMinutos (INTEGER)
        db.execSQL(
            "ALTER TABLE ausencias ADD COLUMN duracaoDeclaracaoMinutos INTEGER DEFAULT NULL"
        )

        // Adicionar coluna duracaoAbonoMinutos (INTEGER)
        db.execSQL(
            "ALTER TABLE ausencias ADD COLUMN duracaoAbonoMinutos INTEGER DEFAULT NULL"
        )

        // Adicionar coluna subTipoFolga (TEXT - enum SubTipoFolga)
        db.execSQL(
            "ALTER TABLE ausencias ADD COLUMN subTipoFolga TEXT DEFAULT NULL"
        )

        // Adicionar coluna periodoAquisitivo (TEXT)
        db.execSQL(
            "ALTER TABLE ausencias ADD COLUMN periodoAquisitivo TEXT DEFAULT NULL"
        )

        // Adicionar coluna imagemUri (TEXT)
        db.execSQL(
            "ALTER TABLE ausencias ADD COLUMN imagemUri TEXT DEFAULT NULL"
        )

        // Migrar dados existentes de FOLGA para ter subTipoFolga = BANCO_HORAS (default)
        db.execSQL(
            "UPDATE ausencias SET subTipoFolga = 'BANCO_HORAS' WHERE tipo = 'FOLGA'"
        )
    }
}
