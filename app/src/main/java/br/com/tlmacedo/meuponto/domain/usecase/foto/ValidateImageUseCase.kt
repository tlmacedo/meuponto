// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/foto/ValidateImageUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.foto

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Use case para validar imagens antes de salvar.
 *
 * Verifica:
 * - Formato suportado
 * - Tamanho mínimo/máximo
 * - Dimensões mínimas
 * - Arquivo corrompido
 *
 * @author Thiago
 * @since 10.0.0
 */
class ValidateImageUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val SUPPORTED_MIME_TYPES = setOf(
            "image/jpeg",
            "image/png",
            "image/jpg"
        )
        const val MIN_DIMENSION = 100 // pixels
        const val MAX_FILE_SIZE = 50 * 1024 * 1024L // 50 MB
        const val MIN_FILE_SIZE = 1024L // 1 KB
    }

    /**
     * Valida uma imagem por URI.
     *
     * @param uri URI da imagem
     * @return Resultado da validação
     */
    operator fun invoke(uri: Uri): ImageValidationResult {
        return try {
            // Verificar tipo MIME
            val mimeType = context.contentResolver.getType(uri)
            if (mimeType == null || mimeType !in SUPPORTED_MIME_TYPES) {
                return ImageValidationResult.InvalidFormat(
                    message = "Formato não suportado: $mimeType",
                    supportedFormats = SUPPORTED_MIME_TYPES.toList()
                )
            }

            // Verificar se pode abrir o arquivo
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return ImageValidationResult.FileNotReadable("Não foi possível abrir o arquivo")

            // Verificar tamanho
            val fileSize = inputStream.available().toLong()
            inputStream.close()

            if (fileSize < MIN_FILE_SIZE) {
                return ImageValidationResult.FileTooSmall(
                    actualSize = fileSize,
                    minSize = MIN_FILE_SIZE
                )
            }

            if (fileSize > MAX_FILE_SIZE) {
                return ImageValidationResult.FileTooLarge(
                    actualSize = fileSize,
                    maxSize = MAX_FILE_SIZE
                )
            }

            // Verificar dimensões
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }

            val width = options.outWidth
            val height = options.outHeight

            if (width <= 0 || height <= 0) {
                return ImageValidationResult.CorruptedImage("Não foi possível decodificar a imagem")
            }

            if (width < MIN_DIMENSION || height < MIN_DIMENSION) {
                return ImageValidationResult.DimensionsTooSmall(
                    actualWidth = width,
                    actualHeight = height,
                    minDimension = MIN_DIMENSION
                )
            }

            ImageValidationResult.Valid(
                width = width,
                height = height,
                sizeBytes = fileSize,
                mimeType = mimeType
            )
        } catch (e: Exception) {
            ImageValidationResult.Error(e.message ?: "Erro desconhecido")
        }
    }
}

/**
 * Resultado da validação de imagem.
 */
sealed class ImageValidationResult {
    /** Imagem válida */
    data class Valid(
        val width: Int,
        val height: Int,
        val sizeBytes: Long,
        val mimeType: String
    ) : ImageValidationResult()

    /** Formato inválido */
    data class InvalidFormat(
        val message: String,
        val supportedFormats: List<String>
    ) : ImageValidationResult()

    /** Arquivo não legível */
    data class FileNotReadable(val message: String) : ImageValidationResult()

    /** Arquivo muito pequeno */
    data class FileTooSmall(
        val actualSize: Long,
        val minSize: Long
    ) : ImageValidationResult()

    /** Arquivo muito grande */
    data class FileTooLarge(
        val actualSize: Long,
        val maxSize: Long
    ) : ImageValidationResult()

    /** Dimensões muito pequenas */
    data class DimensionsTooSmall(
        val actualWidth: Int,
        val actualHeight: Int,
        val minDimension: Int
    ) : ImageValidationResult()

    /** Imagem corrompida */
    data class CorruptedImage(val message: String) : ImageValidationResult()

    /** Erro genérico */
    data class Error(val message: String) : ImageValidationResult()

    /** Verifica se é válida */
    val isValid: Boolean get() = this is Valid

    /** Obtém mensagem de erro */
    fun getErrorMessage(): String? = when (this) {
        is Valid -> null
        is InvalidFormat -> message
        is FileNotReadable -> message
        is FileTooSmall -> "Arquivo muito pequeno (mínimo: ${minSize / 1024} KB)"
        is FileTooLarge -> "Arquivo muito grande (máximo: ${maxSize / 1024 / 1024} MB)"
        is DimensionsTooSmall -> "Imagem muito pequena (mínimo: ${minDimension}x${minDimension}px)"
        is CorruptedImage -> message
        is Error -> message
    }
}
