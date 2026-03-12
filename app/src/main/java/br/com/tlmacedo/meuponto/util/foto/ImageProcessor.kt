// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/util/foto/ImageProcessor.kt
package br.com.tlmacedo.meuponto.util.foto

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import br.com.tlmacedo.meuponto.domain.model.FotoFormato
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Processador central de imagens de comprovante.
 *
 * Orquestra todas as operações de processamento de imagem em uma única
 * classe de fácil uso, aplicando as configurações do emprego.
 *
 * ## Pipeline de Processamento:
 * 1. Carregamento com sampling otimizado
 * 2. Correção de orientação EXIF
 * 3. Redimensionamento (se necessário)
 * 4. Compressão adaptativa
 * 5. Gravação de metadados EXIF
 * 6. Cálculo de hash MD5
 * 7. Salvamento no destino final
 *
 * @author Thiago
 * @since 10.0.0
 */
@Singleton
class ImageProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resizer: ImageResizer,
    private val compressor: ImageCompressor,
    private val orientationCorrector: ImageOrientationCorrector,
    private val hashCalculator: ImageHashCalculator,
    private val exifWriter: ExifDataWriter
) {

    /**
     * Processa uma imagem de URI aplicando todas as configurações.
     *
     * @param sourceUri URI da imagem de origem
     * @param outputFile Arquivo de destino
     * @param config Configuração do emprego
     * @param exifMetadata Metadados EXIF a serem gravados (opcional)
     * @return Resultado do processamento
     */
    fun processImage(
        sourceUri: Uri,
        outputFile: File,
        config: ConfiguracaoEmprego,
        exifMetadata: FotoExifMetadata? = null
    ): ImageProcessingResult {
        return try {
            // 1. Carregar com correção de orientação
            val bitmap = if (config.fotoCorrecaoOrientacao) {
                orientationCorrector.loadBitmapWithCorrectOrientation(sourceUri)
            } else {
                context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            }

            if (bitmap == null) {
                return ImageProcessingResult.Error("Falha ao carregar imagem")
            }

            processAndSave(bitmap, outputFile, config, exifMetadata)
        } catch (e: Exception) {
            e.printStackTrace()
            ImageProcessingResult.Error("Erro no processamento: ${e.message}")
        }
    }

    /**
     * Processa uma imagem de arquivo aplicando todas as configurações.
     *
     * @param sourceFile Arquivo de origem
     * @param outputFile Arquivo de destino
     * @param config Configuração do emprego
     * @param exifMetadata Metadados EXIF a serem gravados (opcional)
     * @return Resultado do processamento
     */
    fun processImage(
        sourceFile: File,
        outputFile: File,
        config: ConfiguracaoEmprego,
        exifMetadata: FotoExifMetadata? = null
    ): ImageProcessingResult {
        return try {
            // 1. Carregar com correção de orientação
            val bitmap = if (config.fotoCorrecaoOrientacao) {
                orientationCorrector.loadBitmapWithCorrectOrientation(sourceFile)
            } else {
                BitmapFactory.decodeFile(sourceFile.absolutePath)
            }

            if (bitmap == null) {
                return ImageProcessingResult.Error("Falha ao carregar imagem")
            }

            processAndSave(bitmap, outputFile, config, exifMetadata)
        } catch (e: Exception) {
            e.printStackTrace()
            ImageProcessingResult.Error("Erro no processamento: ${e.message}")
        }
    }

    /**
     * Processa um Bitmap e salva no arquivo de destino.
     */
    private fun processAndSave(
        originalBitmap: Bitmap,
        outputFile: File,
        config: ConfiguracaoEmprego,
        exifMetadata: FotoExifMetadata?
    ): ImageProcessingResult {
        var bitmap = originalBitmap
        var wasResized = false

        try {
            // 2. Redimensionar se necessário
            val maxDimension = config.fotoResolucaoMaxima
            if (maxDimension > 0 && resizer.needsResize(bitmap.width, bitmap.height, maxDimension)) {
                val resizedBitmap = resizer.resizeToFit(bitmap, maxDimension)
                if (resizedBitmap !== bitmap) {
                    bitmap.recycle()
                    bitmap = resizedBitmap
                    wasResized = true
                }
            }

            // 3. Determinar formato e qualidade
            val format = when (config.fotoFormato) {
                FotoFormato.PNG -> Bitmap.CompressFormat.PNG
                else -> Bitmap.CompressFormat.JPEG
            }

            // 4. Comprimir e salvar
            val maxSizeBytes = if (config.fotoTamanhoMaximoKb > 0) {
                config.fotoTamanhoMaximoKb * 1024L
            } else {
                Long.MAX_VALUE
            }

            val compressionResult = if (format == Bitmap.CompressFormat.JPEG && maxSizeBytes < Long.MAX_VALUE) {
                // Compressão adaptativa para JPEG com limite de tamanho
                compressor.saveToFileAdaptive(
                    bitmap = bitmap,
                    outputFile = outputFile,
                    maxSizeBytes = maxSizeBytes,
                    initialQuality = config.fotoQualidade
                )
            } else {
                // Compressão fixa
                val success = compressor.saveToFile(bitmap, outputFile, format, config.fotoQualidade)
                if (success) {
                    AdaptiveCompressionResult(
                        data = ByteArray(0),
                        finalQuality = config.fotoQualidade,
                        sizeBytes = outputFile.length(),
                        targetAchieved = true
                    )
                } else {
                    null
                }
            }

            if (compressionResult == null) {
                return ImageProcessingResult.Error("Falha na compressão")
            }

            // 5. Gravar metadados EXIF (apenas para JPEG)
            if (format == Bitmap.CompressFormat.JPEG && exifMetadata != null) {
                exifWriter.writeMetadata(outputFile, exifMetadata)
            }

            // 6. Calcular hash MD5
            val hash = hashCalculator.calculateMd5(outputFile)
                ?: return ImageProcessingResult.Error("Falha ao calcular hash")

            return ImageProcessingResult.Success(
                file = outputFile,
                originalWidth = originalBitmap.width,
                originalHeight = originalBitmap.height,
                finalWidth = bitmap.width,
                finalHeight = bitmap.height,
                wasResized = wasResized,
                finalQuality = compressionResult.finalQuality,
                sizeBytes = outputFile.length(),
                hashMd5 = hash,
                format = config.fotoFormato
            )
        } finally {
            if (bitmap !== originalBitmap) {
                bitmap.recycle()
            }
        }
    }

    /**
     * Processa rapidamente para preview (sem salvar).
     *
     * @param sourceUri URI da imagem
     * @param maxDimension Dimensão máxima
     * @param correctOrientation Corrigir orientação
     * @return Bitmap processado ou null
     */
    fun processForPreview(
        sourceUri: Uri,
        maxDimension: Int = 1024,
        correctOrientation: Boolean = true
    ): Bitmap? {
        return try {
            val bitmap = if (correctOrientation) {
                orientationCorrector.loadBitmapWithCorrectOrientation(sourceUri)
            } else {
                resizer.loadAndResize(sourceUri, maxDimension)
            }

            bitmap?.let {
                if (resizer.needsResize(it.width, it.height, maxDimension)) {
                    val resized = resizer.resizeToFit(it, maxDimension)
                    if (resized !== it) it.recycle()
                    resized
                } else {
                    it
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Cria thumbnail de uma imagem.
     *
     * @param sourceFile Arquivo de origem
     * @param size Tamanho da thumbnail
     * @param correctOrientation Corrigir orientação
     * @return Bitmap da thumbnail ou null
     */
    fun createThumbnail(
        sourceFile: File,
        size: Int = ImageResizer.THUMBNAIL_SIZE,
        correctOrientation: Boolean = true
    ): Bitmap? {
        return try {
            val bitmap = if (correctOrientation) {
                orientationCorrector.loadBitmapWithCorrectOrientation(sourceFile)
            } else {
                BitmapFactory.decodeFile(sourceFile.absolutePath)
            }

            bitmap?.let { resizer.createThumbnail(it, size) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Verifica integridade de uma imagem.
     *
     * @param file Arquivo de imagem
     * @param expectedHash Hash MD5 esperado
     * @return Resultado da verificação
     */
    fun verifyIntegrity(file: File, expectedHash: String): IntegrityCheckResult {
        val actualHash = hashCalculator.calculateMd5(file)
        return IntegrityCheckResult(
            isValid = actualHash?.equals(expectedHash, ignoreCase = true) == true,
            expectedHash = expectedHash,
            actualHash = actualHash,
            errorMessage = if (actualHash == null) "Falha ao calcular hash" else null
        )
    }
}

/**
 * Resultado do processamento de imagem.
 */
sealed class ImageProcessingResult {
    /**
     * Processamento bem-sucedido.
     */
    data class Success(
        val file: File,
        val originalWidth: Int,
        val originalHeight: Int,
        val finalWidth: Int,
        val finalHeight: Int,
        val wasResized: Boolean,
        val finalQuality: Int,
        val sizeBytes: Long,
        val hashMd5: String,
        val format: FotoFormato
    ) : ImageProcessingResult() {
        /** Tamanho formatado */
        val sizeFormatted: String
            get() = when {
                sizeBytes < 1024 -> "$sizeBytes B"
                sizeBytes < 1024 * 1024 -> String.format("%.1f KB", sizeBytes / 1024.0)
                else -> String.format("%.2f MB", sizeBytes / (1024.0 * 1024.0))
            }

        /** Dimensões originais formatadas */
        val originalDimensions: String get() = "${originalWidth}x${originalHeight}"

        /** Dimensões finais formatadas */
        val finalDimensions: String get() = "${finalWidth}x${finalHeight}"
    }

    /**
     * Erro no processamento.
     */
    data class Error(val message: String) : ImageProcessingResult()

    /** Verifica se foi sucesso */
    val isSuccess: Boolean get() = this is Success

    /** Verifica se foi erro */
    val isError: Boolean get() = this is Error

    /** Obtém o resultado de sucesso ou null */
    fun getOrNull(): Success? = this as? Success

    /** Obtém a mensagem de erro ou null */
    fun errorMessageOrNull(): String? = (this as? Error)?.message
}
