// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/util/foto/ImageOrientationCorrector.kt
package br.com.tlmacedo.meuponto.util.foto

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Corretor de orientação de imagens baseado em metadados EXIF.
 *
 * @author Thiago
 * @since 10.0.0
 */
@Singleton
class ImageOrientationCorrector @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Obtém a orientação EXIF de um arquivo.
     */
    fun getOrientation(file: File): Int {
        return try {
            val exif = ExifInterface(file)
            exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
    }

    /**
     * Obtém a orientação EXIF de um URI.
     */
    fun getOrientation(uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
    }

    /**
     * Corrige a orientação de um Bitmap baseado na orientação EXIF.
     */
    fun correctOrientation(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()

        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> return bitmap
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return bitmap
        }

        return try {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            bitmap
        }
    }

    /**
     * Carrega um Bitmap de arquivo já com orientação corrigida.
     */
    fun loadBitmapWithCorrectOrientation(file: File): Bitmap? {
        return try {
            val orientation = getOrientation(file)
            val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return null

            if (orientation == ExifInterface.ORIENTATION_NORMAL) {
                bitmap
            } else {
                val corrected = correctOrientation(bitmap, orientation)
                if (corrected !== bitmap) bitmap.recycle()
                corrected
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Carrega um Bitmap de URI já com orientação corrigida.
     */
    fun loadBitmapWithCorrectOrientation(uri: Uri): Bitmap? {
        return try {
            val orientation = getOrientation(uri)
            val bitmap = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            } ?: return null

            if (orientation == ExifInterface.ORIENTATION_NORMAL) {
                bitmap
            } else {
                val corrected = correctOrientation(bitmap, orientation)
                if (corrected !== bitmap) bitmap.recycle()
                corrected
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Verifica se uma imagem precisa de correção de orientação.
     */
    fun needsCorrection(file: File): Boolean {
        return getOrientation(file) != ExifInterface.ORIENTATION_NORMAL
    }

    /**
     * Verifica se uma imagem precisa de correção de orientação.
     */
    fun needsCorrection(uri: Uri): Boolean {
        return getOrientation(uri) != ExifInterface.ORIENTATION_NORMAL
    }
}
