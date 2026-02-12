// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/home/HomeAction.kt
package br.com.tlmacedo.meuponto.presentation.screen.home

import br.com.tlmacedo.meuponto.domain.model.Ponto

/**
 * Sealed class que representa as ações possíveis na tela Home.
 *
 * Define todas as intenções do usuário que podem modificar
 * o estado da tela ou disparar operações.
 *
 * @author Thiago
 * @since 1.0.0
 */
sealed class HomeAction {

    /**
     * Ação para registrar um novo ponto.
     */
    data object RegistrarPonto : HomeAction()

    /**
     * Ação para excluir um ponto existente.
     *
     * @property ponto Ponto a ser excluído
     */
    data class ExcluirPonto(val ponto: Ponto) : HomeAction()

    /**
     * Ação para navegar para edição de ponto.
     *
     * @property pontoId ID do ponto a ser editado
     */
    data class EditarPonto(val pontoId: Long) : HomeAction()

    /**
     * Ação para atualizar o relógio.
     */
    data object AtualizarRelogio : HomeAction()

    /**
     * Ação para limpar mensagem de erro.
     */
    data object LimparErro : HomeAction()

    /**
     * Ação para recarregar dados da tela.
     */
    data object Recarregar : HomeAction()
}
