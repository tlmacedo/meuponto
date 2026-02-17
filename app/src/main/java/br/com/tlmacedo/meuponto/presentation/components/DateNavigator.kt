// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/DateNavigator.kt
package br.com.tlmacedo.meuponto.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Componente para navegação entre datas.
 *
 * Exibe a data atual com setas para navegar entre dias.
 * - Seta esquerda: sempre volta um dia
 * - Seta direita: sempre avança um dia
 * - Clique na data: abre seletor de calendário
 *
 * @param dataFormatada Data formatada para exibição principal
 * @param dataFormatadaCurta Data em formato curto (dd/MM/yyyy)
 * @param isHoje Indica se a data selecionada é hoje
 * @param podeNavegarAnterior Se pode navegar para dia anterior
 * @param podeNavegarProximo Se pode navegar para próximo dia
 * @param onDiaAnterior Callback para navegar ao dia anterior
 * @param onProximoDia Callback para navegar ao próximo dia
 * @param onSelecionarData Callback para abrir seletor de data
 * @param modifier Modificador opcional
 *
 * @author Thiago
 * @since 2.0.0
 * @updated 2.5.0 - Navegação sempre dia a dia, clique na data abre calendário
 * @updated 2.6.0 - Data curta sempre visível
 */
@Composable
fun DateNavigator(
    dataFormatada: String,
    dataFormatadaCurta: String,
    isHoje: Boolean,
    podeNavegarAnterior: Boolean,
    podeNavegarProximo: Boolean,
    onDiaAnterior: () -> Unit,
    onProximoDia: () -> Unit,
    onSelecionarData: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Botão dia anterior - sempre navega para trás
            IconButton(
                onClick = onDiaAnterior,
                enabled = podeNavegarAnterior
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Dia anterior",
                    tint = if (podeNavegarAnterior) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    },
                    modifier = Modifier.size(32.dp)
                )
            }

            // Data central - clicável para abrir calendário
            AnimatedContent(
                targetState = dataFormatada to dataFormatadaCurta,
                transitionSpec = {
                    (slideInHorizontally { width -> width } + fadeIn())
                        .togetherWith(slideOutHorizontally { width -> -width } + fadeOut())
                },
                label = "date_animation",
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelecionarData() }
            ) { (dataLonga, dataCurta) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dataLonga,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    // Sempre mostrar a data curta
                    Text(
                        text = dataCurta,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Botão próximo dia - sempre navega para frente
            IconButton(
                onClick = onProximoDia,
                enabled = podeNavegarProximo
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Próximo dia",
                    tint = if (podeNavegarProximo) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    },
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
