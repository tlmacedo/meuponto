// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/Marcador.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.LocalDateTime

/**
 * Modelo de domínio que representa um marcador/tag para categorizar registros de ponto.
 *
 * Permite criar etiquetas personalizadas como "Home Office", "Externo", "Plantão", etc.
 *
 * @property id Identificador único do marcador
 * @property empregoId FK para o emprego associado
 * @property nome Nome do marcador
 * @property cor Cor em formato hexadecimal (ex: "#FF5722")
 * @property icone Nome do ícone (opcional, para uso futuro)
 * @property ativo Se false, o marcador não aparece nas opções
 * @property ordem Ordem de exibição na lista
 * @property criadoEm Timestamp de criação
 * @property atualizadoEm Timestamp da última atualização
 *
 * @author Thiago
 * @since 2.0.0
 */
data class Marcador(
    val id: Long = 0,
    val empregoId: Long,
    val nome: String,
    val cor: String = "#2196F3",
    val icone: String? = null,
    val ativo: Boolean = true,
    val ordem: Int = 0,
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Verifica se o marcador está disponível para uso.
     */
    val isDisponivel: Boolean
        get() = ativo
}
