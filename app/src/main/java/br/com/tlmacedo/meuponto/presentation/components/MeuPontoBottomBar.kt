// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/MeuPontoBottomBar.kt
package br.com.tlmacedo.meuponto.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.tlmacedo.meuponto.presentation.navigation.BottomNavItem

/**
 * Barra de navegação inferior do aplicativo.
 *
 * Exibe os itens de navegação principais permitindo ao usuário
 * alternar entre as telas Home, Histórico e Configurações.
 *
 * @param currentRoute Rota atual para destacar o item selecionado
 * @param onNavigate Callback chamado quando um item é clicado
 * @param modifier Modificador opcional para customização do layout
 *
 * @author Thiago
 * @since 1.0.0
 */
@Composable
fun MeuPontoBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    ) {
        BottomNavItem.getItems().forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
