// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/navigation/Route.kt
package br.com.tlmacedo.meuponto.presentation.navigation

/**
 * Sealed class que define todas as rotas de navegação do aplicativo.
 *
 * Centraliza a definição das rotas para garantir type-safety na navegação
 * e facilitar a manutenção dos caminhos de tela.
 *
 * @property route String identificadora da rota para o NavController
 *
 * @author Thiago
 * @since 1.0.0
 */
sealed class Route(val route: String) {

    /**
     * Tela inicial com registro de ponto e resumo do dia.
     */
    data object Home : Route("home")

    /**
     * Tela de histórico de pontos registrados.
     */
    data object History : Route("history")

    /**
     * Tela de configurações do aplicativo.
     */
    data object Settings : Route("settings")

    /**
     * Tela de relatórios e estatísticas.
     */
    data object Reports : Route("reports")

    /**
     * Tela de edição de ponto específico.
     *
     * @param pontoId ID do ponto a ser editado
     */
    data object EditPonto : Route("edit_ponto/{pontoId}") {
        /**
         * Cria a rota com o ID do ponto.
         *
         * @param pontoId ID do ponto a ser editado
         * @return Rota formatada com o ID
         */
        fun createRoute(pontoId: Long): String = "edit_ponto/$pontoId"
    }

    companion object {
        /** Argumento para ID do ponto na navegação */
        const val ARG_PONTO_ID = "pontoId"
    }
}
