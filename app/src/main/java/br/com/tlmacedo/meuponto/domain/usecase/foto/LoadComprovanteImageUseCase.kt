// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/foto/LoadComprovanteImageUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.foto

import android.graphics.Bitmap
import br.com.tlmacedo.meuponto.util.ComprovanteImageStorage
import javax.inject.Inject

/**
 * Use case para carregar imagens de comprovante de ponto.
 *
 * Suporta carregamento de imagem completa ou thumbnail otimizada.
 *
 * @author Thiago
 * @since 9.0.0
 */
class LoadComprovanteImageUseCase @Inject constructor(
    private val imageStorage: ComprovanteImageStorage
) {
    /**
     * Carrega a imagem completa.
     *
     * @param relativePath Caminho relativo da imagem
     * @return Result com o Bitmap ou erro
     */
    suspend operator fun invoke(relativePath: String): Result<Bitmap> {
        return try {
            if (relativePath.isBlank()) {
                return Result.failure(ImageLoadException("Caminho da imagem não informado"))
            }

            val bitmap = imageStorage.loadBitmap(relativePath)
            if (bitmap != null) {
                Result.success(bitmap)
            } else {
                Result.failure(ImageLoadException("Imagem não encontrada: $relativePath"))
            }
        } catch (e: Exception) {
            Result.failure(ImageLoadException("Erro ao carregar imagem: ${e.message}", e))
        }
    }

    /**
     * Carrega uma thumbnail otimizada para exibição em listas.
     *
     * @param relativePath Caminho relativo da imagem
     * @return Result com o Bitmap da thumbnail ou erro
     */
    suspend fun thumbnail(relativePath: String): Result<Bitmap> {
        return try {
            if (relativePath.isBlank()) {
                return Result.failure(ImageLoadException("Caminho da imagem não informado"))
            }

            val bitmap = imageStorage.loadThumbnail(relativePath)
            if (bitmap != null) {
                Result.success(bitmap)
            } else {
                Result.failure(ImageLoadException("Thumbnail não encontrada: $relativePath"))
            }
        } catch (e: Exception) {
            Result.failure(ImageLoadException("Erro ao carregar thumbnail: ${e.message}", e))
        }
    }

    /**
     * Verifica se a imagem existe.
     *
     * @param relativePath Caminho relativo da imagem
     * @return true se existe
     */
    fun exists(relativePath: String): Boolean {
        return relativePath.isNotBlank() && imageStorage.exists(relativePath)
    }
}

/**
 * Exceção específica para erros ao carregar imagem.
 */
class ImageLoadException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
