// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/foto/CreateTempImageFileUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.foto

import android.net.Uri
import br.com.tlmacedo.meuponto.util.foto.PhotoCaptureManager
import javax.inject.Inject

/**
 * Use case para criar arquivo temporário para captura de imagem.
 *
 * @author Thiago
 * @since 10.0.0
 */
class CreateTempImageFileUseCase @Inject constructor(
    private val photoCaptureManager: PhotoCaptureManager
) {
    /**
     * Prepara para captura via câmera e retorna URI.
     */
    operator fun invoke(): Uri? {
        return photoCaptureManager.prepareForCameraCapture()
    }

    /**
     * Limpa arquivos temporários antigos.
     */
    fun cleanupOldTempFiles(maxAgeMinutes: Int = 60): Int {
        return photoCaptureManager.cleanupOldTempFiles(maxAgeMinutes)
    }
}
