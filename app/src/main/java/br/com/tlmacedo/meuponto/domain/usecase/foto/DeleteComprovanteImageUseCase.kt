// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/foto/DeleteComprovanteImageUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.foto

import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.repository.PontoRepository
import br.com.tlmacedo.meuponto.util.foto.FotoStorageManager
import javax.inject.Inject

/**
 * Use case para excluir imagem de comprovante.
 *
 * @author Thiago
 * @since 10.0.0
 */
class DeleteComprovanteImageUseCase @Inject constructor(
    private val storageManager: FotoStorageManager,
    private val pontoRepository: PontoRepository
) {
    /**
     * Exclui a foto de comprovante de um ponto.
     */
    suspend operator fun invoke(ponto: Ponto, updatePonto: Boolean = true): Boolean {
        val filePath = ponto.fotoComprovantePath ?: return true

        val deleted = storageManager.deletePhoto(filePath)

        if (deleted && updatePonto) {
            val pontoAtualizado = ponto.comFotoComprovante(null)
            pontoRepository.atualizar(pontoAtualizado)
        }

        return deleted
    }

    /**
     * Exclui apenas o arquivo físico.
     */
    suspend fun deleteFileOnly(filePath: String): Boolean {
        return storageManager.deletePhoto(filePath)
    }
}
