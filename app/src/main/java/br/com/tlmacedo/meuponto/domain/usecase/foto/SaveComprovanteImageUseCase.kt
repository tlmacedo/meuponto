// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/foto/SaveComprovanteImageUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.foto

import android.graphics.Bitmap
import android.net.Uri
import br.com.tlmacedo.meuponto.util.ComprovanteImageStorage
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case para salvar imagens de comprovante de ponto.
 *
 * Suporta salvamento a partir de URI (galeria/câmera) ou Bitmap direto.
 * A imagem é redimensionada automaticamente se exceder o tamanho máximo.
 *
 * @author Thiago
 * @since 9.0.0
 */
class SaveComprovanteImageUseCase @Inject constructor(
    private val imageStorage: ComprovanteImageStorage
) {
    /**
     * Salva imagem a partir de um URI.
     *
     * @param uri URI da imagem (content:// ou file://)
     * @param empregoId ID do emprego
     * @param pontoId ID do ponto (use 0 se ainda não persistido)
     * @param data Data do registro de ponto
     * @return Result com o caminho relativo salvo ou erro
     */
    suspend operator fun invoke(
        uri: Uri,
        empregoId: Long,
        pontoId: Long,
        data: LocalDate
    ): Result<String> {
        return try {
            val relativePath = imageStorage.saveFromUri(uri, empregoId, pontoId, data)
            if (relativePath != null) {
                Result.success(relativePath)
            } else {
                Result.failure(ImageSaveException("Falha ao salvar imagem do URI"))
            }
        } catch (e: Exception) {
            Result.failure(ImageSaveException("Erro ao processar imagem: ${e.message}", e))
        }
    }

    /**
     * Salva imagem a partir de um Bitmap.
     *
     * @param bitmap Bitmap da imagem
     * @param empregoId ID do emprego
     * @param pontoId ID do ponto
     * @param data Data do registro de ponto
     * @return Result com o caminho relativo salvo ou erro
     */
    suspend fun fromBitmap(
        bitmap: Bitmap,
        empregoId: Long,
        pontoId: Long,
        data: LocalDate
    ): Result<String> {
        return try {
            val relativePath = imageStorage.saveFromBitmap(bitmap, empregoId, pontoId, data)
            if (relativePath != null) {
                Result.success(relativePath)
            } else {
                Result.failure(ImageSaveException("Falha ao salvar bitmap"))
            }
        } catch (e: Exception) {
            Result.failure(ImageSaveException("Erro ao processar bitmap: ${e.message}", e))
        }
    }
}

/**
 * Exceção específica para erros ao salvar imagem.
 */
class ImageSaveException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
