// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/foto/DeleteComprovanteImageUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.foto

import br.com.tlmacedo.meuponto.util.ComprovanteImageStorage
import javax.inject.Inject

/**
 * Use case para deletar imagens de comprovante de ponto.
 *
 * @author Thiago
 * @since 9.0.0
 */
class DeleteComprovanteImageUseCase @Inject constructor(
    private val imageStorage: ComprovanteImageStorage
) {
    /**
     * Deleta uma imagem específica.
     *
     * @param relativePath Caminho relativo da imagem
     * @return Result indicando sucesso ou erro
     */
    suspend operator fun invoke(relativePath: String): Result<Unit> {
        return try {
            if (relativePath.isBlank()) {
                return Result.success(Unit) // Nada a deletar
            }

            val deleted = imageStorage.delete(relativePath)
            if (deleted) {
                Result.success(Unit)
            } else {
                Result.failure(ImageDeleteException("Falha ao deletar imagem: $relativePath"))
            }
        } catch (e: Exception) {
            Result.failure(ImageDeleteException("Erro ao deletar imagem: ${e.message}", e))
        }
    }

    /**
     * Deleta todas as imagens de um emprego.
     * Útil quando o emprego é excluído.
     *
     * @param empregoId ID do emprego
     * @return Result com o número de arquivos deletados
     */
    suspend fun deleteAllForEmprego(empregoId: Long): Result<Int> {
        return try {
            val count = imageStorage.deleteAllForEmprego(empregoId)
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(ImageDeleteException("Erro ao deletar imagens do emprego: ${e.message}", e))
        }
    }
}

/**
 * Exceção específica para erros ao deletar imagem.
 */
class ImageDeleteException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
