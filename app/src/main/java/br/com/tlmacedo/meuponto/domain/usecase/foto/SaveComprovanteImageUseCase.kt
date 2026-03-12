// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/foto/SaveComprovanteImageUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.foto

import android.net.Uri
import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.repository.ConfiguracaoEmpregoRepository
import br.com.tlmacedo.meuponto.util.foto.FotoStorageManager
import br.com.tlmacedo.meuponto.util.foto.GpsData
import br.com.tlmacedo.meuponto.util.foto.SavePhotoResult
import java.io.File
import javax.inject.Inject

/**
 * Use case para salvar imagem de comprovante de ponto.
 *
 * @author Thiago
 * @since 10.0.0
 */
class SaveComprovanteImageUseCase @Inject constructor(
    private val storageManager: FotoStorageManager,
    private val configuracaoRepository: ConfiguracaoEmpregoRepository
) {
    /**
     * Salva uma imagem de comprovante a partir de URI.
     */
    suspend operator fun invoke(
        sourceUri: Uri,
        ponto: Ponto,
        latitude: Double? = null,
        longitude: Double? = null
    ): SavePhotoResult {
        val config = configuracaoRepository.buscarPorEmpregoId(ponto.empregoId)
            ?: ConfiguracaoEmprego(empregoId = ponto.empregoId)

        val gpsData = if (latitude != null && longitude != null) {
            GpsData(latitude = latitude, longitude = longitude)
        } else null

        return storageManager.savePhoto(
            sourceUri = sourceUri,
            empregoId = ponto.empregoId,
            pontoId = ponto.id,
            data = ponto.data,
            config = config,
            gpsData = gpsData
        )
    }

    /**
     * Salva uma imagem de comprovante a partir de arquivo.
     */
    suspend fun fromFile(
        sourceFile: File,
        ponto: Ponto,
        latitude: Double? = null,
        longitude: Double? = null
    ): SavePhotoResult {
        val config = configuracaoRepository.buscarPorEmpregoId(ponto.empregoId)
            ?: ConfiguracaoEmprego(empregoId = ponto.empregoId)

        val gpsData = if (latitude != null && longitude != null) {
            GpsData(latitude = latitude, longitude = longitude)
        } else null

        return storageManager.savePhoto(
            sourceFile = sourceFile,
            empregoId = ponto.empregoId,
            pontoId = ponto.id,
            data = ponto.data,
            config = config,
            gpsData = gpsData
        )
    }
}
