package br.com.tlmacedo.meuponto.presentation.components.swipe

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Representa uma ação disponível no gesto de swipe.
 *
 * @param id Identificador único da ação
 * @param icon Ícone a ser exibido
 * @param label Texto descritivo da ação (usado para acessibilidade)
 * @param backgroundColor Cor de fundo do botão de ação
 * @param iconTint Cor do ícone (padrão: branco)
 * @param weight Peso relativo para distribuição de espaço entre múltiplas ações
 * @param enabled Se a ação está habilitada
 * @param onClick Callback executado ao acionar a ação
 */
data class SwipeAction(
    val id: String,
    val icon: ImageVector,
    val label: String,
    val backgroundColor: Color,
    val iconTint: Color = Color.White,
    val weight: Float = 1f,
    val enabled: Boolean = true,
    val onClick: () -> Unit
)

/**
 * Direção do gesto de swipe
 */
enum class SwipeDirection {
    /** Swipe da esquerda para direita - revela ações à esquerda */
    START_TO_END,
    /** Swipe da direita para esquerda - revela ações à direita */
    END_TO_START
}

/**
 * Estado atual do swipe
 */
enum class SwipeState {
    /** Posição inicial/fechada */
    COLLAPSED,
    /** Ações do início (esquerda em LTR) reveladas */
    EXPANDED_START,
    /** Ações do fim (direita em LTR) reveladas */
    EXPANDED_END
}
