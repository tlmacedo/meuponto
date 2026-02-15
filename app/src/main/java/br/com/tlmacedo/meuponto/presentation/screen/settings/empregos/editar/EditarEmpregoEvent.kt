// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/empregos/editar/EditarEmpregoEvent.kt
package br.com.tlmacedo.meuponto.presentation.screen.settings.empregos.editar

/**
 * Eventos únicos emitidos pela tela de edição de emprego.
 *
 * Representa ações que devem ser executadas uma única vez,
 * como navegação ou exibição de mensagens.
 *
 * @author Thiago
 * @since 2.0.0
 */
sealed interface EditarEmpregoEvent {

    /**
     * Emprego salvo com sucesso - navegar de volta.
     *
     * @property mensagem Mensagem de sucesso para exibir
     */
    data class SalvoComSucesso(val mensagem: String) : EditarEmpregoEvent

    /**
     * Exibe uma mensagem de erro ao usuário.
     *
     * @property mensagem Texto do erro a ser exibido
     */
    data class MostrarErro(val mensagem: String) : EditarEmpregoEvent

    /**
     * Navega de volta para a tela anterior.
     */
    data object Voltar : EditarEmpregoEvent
}
