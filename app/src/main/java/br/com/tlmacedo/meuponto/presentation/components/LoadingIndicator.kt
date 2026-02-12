// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/LoadingIndicator.kt
package br.com.tlmacedo.meuponto.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Indicador de carregamento centralizado.
 *
 * Exibe um CircularProgressIndicator no centro do container pai.
 * Utilizado durante operações assíncronas.
 *
 * @param modifier Modificador opcional para customização do layout
 *
 * @author Thiago
 * @since 1.0.0
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
    }
}
