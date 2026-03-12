// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/util/foto/ImageResizer.kt
package br.com.tlmacedo.meuponto.util.foto

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Utilitário para redimensionamento de imagens.
 *
 * Oferece múltiplas estratégias de redimensionamento mantendo
 * o aspect ratio original da imagem.
 *
 * ## Estratégias:
 * - **FIT**: Redimensiona para caber dentro do limite (pode ser menor)
 * - **FILL**: Redimensiona para preencher o limite (pode cortar)
 * - **EXACT**: Redimensiona para dimensões exatas (pode distorcer)
 *
 * @author Thiago
 * @since 10.0.0
 */
@Singleton
class ImageResizer @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        /** Resolução padrão máxima (Full HD) */
        const val DEFAULT_MAX_DIMENSION = 1920

        /** Resolução para thumbnails */
        const val THUMBNAIL_SIZE = 200
    }

    /**
     * Estratégia de redimensionamento.
     */
    enum class ResizeStrategy {
        /** Cabe dentro das dimensões (pode ser menor) */
        FIT,
        /** Preenche as dimensões (pode cortar) */
        FILL,
        /** Dimensões exatas (pode distorcer) */
        EXACT
    }

    /**
     * Redimensiona um Bitmap para caber dentro de uma dimensão máxima.
     *
     * Mantém o aspect ratio original. Se a imagem já for menor,
     * retorna o Bitmap original.
     *
     * @param bitmap Bitmap original
     * @param maxDimension Dimensão máxima (largura ou altura)
     * @return Bitmap redimensionado ou original se não precisar
     */
    fun resizeToFit(bitmap: Bitmap, maxDimension: Int = DEFAULT_MAX_DIMENSION): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Se já está dentro do limite, retorna o original
        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        val ratio = minOf(
            maxDimension.toFloat() / width,
            maxDimension.toFloat() / height
        )

        val newWidth = (width * ratio).roundToInt()
        val newHeight = (height * ratio).roundToInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Redimensiona um Bitmap para largura específica mantendo aspect ratio.
     *
     * @param bitmap Bitmap original
     * @param targetWidth Largura desejada
     * @return Bitmap redimensionado
     */
    fun resizeToWidth(bitmap: Bitmap, targetWidth: Int): Bitmap {
        if (bitmap.width == targetWidth) return bitmap

        val ratio = targetWidth.toFloat() / bitmap.width
        val newHeight = (bitmap.height * ratio).roundToInt()

        return Bitmap.createScaledBitmap(bitmap, targetWidth, newHeight, true)
    }

    /**
     * Redimensiona um Bitmap para altura específica mantendo aspect ratio.
     *
     * @param bitmap Bitmap original
     * @param targetHeight Altura desejada
     * @return Bitmap redimensionado
     */
    fun resizeToHeight(bitmap: Bitmap, targetHeight: Int): Bitmap {
        if (bitmap.height == targetHeight) return bitmap

        val ratio = targetHeight.toFloat() / bitmap.height
        val newWidth = (bitmap.width * ratio).roundToInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, targetHeight, true)
    }

    /**
     * Redimensiona para dimensões exatas (pode distorcer).
     *
     * @param bitmap Bitmap original
     * @param width Largura desejada
     * @param height Altura desejada
     * @return Bitmap redimensionado
     */
    fun resizeExact(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        if (bitmap.width == width && bitmap.height == height) return bitmap
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    /**
     * Redimensiona usando estratégia especificada.
     *
     * @param bitmap Bitmap original
     * @param targetWidth Largura alvo
     * @param targetHeight Altura alvo
     * @param strategy Estratégia de redimensionamento
     * @return Bitmap redimensionado
     */
    fun resize(
        bitmap: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        strategy: ResizeStrategy = ResizeStrategy.FIT
    ): Bitmap {
        return when (strategy) {
            ResizeStrategy.FIT -> resizeToFit(bitmap, maxOf(targetWidth, targetHeight))
            ResizeStrategy.FILL -> resizeToFill(bitmap, targetWidth, targetHeight)
            ResizeStrategy.EXACT -> resizeExact(bitmap, targetWidth, targetHeight)
        }
    }

    /**
     * Redimensiona para preencher área (crop center).
     *
     * @param bitmap Bitmap original
     * @param targetWidth Largura alvo
     * @param targetHeight Altura alvo
     * @return Bitmap redimensionado e cortado
     */
    fun resizeToFill(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val ratio = maxOf(
            targetWidth.toFloat() / bitmap.width,
            targetHeight.toFloat() / bitmap.height
        )

        val scaledWidth = (bitmap.width * ratio).roundToInt()
        val scaledHeight = (bitmap.height * ratio).roundToInt()

        val scaled = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)

        // Crop center
        val x = (scaledWidth - targetWidth) / 2
        val y = (scaledHeight - targetHeight) / 2

        return try {
            Bitmap.createBitmap(scaled, x, y, targetWidth, targetHeight)
        } finally {
            if (scaled !== bitmap) {
                scaled.recycle()
            }
        }
    }

    /**
     * Cria uma thumbnail quadrada (crop center).
     *
     * @param bitmap Bitmap original
     * @param size Tamanho da thumbnail
     * @return Thumbnail quadrada
     */
    fun createSquareThumbnail(bitmap: Bitmap, size: Int = THUMBNAIL_SIZE): Bitmap {
        return resizeToFill(bitmap, size, size)
    }

    /**
     * Cria uma thumbnail mantendo aspect ratio.
     *
     * @param bitmap Bitmap original
     * @param maxSize Tamanho máximo
     * @return Thumbnail
     */
    fun createThumbnail(bitmap: Bitmap, maxSize: Int = THUMBNAIL_SIZE): Bitmap {
        return resizeToFit(bitmap, maxSize)
    }

    /**
     * Carrega e redimensiona uma imagem de arquivo com sampling otimizado.
     *
     * Usa inSampleSize para eficiência de memória ao carregar imagens grandes.
     *
     * @param file Arquivo de imagem
     * @param maxDimension Dimensão máxima desejada
     * @return Bitmap redimensionado ou null em caso de erro
     */
    fun loadAndResize(file: File, maxDimension: Int = DEFAULT_MAX_DIMENSION): Bitmap? {
        return try {
            // Primeiro, obtém as dimensões sem carregar a imagem
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)

            // Calcula o sample size ideal
            options.inSampleSize = calculateInSampleSize(
                options.outWidth,
                options.outHeight,
                maxDimension,
                maxDimension
            )
            options.inJustDecodeBounds = false

            // Carrega com sampling
            val bitmap = BitmapFactory.decodeFile(file.absolutePath, options) ?: return null

            // Redimensiona se ainda for maior que o limite
            if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                val resized = resizeToFit(bitmap, maxDimension)
                if (resized !== bitmap) bitmap.recycle()
                resized
            } else {
                bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Carrega e redimensiona uma imagem de URI com sampling otimizado.
     *
     * @param uri URI da imagem
     * @param maxDimension Dimensão máxima desejada
     * @return Bitmap redimensionado ou null em caso de erro
     */
    fun loadAndResize(uri: Uri, maxDimension: Int = DEFAULT_MAX_DIMENSION): Bitmap? {
        return try {
            // Primeiro, obtém as dimensões sem carregar a imagem
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            // Calcula o sample size ideal
            options.inSampleSize = calculateInSampleSize(
                options.outWidth,
                options.outHeight,
                maxDimension,
                maxDimension
            )
            options.inJustDecodeBounds = false

            // Carrega com sampling
            val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            } ?: return null

            // Redimensiona se ainda for maior que o limite
            if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                val resized = resizeToFit(bitmap, maxDimension)
                if (resized !== bitmap) bitmap.recycle()
                resized
            } else {
                bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calcula o inSampleSize ideal para carregar uma imagem.
     *
     * Um inSampleSize de 2 reduz a imagem pela metade em cada dimensão,
     * resultando em 1/4 dos pixels e uso de memória.
     *
     * @param width Largura original
     * @param height Altura original
     * @param reqWidth Largura requerida
     * @param reqHeight Altura requerida
     * @return Valor ideal de inSampleSize (sempre potência de 2)
     */
    fun calculateInSampleSize(
        width: Int,
        height: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calcula o maior inSampleSize que ainda é >= que as dimensões requeridas
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * Obtém as dimensões de uma imagem sem carregá-la na memória.
     *
     * @param file Arquivo de imagem
     * @return Par (largura, altura) ou null em caso de erro
     */
    fun getImageDimensions(file: File): Pair<Int, Int>? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)

            if (options.outWidth > 0 && options.outHeight > 0) {
                Pair(options.outWidth, options.outHeight)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtém as dimensões de uma imagem sem carregá-la na memória.
     *
     * @param uri URI da imagem
     * @return Par (largura, altura) ou null em caso de erro
     */
    fun getImageDimensions(uri: Uri): Pair<Int, Int>? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            if (options.outWidth > 0 && options.outHeight > 0) {
                Pair(options.outWidth, options.outHeight)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Verifica se uma imagem precisa ser redimensionada.
     *
     * @param width Largura atual
     * @param height Altura atual
     * @param maxDimension Dimensão máxima permitida
     * @return true se precisa redimensionamento
     */
    fun needsResize(width: Int, height: Int, maxDimension: Int): Boolean {
        return width > maxDimension || height > maxDimension
    }
}

/**
 * Informações sobre dimensões de imagem.
 */
data class ImageDimensions(
    val width: Int,
    val height: Int
) {
    val aspectRatio: Float get() = width.toFloat() / height
    val isPortrait: Boolean get() = height > width
    val isLandscape: Boolean get() = width > height
    val isSquare: Boolean get() = width == height
    val pixelCount: Long get() = width.toLong() * height
}
