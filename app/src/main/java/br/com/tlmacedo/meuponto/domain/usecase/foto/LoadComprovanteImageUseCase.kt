// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/foto/LoadComprovanteImageUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.foto

import android.graphics.Bitmap
import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.util.foto.FotoStorageManager
import br.com.tlmacedo.meuponto.util.foto.IntegrityCheckResult
import java.io.File
import javax.inject.Inject

/**
 * Use case para carregar imagem de comprovante.
 *
 * @author Thiago
 * @since 10.0.0
 */
class LoadComprovanteImageUseCase @Inject constructor(
    private val storageManager: FotoStorageManager
) {
    /**
     * Carrega o Bitmap do comprovante.
     */
    suspend operator fun invoke(ponto: Ponto): Bitmap? {
        val filePath = ponto.fotoComprovantePath ?: return null
        return storageManager.loadPhoto(filePath)
    }

    /**
     * Carrega thumbnail do comprovante.
     */
    suspend fun loadThumbnail(ponto: Ponto): Bitmap? {
        val filePath = ponto.fotoComprovantePath ?: return null
        return storageManager.loadThumbnail(filePath)
    }

    /**
     * Obtém o arquivo do comprovante.
     */
    fun getFile(ponto: Ponto): File? {
        val filePath = ponto.fotoComprovantePath ?: return null
        val file = storageManager.getAbsoluteFile(filePath)
        return if (file.exists()) file else null
    }

    /**
     * Verifica se o comprovante existe.
     */
    fun exists(ponto: Ponto): Boolean {
        val filePath = ponto.fotoComprovantePath ?: return false
        return storageManager.exists(filePath)
    }

    /**
     * Verifica integridade do arquivo.
     */
    suspend fun verifyIntegrity(ponto: Ponto, expectedHash: String): IntegrityCheckResult {
        val filePath = ponto.fotoComprovantePath
            ?: return IntegrityCheckResult(
                isValid = false,
                expectedHash = expectedHash,
                actualHash = null,
                errorMessage = "Arquivo não encontrado"
            )
        return storageManager.verifyIntegrity(filePath, expectedHash)
    }
}
