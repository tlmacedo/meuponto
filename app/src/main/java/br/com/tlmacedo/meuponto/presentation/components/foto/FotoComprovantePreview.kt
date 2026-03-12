// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/foto/FotoComprovantePreview.kt
package br.com.tlmacedo.meuponto.presentation.components.foto

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Preview da foto de comprovante com opções de selecionar/remover.
 *
 * @author Thiago
 * @since 10.0.0
 */
@Composable
fun FotoComprovantePreview(
    fotoUri: Uri?,
    isObrigatorio: Boolean,
    onSelecionarClick: () -> Unit,
    onRemoverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Foto do Comprovante",
                    style = MaterialTheme.typography.titleSmall
                )
                if (isObrigatorio) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "*",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (fotoUri != null) {
                // Foto selecionada - mostrar preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(shape)
                ) {
                    AsyncImage(
                        model = fotoUri,
                        contentDescription = "Foto do comprovante",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Botão de remover
                    IconButton(
                        onClick = onRemoverClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remover foto",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            } else {
                // Nenhuma foto - mostrar botão para selecionar
                OutlinedButton(
                    onClick = onSelecionarClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = shape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isObrigatorio)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isObrigatorio)
                            "Adicionar foto (obrigatório)"
                        else
                            "Adicionar foto"
                    )
                }
            }
        }
    }
}
