// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/util/foto/ImageCompressor.kt
package br.com.tlmacedo.meuponto.util.foto

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utilitário para compressão inteligente de imagens.
 *
 * Oferece compressão adaptativa que tenta atingir um tamanho alvo
 * ajustando automaticamente a qualidade quando necessário.
 *
 * ## Funcionalidades:
 * - Compressão com qualidade fixa
 * - Compressão adaptativa para atingir tamanho alvo
 * - Suporte a JPEG e PNG
 * - Preservação de aspect ratio
 *
 * @author Thiago
 * @since 10.0.0
 */
@Singleton
class ImageCompressor @Inject constructor() {

    companion object {
        /** Qualidade mínima aceitável para JPEG */
        const val MIN_QUALITY = 30

        /** Qualidade máxima para JPEG */
        const val MAX_QUALITY = 100

        /** Passo de redução de qualidade na compressão adaptativa */
        const val QUALITY_STEP = 5

        /** Tamanho padrão máximo em bytes (1MB) */
        const val DEFAULT_MAX_SIZE_BYTES = 1024 * 1024L
    }

    /**
     * Comprime um Bitmap para JPEG com qualidade especificada.
     *
     * @param bitmap Bitmap a ser comprimido
     * @param quality Qualidade (1-100)
     * @return ByteArray com os dados comprimidos
     */
    fun compressToJpeg(bitmap: Bitmap, quality: Int = 85): ByteArray {
        val validQuality = quality.coerceIn(MIN_QUALITY, MAX_QUALITY)
        return ByteArrayOutputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, validQuality, outputStream)
            outputStream.toByteArray()
        }
    }

    /**
     * Comprime um Bitmap para PNG (sem perdas).
     *
     * @param bitmap Bitmap a ser comprimido
     * @return ByteArray com os dados comprimidos
     */
    fun compressToPng(bitmap: Bitmap): ByteArray {
        return ByteArrayOutputStream().use { outputStream ->
            // PNG ignora o parâmetro de qualidade
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.toByteArray()
        }
    }

    /**
     * Comprime um Bitmap com formato especificado.
     *
     * @param bitmap Bitmap a ser comprimido
     * @param format Formato de compressão
     * @param quality Qualidade (1-100, ignorado para PNG)
     * @return ByteArray com os dados comprimidos
     */
    fun compress(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        quality: Int = 85
    ): ByteArray {
        return when (format) {
            Bitmap.CompressFormat.PNG -> compressToPng(bitmap)
            else -> compressToJpeg(bitmap, quality)
        }
    }

    /**
     * Compressão adaptativa que tenta atingir um tamanho alvo.
     *
     * Começa com a qualidade especificada e reduz gradualmente até
     * atingir o tamanho máximo ou a qualidade mínima.
     *
     * @param bitmap Bitmap a ser comprimido
     * @param maxSizeBytes Tamanho máximo desejado em bytes
     * @param initialQuality Qualidade inicial (padrão: 85)
     * @param minQuality Qualidade mínima aceitável (padrão: 30)
     * @return Resultado da compressão com dados e qualidade final
     */
    fun compressAdaptive(
        bitmap: Bitmap,
        maxSizeBytes: Long = DEFAULT_MAX_SIZE_BYTES,
        initialQuality: Int = 85,
        minQuality: Int = MIN_QUALITY
    ): AdaptiveCompressionResult {
        var currentQuality = initialQuality.coerceIn(minQuality, MAX_QUALITY)
        var compressedData = compressToJpeg(bitmap, currentQuality)

        // Reduz a qualidade até atingir o tamanho alvo ou qualidade mínima
        while (compressedData.size > maxSizeBytes && currentQuality > minQuality) {
            currentQuality = (currentQuality - QUALITY_STEP).coerceAtLeast(minQuality)
            compressedData = compressToJpeg(bitmap, currentQuality)
        }

        return AdaptiveCompressionResult(
            data = compressedData,
            finalQuality = currentQuality,
            sizeBytes = compressedData.size.toLong(),
            targetAchieved = compressedData.size <= maxSizeBytes
        )
    }

    /**
     * Salva um Bitmap comprimido diretamente em um arquivo.
     *
     * @param bitmap Bitmap a ser salvo
     * @param outputFile Arquivo de destino
     * @param format Formato de compressão
     * @param quality Qualidade (1-100)
     * @return true se salvou com sucesso
     */
    fun saveToFile(
        bitmap: Bitmap,
        outputFile: File,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 85
    ): Boolean {
        return try {
            FileOutputStream(outputFile).use { outputStream ->
                bitmap.compress(format, quality.coerceIn(MIN_QUALITY, MAX_QUALITY), outputStream)
                outputStream.flush()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Salva com compressão adaptativa diretamente em arquivo.
     *
     * @param bitmap Bitmap a ser salvo
     * @param outputFile Arquivo de destino
     * @param maxSizeBytes Tamanho máximo em bytes
     * @param initialQuality Qualidade inicial
     * @return Resultado da compressão ou null em caso de erro
     */
    fun saveToFileAdaptive(
        bitmap: Bitmap,
        outputFile: File,
        maxSizeBytes: Long = DEFAULT_MAX_SIZE_BYTES,
        initialQuality: Int = 85
    ): AdaptiveCompressionResult? {
        return try {
            val result = compressAdaptive(bitmap, maxSizeBytes, initialQuality)

            FileOutputStream(outputFile).use { outputStream ->
                outputStream.write(result.data)
                outputStream.flush()
            }

            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Estima o tamanho final após compressão sem realmente comprimir.
     *
     * Útil para prever se uma imagem precisará de redimensionamento.
     *
     * @param bitmap Bitmap para estimar
     * @param quality Qualidade estimada
     * @return Tamanho estimado em bytes
     */
    fun estimateCompressedSize(bitmap: Bitmap, quality: Int = 85): Long {
        // Estimativa baseada em pixels e qualidade
        // JPEG típico: ~0.5 bytes por pixel em qualidade 85
        val pixelCount = bitmap.width.toLong() * bitmap.height
        val bytesPerPixel = when {
            quality >= 90 -> 0.8
            quality >= 80 -> 0.5
            quality >= 70 -> 0.35
            quality >= 60 -> 0.25
            else -> 0.15
        }
        return (pixelCount * bytesPerPixel).toLong()
    }

    /**
     * Recomprime um arquivo de imagem existente.
     *
     * @param inputFile Arquivo de entrada
     * @param outputFile Arquivo de saída
     * @param quality Qualidade desejada
     * @return true se recomprimiu com sucesso
     */
    fun recompressFile(
        inputFile: File,
        outputFile: File,
        quality: Int = 85
    ): Boolean {
        return try {
            val bitmap = BitmapFactory.decodeFile(inputFile.absolutePath) ?: return false
            val result = saveToFile(bitmap, outputFile, Bitmap.CompressFormat.JPEG, quality)
            bitmap.recycle()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

/**
 * Resultado da compressão adaptativa.
 *
 * @property data Dados comprimidos
 * @property finalQuality Qualidade final utilizada
 * @property sizeBytes Tamanho final em bytes
 * @property targetAchieved true se atingiu o tamanho alvo
 */
data class AdaptiveCompressionResult(
    val data: ByteArray,
    val finalQuality: Int,
    val sizeBytes: Long,
    val targetAchieved: Boolean
) {
    /** Tamanho formatado para exibição */
    val sizeFormatted: String
        get() = when {
            sizeBytes < 1024 -> "$sizeBytes B"
            sizeBytes < 1024 * 1024 -> String.format("%.1f KB", sizeBytes / 1024.0)
            else -> String.format("%.2f MB", sizeBytes / (1024.0 * 1024.0))
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AdaptiveCompressionResult
        return data.contentEquals(other.data) &&
                finalQuality == other.finalQuality &&
                sizeBytes == other.sizeBytes &&
                targetAchieved == other.targetAchieved
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + finalQuality
        result = 31 * result + sizeBytes.hashCode()
        result = 31 * result + targetAchieved.hashCode()
        return result
    }
}
