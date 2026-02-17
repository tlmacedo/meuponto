// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/MeuPontoTopBar.kt
package br.com.tlmacedo.meuponto.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

/**
 * TopAppBar customizada do aplicativo Meu Ponto.
 *
 * Barra superior com título centralizado e ações opcionais
 * de navegação e configurações, com design moderno.
 *
 * @param title Título a ser exibido
 * @param showBackButton Se deve exibir botão de voltar
 * @param showTodayButton Se deve exibir botão do dia atual (atalho para hoje)
 * @param showSettingsButton Se deve exibir botão de configurações
 * @param onBackClick Callback para ação de voltar
 * @param onTodayClick Callback para ir para o dia atual
 * @param onSettingsClick Callback para ação de configurações
 * @param modifier Modificador opcional
 *
 * @author Thiago
 * @since 1.0.0
 * @updated 2.5.0 - Botão de calendário substituído por ícone do dia atual
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeuPontoTopBar(
    title: String,
    showBackButton: Boolean = false,
    showTodayButton: Boolean = false,
    showSettingsButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onTodayClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            }
        },
        actions = {
            if (showTodayButton) {
                IconButton(onClick = onTodayClick) {
                    TodayDateIcon()
                }
            }
            if (showSettingsButton) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurações"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    )
}

/**
 * Ícone personalizado que exibe o dia atual do mês.
 * Funciona como atalho visual para "ir para hoje".
 */
@Composable
private fun TodayDateIcon() {
    val today = LocalDate.now().dayOfMonth

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = today.toString(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center
        )
    }
}
