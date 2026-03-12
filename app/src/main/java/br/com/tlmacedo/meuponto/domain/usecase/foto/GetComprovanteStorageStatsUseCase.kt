// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/foto/GetComprovanteStorageStatsUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.foto

import br.com.tlmacedo.meuponto.util.foto.FotoStorageManager
import br.com.tlmacedo.meuponto.util.foto.StorageStats
import javax.inject.Inject

/**
 * Use case para obter estatísticas de armazenamento de fotos.
 *
 * @author Thiago
 * @since 10.0.0
 */
class GetComprovanteStorageStatsUseCase @Inject constructor(
    private val storageManager: FotoStorageManager
) {
    /**
     * Obtém estatísticas de armazenamento.
     */
    operator fun invoke(): StorageStats {
        return storageManager.getStorageStats()
    }
}
