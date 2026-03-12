// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/util/foto/FotoStorageManager.kt
package br.com.tlmacedo.meuponto.util.foto

import android.content.Context
import android.net.Uri
import br.com.tlmacedo.meuponto.data.local.database.dao.FotoComprovanteDao
import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import br.com.tlmacedo.meuponto.domain.model.FotoComprovante
import br.com.tlmacedo.meuponto.util.ComprovanteImageStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador de armazenamento de fotos de comprovante.
 *
 * Camada de abstração que integra:
 * - Processamento de imagem (ImageProcessor)
 * - Armazenamento físico (ComprovanteImageStorage)
 * - Metadados no banco (FotoComprovanteDao)
 *
 * ## Responsabilidades:
 * - Processar e salvar novas fotos
 * - Carregar fotos existentes
 * - Gerenciar ciclo de vida dos arquivos
 * - Manter consistência entre arquivos e banco de dados
 *
 * @author Thiago
 * @since 10.0.0
 */
@Singleton
class FotoStorageManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageStorage: ComprovanteImageStorage,
    private val imageProcessor: ImageProcessor,
    private val hashCalculator: ImageHashCalculator,
    private val exifWriter: ExifDataWriter
) {

    private val timestampFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")

    /**
     * Salva uma nova foto de comprovante processando conforme configurações.
     *
     * @param sourceUri URI da imagem de origem (câmera ou galeria)
     * @param empregoId ID do emprego
     * @param pontoId ID do ponto vinculado
     * @param data Data do ponto
     * @param config Configuração do emprego
     * @param gpsData Dados de localização (opcional)
     * @return Resultado do salvamento
     */
    suspend fun savePhoto(
        sourceUri: Uri,
        empregoId: Long,
        pontoId: Long,
        data: LocalDate,
        config: ConfiguracaoEmprego,
        gpsData: GpsData? = null
    ): SavePhotoResult = withContext(Dispatchers.IO) {
        try {
            // 1. Criar arquivo de destino
            val outputFile = createOutputFile(empregoId, pontoId, data, config)

            // 2. Preparar metadados EXIF
            val exifMetadata = if (config.fotoIncluirLocalizacaoExif && gpsData != null) {
                FotoExifMetadata(
                    dateTime = LocalDateTime.now(),
                    latitude = gpsData.latitude,
                    longitude = gpsData.longitude,
                    altitude = gpsData.altitude,
                    userComment = "ponto:$pontoId;emprego:$empregoId",
                    description = "Comprovante de ponto - ${data}"
                )
            } else {
                FotoExifMetadata(
                    dateTime = LocalDateTime.now(),
                    userComment = "ponto:$pontoId;emprego:$empregoId",
                    description = "Comprovante de ponto - ${data}"
                )
            }

            // 3. Processar imagem
            val result = imageProcessor.processImage(
                sourceUri = sourceUri,
                outputFile = outputFile,
                config = config,
                exifMetadata = exifMetadata
            )

            when (result) {
                is ImageProcessingResult.Success -> {
                    val relativePath = getRelativePath(empregoId, data, outputFile.name)
                    SavePhotoResult.Success(
                        relativePath = relativePath,
                        absolutePath = outputFile.absolutePath,
                        sizeBytes = result.sizeBytes,
                        hashMd5 = result.hashMd5,
                        width = result.finalWidth,
                        height = result.finalHeight,
                        wasResized = result.wasResized,
                        finalQuality = result.finalQuality
                    )
                }
                is ImageProcessingResult.Error -> {
                    outputFile.delete()
                    SavePhotoResult.Error(result.message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SavePhotoResult.Error("Erro ao salvar foto: ${e.message}")
        }
    }

    /**
     * Salva uma foto a partir de arquivo (ex: foto capturada pela câmera).
     *
     * @param sourceFile Arquivo de origem
     * @param empregoId ID do emprego
     * @param pontoId ID do ponto vinculado
     * @param data Data do ponto
     * @param config Configuração do emprego
     * @param gpsData Dados de localização (opcional)
     * @return Resultado do salvamento
     */
    suspend fun savePhoto(
        sourceFile: File,
        empregoId: Long,
        pontoId: Long,
        data: LocalDate,
        config: ConfiguracaoEmprego,
        gpsData: GpsData? = null
    ): SavePhotoResult = withContext(Dispatchers.IO) {
        try {
            // 1. Criar arquivo de destino
            val outputFile = createOutputFile(empregoId, pontoId, data, config)

            // 2. Preparar metadados EXIF
            val exifMetadata = if (config.fotoIncluirLocalizacaoExif && gpsData != null) {
                FotoExifMetadata(
                    dateTime = LocalDateTime.now(),
                    latitude = gpsData.latitude,
                    longitude = gpsData.longitude,
                    altitude = gpsData.altitude,
                    userComment = "ponto:$pontoId;emprego:$empregoId",
                    description = "Comprovante de ponto - ${data}"
                )
            } else {
                FotoExifMetadata(
                    dateTime = LocalDateTime.now(),
                    userComment = "ponto:$pontoId;emprego:$empregoId",
                    description = "Comprovante de ponto - ${data}"
                )
            }

            // 3. Processar imagem
            val result = imageProcessor.processImage(
                sourceFile = sourceFile,
                outputFile = outputFile,
                config = config,
                exifMetadata = exifMetadata
            )

            when (result) {
                is ImageProcessingResult.Success -> {
                    val relativePath = getRelativePath(empregoId, data, outputFile.name)

                    // 4. Limpar arquivo temporário de origem se diferente
                    if (sourceFile.absolutePath != outputFile.absolutePath) {
                        sourceFile.delete()
                    }

                    SavePhotoResult.Success(
                        relativePath = relativePath,
                        absolutePath = outputFile.absolutePath,
                        sizeBytes = result.sizeBytes,
                        hashMd5 = result.hashMd5,
                        width = result.finalWidth,
                        height = result.finalHeight,
                        wasResized = result.wasResized,
                        finalQuality = result.finalQuality
                    )
                }
                is ImageProcessingResult.Error -> {
                    outputFile.delete()
                    SavePhotoResult.Error(result.message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SavePhotoResult.Error("Erro ao salvar foto: ${e.message}")
        }
    }

    /**
     * Carrega uma foto como Bitmap.
     *
     * @param relativePath Caminho relativo da foto
     * @return Bitmap ou null se não encontrada
     */
    suspend fun loadPhoto(relativePath: String): android.graphics.Bitmap? = withContext(Dispatchers.IO) {
        imageStorage.loadBitmap(relativePath)
    }

    /**
     * Carrega thumbnail de uma foto.
     *
     * @param relativePath Caminho relativo da foto
     * @return Bitmap da thumbnail ou null
     */
    suspend fun loadThumbnail(relativePath: String): android.graphics.Bitmap? = withContext(Dispatchers.IO) {
        imageStorage.loadThumbnail(relativePath)
    }

    /**
     * Obtém o arquivo absoluto de uma foto.
     *
     * @param relativePath Caminho relativo
     * @return File
     */
    fun getAbsoluteFile(relativePath: String): File {
        return imageStorage.getAbsoluteFile(relativePath)
    }

    /**
     * Verifica se uma foto existe.
     *
     * @param relativePath Caminho relativo
     * @return true se existe
     */
    fun exists(relativePath: String): Boolean {
        return imageStorage.exists(relativePath)
    }

    /**
     * Verifica integridade de uma foto.
     *
     * @param relativePath Caminho relativo
     * @param expectedHash Hash MD5 esperado
     * @return Resultado da verificação
     */
    suspend fun verifyIntegrity(relativePath: String, expectedHash: String): IntegrityCheckResult =
        withContext(Dispatchers.IO) {
            val file = imageStorage.getAbsoluteFile(relativePath)
            imageProcessor.verifyIntegrity(file, expectedHash)
        }

    /**
     * Deleta uma foto.
     *
     * @param relativePath Caminho relativo
     * @return true se deletou com sucesso
     */
    suspend fun deletePhoto(relativePath: String): Boolean = withContext(Dispatchers.IO) {
        imageStorage.delete(relativePath)
    }

    /**
     * Deleta todas as fotos de um emprego.
     *
     * @param empregoId ID do emprego
     * @return Número de arquivos deletados
     */
    suspend fun deleteAllForEmprego(empregoId: Long): Int = withContext(Dispatchers.IO) {
        imageStorage.deleteAllForEmprego(empregoId)
    }

    /**
     * Obtém estatísticas de armazenamento.
     */
    fun getStorageStats(): StorageStats {
        return StorageStats(
            totalImages = imageStorage.getTotalImageCount(),
            totalSizeBytes = imageStorage.getTotalStorageSize(),
            totalSizeFormatted = imageStorage.getTotalStorageSizeFormatted()
        )
    }

    /**
     * Limpa arquivos órfãos (sem registro no banco).
     *
     * @param validPaths Conjunto de caminhos válidos (do banco)
     * @return Número de arquivos removidos
     */
    suspend fun cleanupOrphanFiles(validPaths: Set<String>): Int = withContext(Dispatchers.IO) {
        imageStorage.cleanupOrphanImages(validPaths)
    }

    // ════════════════════════════════════════════════════════════════════════
    // MÉTODOS PRIVADOS
    // ════════════════════════════════════════════════════════════════════════

    private fun createOutputFile(
        empregoId: Long,
        pontoId: Long,
        data: LocalDate,
        config: ConfiguracaoEmprego
    ): File {
        val directory = getOrCreateDirectory(empregoId, data)
        val timestamp = LocalDateTime.now().format(timestampFormatter)
        val extension = config.fotoFormato.extensao
        val fileName = "ponto_${pontoId}_$timestamp.$extension"
        return File(directory, fileName)
    }

    private fun getOrCreateDirectory(empregoId: Long, data: LocalDate): File {
        val path = "emprego_$empregoId/${data.year}/${String.format("%02d", data.monthValue)}"
        return File(imageStorage.getComprovantesDirectory(), path).apply {
            if (!exists()) mkdirs()
        }
    }

    private fun getRelativePath(empregoId: Long, data: LocalDate, fileName: String): String {
        return "emprego_$empregoId/${data.year}/${String.format("%02d", data.monthValue)}/$fileName"
    }
}

/**
 * Dados de localização GPS.
 */
data class GpsData(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val accuracy: Float? = null
)

/**
 * Resultado do salvamento de foto.
 */
sealed class SavePhotoResult {
    data class Success(
        val relativePath: String,
        val absolutePath: String,
        val sizeBytes: Long,
        val hashMd5: String,
        val width: Int,
        val height: Int,
        val wasResized: Boolean,
        val finalQuality: Int
    ) : SavePhotoResult() {
        val sizeFormatted: String
            get() = when {
                sizeBytes < 1024 -> "$sizeBytes B"
                sizeBytes < 1024 * 1024 -> String.format("%.1f KB", sizeBytes / 1024.0)
                else -> String.format("%.2f MB", sizeBytes / (1024.0 * 1024.0))
            }
    }

    data class Error(val message: String) : SavePhotoResult()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
}

/**
 * Estatísticas de armazenamento.
 */
data class StorageStats(
    val totalImages: Int,
    val totalSizeBytes: Long,
    val totalSizeFormatted: String
) {
    val isEmpty: Boolean get() = totalImages == 0
}
