// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/Emprego.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.LocalDateTime

/**
 * Modelo de domínio que representa um emprego/trabalho do usuário.
 *
 * Cada emprego é um workspace independente com seus próprios registros de ponto,
 * configurações de horário e banco de horas.
 *
 * @property id Identificador único do emprego (0 para novos registros)
 * @property nome Nome do emprego (ex: "Empresa ABC", "Freelance")
 * @property descricao Descrição opcional do emprego
 * @property ativo Indica se o emprego está ativo (visível na lista principal)
 * @property arquivado Indica se o emprego foi arquivado permanentemente
 * @property ordem Ordem de exibição na lista de empregos
 * @property criadoEm Timestamp de criação
 * @property atualizadoEm Timestamp da última atualização
 *
 * @author Thiago
 * @since 2.0.0
 */
data class Emprego(
    val id: Long = 0,
    val nome: String,
    val descricao: String? = null,
    val ativo: Boolean = true,
    val arquivado: Boolean = false,
    val ordem: Int = 0,
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Verifica se o emprego está visível (ativo e não arquivado).
     */
    val isVisivel: Boolean
        get() = ativo && !arquivado

    /**
     * Verifica se o emprego pode receber novos registros.
     */
    val podeRegistrarPonto: Boolean
        get() = ativo && !arquivado
}
