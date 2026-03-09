// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/foto/CreateTempImageFileUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.foto

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Use case para criar arquivo temporário para captura de foto com a câmera.
 *
 * Cria um arquivo temporário e retorna o URI via FileProvider para uso
 * com Intent da câmera (ACTION_IMAGE_CAPTURE).
 *
 * @author Thiago
 * @since 9.0.0
 */
class CreateTempImageFileUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TEMP_DIR = "temp_images"
        private const val FILE_PROVIDER_AUTHORITY_SUFFIX = ".fileprovider"
    }

    private val timestampFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")

    /**
     * Cria um arquivo temporário e retorna o URI para uso com a câmera.
     *
     * @return Result com o par (URI, caminho do arquivo) ou erro
     */
    operator fun invoke(): Result<TempImageFile> {
        return try {
            val tempDir = File(context.cacheDir, TEMP_DIR).apply {
                if (!exists()) mkdirs()
            }

            val timestamp = LocalDateTime.now().format(timestampFormatter)
            val fileName = "TEMP_$timestamp.jpg"
            val file = File(tempDir, fileName)

            val authority = "${context.packageName}$FILE_PROVIDER_AUTHORITY_SUFFIX"
            val uri = FileProvider.getUriForFile(context, authority, file)

            Result.success(TempImageFile(uri = uri, file = file))
        } catch (e: Exception) {
            Result.failure(TempFileException("Erro ao criar arquivo temporário: ${e.message}", e))
        }
    }

    /**
     * Limpa arquivos temporários antigos (mais de 1 hora).
     *
     * @return Número de arquivos removidos
     */
    fun cleanupOldTempFiles(): Int {
        return try {
            val tempDir = File(context.cacheDir, TEMP_DIR)
            if (!tempDir.exists()) return 0

            val oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000)
            var count = 0

            tempDir.listFiles()?.forEach { file ->
                if (file.lastModified() < oneHourAgo) {
                    if (file.delete()) count++
                }
            }

            count
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Deleta um arquivo temporário específico.
     *
     * @param file Arquivo a ser deletado
     */
    fun deleteTempFile(file: File) {
        try {
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            // Ignora erro na limpeza
        }
    }
}

/**
 * Dados do arquivo temporário criado.
 */
data class TempImageFile(
    val uri: Uri,
    val file: File
)

/**
 * Exceção específica para erros ao criar arquivo temporário.
 */
class TempFileException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
