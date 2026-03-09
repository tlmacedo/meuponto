// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/foto/ComprovanteImagePicker.kt
package br.com.tlmacedo.meuponto.presentation.components.foto

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File

/**
 * Componente para seleção e exibição de foto de comprovante.
 *
 * Permite capturar foto com a câmera ou selecionar da galeria.
 * Exibe preview da imagem selecionada com opção de remoção.
 *
 * @param currentImagePath Caminho relativo da imagem atual (se houver)
 * @param currentImageUri URI temporário da imagem selecionada (antes de salvar)
 * @param imageBaseDir Diretório base onde as imagens são armazenadas
 * @param onImageSelected Callback quando uma imagem é selecionada (retorna URI)
 * @param onImageRemoved Callback quando a imagem é removida
 * @param onCameraUriCreated Callback para criar URI temporário para câmera
 * @param modifier Modificador do componente
 * @param enabled Se o componente está habilitado para interação
 *
 * @author Thiago
 * @since 9.0.0
 */
@Composable
fun ComprovanteImagePicker(
    currentImagePath: String?,
    currentImageUri: Uri?,
    imageBaseDir: File?,
    onImageSelected: (Uri) -> Unit,
    onImageRemoved: () -> Unit,
    onCameraUriCreated: () -> Uri?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    var showSourceDialog by remember { mutableStateOf(false) }
    var showImagePreview by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para captura de foto (declarado primeiro)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCameraUri?.let { uri ->
                onImageSelected(uri)
            }
        }
        pendingCameraUri = null
    }

    // Função auxiliar para lançar a câmera
    val launchCamera: () -> Unit = {
        pendingCameraUri = onCameraUriCreated()
        pendingCameraUri?.let { uri ->
            cameraLauncher.launch(uri)
        }
    }

    // Launcher para permissão de câmera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(
                context,
                "Permissão de câmera necessária para tirar fotos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Launcher para seleção da galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onImageSelected(it) }
    }

    // Determina a imagem a exibir
    val displayImage: Any? = when {
        currentImageUri != null -> currentImageUri
        currentImagePath != null && imageBaseDir != null -> {
            File(imageBaseDir, currentImagePath)
        }
        else -> null
    }

    val hasImage = displayImage != null

    Column(modifier = modifier) {
        Text(
            text = "Comprovante",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (hasImage) {
            // Exibe preview da imagem
            ImagePreviewBox(
                image = displayImage!!,
                onRemove = onImageRemoved,
                onClick = { showImagePreview = true },
                enabled = enabled
            )
        } else {
            // Exibe botão para adicionar
            AddImageBox(
                onClick = { showSourceDialog = true },
                enabled = enabled
            )
        }
    }

    // Dialog para escolher fonte (câmera ou galeria)
    if (showSourceDialog) {
        ImageSourceDialog(
            onCameraClick = {
                showSourceDialog = false
                if (hasCameraPermission(context)) {
                    launchCamera()
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            onGalleryClick = {
                showSourceDialog = false
                galleryLauncher.launch("image/*")
            },
            onDismiss = { showSourceDialog = false }
        )
    }

    // Dialog para visualização em tela cheia
    if (showImagePreview && displayImage != null) {
        ImagePreviewDialog(
            image = displayImage,
            onDismiss = { showImagePreview = false }
        )
    }
}

/**
 * Box para adicionar nova imagem.
 */
@Composable
private fun AddImageBox(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = if (enabled) {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Adicionar comprovante",
                style = MaterialTheme.typography.bodyMedium,
                color = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        }
    }
}

/**
 * Box com preview da imagem selecionada.
 */
@Composable
private fun ImagePreviewBox(
    image: Any,
    onRemove: () -> Unit,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .crossfade(true)
                .build(),
            contentDescription = "Comprovante de ponto",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay escuro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.1f)
                )
        )

        // Botão de remover
        if (enabled) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(32.dp)
                    .background(
                        MaterialTheme.colorScheme.errorContainer,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remover imagem",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Indicador de toque para ampliar
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Toque para ampliar",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Dialog para escolher fonte da imagem.
 */
@Composable
private fun ImageSourceDialog(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Adicionar comprovante")
        },
        text = {
            Column {
                Text(
                    text = "Escolha como deseja adicionar a imagem:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ImageSourceOption(
                        icon = Icons.Default.CameraAlt,
                        label = "Câmera",
                        onClick = onCameraClick
                    )
                    ImageSourceOption(
                        icon = Icons.Default.Photo,
                        label = "Galeria",
                        onClick = onGalleryClick
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Opção de fonte de imagem (câmera/galeria).
 */
@Composable
private fun ImageSourceOption(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Dialog para visualização da imagem em tela cheia.
 */
@Composable
private fun ImagePreviewDialog(
    image: Any,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        title = null,
        text = {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .crossfade(true)
                    .build(),
                contentDescription = "Comprovante de ponto",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillWidth
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}

/**
 * Verifica se a permissão de câmera foi concedida.
 */
private fun hasCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
