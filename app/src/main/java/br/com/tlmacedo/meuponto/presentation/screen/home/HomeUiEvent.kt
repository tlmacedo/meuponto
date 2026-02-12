// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/home/HomeUiEvent.kt
package br.com.tlmacedo.meuponto.presentation.screen.home

/**
 * Sealed class que representa eventos únicos da tela Home.
 *
 * Eventos que devem ser consumidos uma única vez pela UI,
 * como navegação, snackbars e toasts.
 *
 * @author Thiago
 * @since 1.0.0
 */
sealed class HomeUiEvent {

    /**
     * Evento para exibir mensagem em Snackbar.
     *
     * @property message Mensagem a ser exibida
     */
    data class ShowSnackbar(val message: String) : HomeUiEvent()

    /**
     * Evento para navegar para tela de edição de ponto.
     *
     * @property pontoId ID do ponto a ser editado
     */
    data class NavigateToEditPonto(val pontoId: Long) : HomeUiEvent()

    /**
     * Evento para navegar para tela de histórico.
     */
    data object NavigateToHistory : HomeUiEvent()

    /**
     * Evento indicando que o ponto foi registrado com sucesso.
     *
     * @property message Mensagem de confirmação
     */
    data class PontoRegistrado(val message: String) : HomeUiEvent()

    /**
     * Evento indicando que o ponto foi excluído com sucesso.
     */
    data object PontoExcluido : HomeUiEvent()
}
