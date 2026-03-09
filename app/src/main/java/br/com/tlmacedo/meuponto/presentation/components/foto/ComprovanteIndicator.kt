// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/foto/ComprovanteIndicator.kt
package br.com.tlmacedo.meuponto.presentation.components.foto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Indicador compacto de que um registro de ponto possui foto de comprovante.
 *
 * Útil para exibir em cards de ponto ou listas onde o espaço é limitado.
 *
 * @param hasComprovante Se existe comprovante anexado
 * @param modifier Modificador do componente
 *
 * @author Thiago
 * @since 9.0.0
 */
@Composable
fun ComprovanteIndicator(
    hasComprovante: Boolean,
    modifier: Modifier = Modifier
) {
    if (!hasComprovante) return

    Box(
        modifier = modifier
            .size(24.dp)
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = CircleShape
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = "Possui comprovante",
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}
