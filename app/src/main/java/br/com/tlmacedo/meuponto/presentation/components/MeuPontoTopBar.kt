// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/MeuPontoTopBar.kt
package br.com.tlmacedo.meuponto.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * TopBar customizada do app Meu Ponto.
 *
 * @author Thiago
 * @since 3.0.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeuPontoTopBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    showTodayButton: Boolean = false,
    showHistoryButton: Boolean = false,
    showSettingsButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onTodayClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit)? = null,  // NOVO PARÂMETRO
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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
            // Ações customizadas (se fornecidas)
            actions?.invoke(this)

            // Botões padrão
            if (showTodayButton) {
                IconButton(onClick = onTodayClick) {
                    Icon(
                        imageVector = Icons.Default.Today,
                        contentDescription = "Hoje"
                    )
                }
            }
            if (showHistoryButton) {
                IconButton(onClick = onHistoryClick) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Histórico"
                    )
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
        modifier = modifier
    )
}
