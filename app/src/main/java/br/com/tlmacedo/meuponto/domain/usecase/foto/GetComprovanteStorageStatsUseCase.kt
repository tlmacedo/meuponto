// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/foto/GetComprovanteStorageStatsUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.foto

import br.com.tlmacedo.meuponto.util.ComprovanteImageStorage
import javax.inject.Inject

/**
 * Use case para obter estatísticas de armazenamento de comprovantes.
 *
 * Útil para exibir informações sobre uso de espaço nas configurações.
 *
 * @author Thiago
 * @since 9.0.0
 */
class GetComprovanteStorageStatsUseCase @Inject constructor(
    private val imageStorage: ComprovanteImageStorage
) {
    /**
     * Obtém estatísticas de armazenamento.
     *
     * @return Estatísticas de uso de armazenamento
     */
    operator fun invoke(): ComprovanteStorageStats {
        return ComprovanteStorageStats(
            totalImages = imageStorage.getTotalImageCount(),
            totalSizeBytes = imageStorage.getTotalStorageSize(),
            totalSizeFormatted = imageStorage.getTotalStorageSizeFormatted()
        )
    }
}

/**
 * Estatísticas de armazenamento de comprovantes.
 */
data class ComprovanteStorageStats(
    val totalImages: Int,
    val totalSizeBytes: Long,
    val totalSizeFormatted: String
) {
    val isEmpty: Boolean get() = totalImages == 0
}
