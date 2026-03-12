package br.com.tlmacedo.meuponto.presentation.components.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Container que permite revelar ações através de gestos de swipe horizontal.
 *
 * Suporta ações em ambos os lados (início e fim) com animações suaves
 * e feedback visual adequado.
 *
 * @param modifier Modificador do container
 * @param startActions Ações reveladas ao fazer swipe para a direita (start -> end)
 * @param endActions Ações reveladas ao fazer swipe para a esquerda (end -> start)
 * @param actionWidth Largura de cada botão de ação
 * @param swipeThreshold Porcentagem mínima de arraste para completar o swipe (0.0 - 1.0)
 * @param enabled Se os gestos de swipe estão habilitados
 * @param onSwipeStateChange Callback quando o estado do swipe muda
 * @param content Conteúdo principal do item
 */
@Composable
fun SwipeableItemContainer(
    modifier: Modifier = Modifier,
    startActions: List<SwipeAction> = emptyList(),
    endActions: List<SwipeAction> = emptyList(),
    actionWidth: Int = 72,
    swipeThreshold: Float = 0.4f,
    enabled: Boolean = true,
    onSwipeStateChange: ((SwipeState) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    
    // Largura total das ações em cada lado
    val startActionsWidth = actionWidth * startActions.size
    val endActionsWidth = actionWidth * endActions.size
    
    // Estado do offset horizontal
    val offsetX = remember { Animatable(0f) }
    var containerWidth by remember { mutableFloatStateOf(0f) }
    var currentSwipeState by remember { mutableStateOf(SwipeState.COLLAPSED) }
    
    // Notifica mudanças de estado
    LaunchedEffect(currentSwipeState) {
        onSwipeStateChange?.invoke(currentSwipeState)
    }
    
    // Função para animar para uma posição específica
    suspend fun animateTo(targetOffset: Float, newState: SwipeState) {
        offsetX.animateTo(
            targetValue = targetOffset,
            animationSpec = tween(durationMillis = 250)
        )
        currentSwipeState = newState
    }
    
    // Função para fechar o swipe
    suspend fun collapse() {
        animateTo(0f, SwipeState.COLLAPSED)
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .onSizeChanged { containerWidth = it.width.toFloat() }
    ) {
        // Camada de ações de fundo (reveladas pelo swipe)
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Ações do lado esquerdo (start)
            if (startActions.isNotEmpty()) {
                SwipeActionsRow(
                    actions = startActions,
                    actionWidth = actionWidth,
                    alignment = Alignment.CenterStart
                )
            }
            
            // Espaçador
            Box(modifier = Modifier.weight(1f))
            
            // Ações do lado direito (end)
            if (endActions.isNotEmpty()) {
                SwipeActionsRow(
                    actions = endActions,
                    actionWidth = actionWidth,
                    alignment = Alignment.CenterEnd
                )
            }
        }
        
        // Conteúdo principal (desliza sobre as ações)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(enabled, startActions, endActions) {
                    if (!enabled || (startActions.isEmpty() && endActions.isEmpty())) return@pointerInput
                    
                    detectHorizontalDragGestures(
                        onDragStart = { },
                        onDragEnd = {
                            scope.launch {
                                val currentOffset = offsetX.value
                                
                                when {
                                    // Swipe para direita (revela ações do início)
                                    currentOffset > 0 && startActions.isNotEmpty() -> {
                                        val threshold = startActionsWidth * swipeThreshold
                                        if (currentOffset > threshold) {
                                            animateTo(startActionsWidth.toFloat(), SwipeState.EXPANDED_START)
                                        } else {
                                            collapse()
                                        }
                                    }
                                    // Swipe para esquerda (revela ações do fim)
                                    currentOffset < 0 && endActions.isNotEmpty() -> {
                                        val threshold = endActionsWidth * swipeThreshold
                                        if (currentOffset.absoluteValue > threshold) {
                                            animateTo(-endActionsWidth.toFloat(), SwipeState.EXPANDED_END)
                                        } else {
                                            collapse()
                                        }
                                    }
                                    else -> collapse()
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch { collapse() }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            
                            scope.launch {
                                val newOffset = offsetX.value + dragAmount
                                
                                // Limita o arraste baseado nas ações disponíveis
                                val maxStart = if (startActions.isNotEmpty()) startActionsWidth.toFloat() else 0f
                                val maxEnd = if (endActions.isNotEmpty()) -endActionsWidth.toFloat() else 0f
                                
                                // Aplica resistência quando ultrapassa os limites
                                val constrainedOffset = when {
                                    newOffset > maxStart -> maxStart + (newOffset - maxStart) * 0.2f
                                    newOffset < maxEnd -> maxEnd + (newOffset - maxEnd) * 0.2f
                                    else -> newOffset
                                }
                                
                                offsetX.snapTo(constrainedOffset)
                            }
                        }
                    )
                },
            content = content
        )
    }
}

/**
 * Linha de botões de ação para o swipe
 */
@Composable
private fun SwipeActionsRow(
    actions: List<SwipeAction>,
    actionWidth: Int,
    alignment: Alignment
) {
    Row(
        modifier = Modifier.fillMaxHeight(),
        horizontalArrangement = if (alignment == Alignment.CenterStart) {
            Arrangement.Start
        } else {
            Arrangement.End
        }
    ) {
        actions.forEach { action ->
            SwipeActionButton(
                action = action,
                width = actionWidth
            )
        }
    }
}

/**
 * Botão individual de ação no swipe
 */
@Composable
private fun SwipeActionButton(
    action: SwipeAction,
    width: Int
) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .fillMaxHeight()
            .background(action.backgroundColor)
            .semantics { contentDescription = action.label },
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = action.onClick,
            enabled = action.enabled,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                tint = if (action.enabled) action.iconTint else action.iconTint.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
