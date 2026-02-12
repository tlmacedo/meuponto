// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/entity/PontoEntity.kt
package br.com.tlmacedo.meuponto.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.model.TipoPonto
import java.time.LocalDateTime

/**
 * Entidade Room que representa um registro de ponto no banco de dados.
 *
 * Esta classe é responsável pelo mapeamento entre o modelo de domínio [Ponto]
 * e a tabela do banco de dados SQLite. Utiliza o padrão de conversão
 * bidirecional através dos métodos [toDomain] e [fromDomain].
 *
 * @property id Identificador único do registro (auto-gerado pelo Room)
 * @property dataHora Data e hora da batida de ponto
 * @property tipo Tipo da batida (ENTRADA ou SAIDA)
 * @property isEditadoManualmente Indica se o registro foi editado manualmente pelo usuário
 * @property observacao Observação opcional do usuário sobre o registro
 * @property criadoEm Data e hora de criação do registro no sistema
 * @property atualizadoEm Data e hora da última atualização do registro
 *
 * @author Thiago
 * @since 1.0.0
 */
@Entity(tableName = "pontos")
data class PontoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "data_hora")
    val dataHora: LocalDateTime,

    @ColumnInfo(name = "tipo")
    val tipo: TipoPonto,

    @ColumnInfo(name = "is_editado_manualmente")
    val isEditadoManualmente: Boolean = false,

    @ColumnInfo(name = "observacao")
    val observacao: String? = null,

    @ColumnInfo(name = "criado_em")
    val criadoEm: LocalDateTime = LocalDateTime.now(),

    @ColumnInfo(name = "atualizado_em")
    val atualizadoEm: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Converte a entidade do banco de dados para o modelo de domínio.
     *
     * @return Modelo de domínio [Ponto] correspondente
     */
    fun toDomain(): Ponto {
        return Ponto(
            id = id,
            dataHora = dataHora,
            tipo = tipo,
            isEditadoManualmente = isEditadoManualmente,
            observacao = observacao,
            criadoEm = criadoEm,
            atualizadoEm = atualizadoEm
        )
    }

    companion object {
        /**
         * Converte um modelo de domínio para entidade do banco de dados.
         *
         * @param ponto Modelo de domínio a ser convertido
         * @return Entidade [PontoEntity] para persistência no Room
         */
        fun fromDomain(ponto: Ponto): PontoEntity {
            return PontoEntity(
                id = ponto.id,
                dataHora = ponto.dataHora,
                tipo = ponto.tipo,
                isEditadoManualmente = ponto.isEditadoManualmente,
                observacao = ponto.observacao,
                criadoEm = ponto.criadoEm,
                atualizadoEm = ponto.atualizadoEm
            )
        }
    }
}
