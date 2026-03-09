// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/util/ComprovanteImageStorage.kt
package br.com.tlmacedo.meuponto.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utilitário para gerenciar o armazenamento de fotos de comprovantes de ponto.
 *
 * As imagens são salvas no diretório interno do app (não acessível externamente),
 * organizadas por emprego e data para facilitar a manutenção.
 *
 * Estrutura de diretórios:
 * ```
 * files/
 * └── comprovantes/
 *     └── emprego_{id}/
 *         └── {ano}/
 *             └── {mes}/
 *                 └── ponto_{pontoId}_{timestamp}.jpg
 * ```
 *
 * @author Thiago
 * @since 9.0.0
 */
@Singleton
class ComprovanteImageStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val ROOT_DIR = "comprovantes"
        private const val IMAGE_QUALITY = 85 // Qualidade JPEG (0-100)
        private const val MAX_IMAGE_DIMENSION = 1920 // Máximo largura/altura em pixels
        private const val THUMBNAIL_SIZE = 200 // Tamanho da thumbnail em pixels
    }

    private val timestampFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")

    // ========================================================================
    // ACESSO PÚBLICO AO CONTEXTO E DIRETÓRIO
    // ========================================================================

    /**
     * Contexto da aplicação (necessário para FileProvider).
     */
    val appContext: Context get() = context

    /**
     * Obtém o diretório raiz de comprovantes.
     * Necessário para componentes de UI que precisam do diretório base.
     */
    fun getComprovantesDirectory(): File = getRootDirectory()

    // ========================================================================
    // SUPORTE À CÂMERA
    // ========================================================================

    /**
     * Cria um arquivo temporário para captura de foto pela câmera.
     *
     * @param empregoId ID do emprego
     * @param data Data do ponto
     * @return File temporário pronto para receber a foto
     */
    fun createTempFileForCamera(empregoId: Long, data: LocalDate): File {
        val directory = getOrCreateDirectory(empregoId, data)
        val timestamp = LocalDateTime.now().format(timestampFormatter)
        val fileName = "temp_camera_$timestamp.jpg"
        return File(directory, fileName)
    }

    // ========================================================================
    // SALVAR IMAGEM
    // ========================================================================

    /**
     * Salva uma imagem de comprovante a partir de um URI (galeria ou câmera).
     *
     * @param uri URI da imagem original
     * @param empregoId ID do emprego associado
     * @param pontoId ID do ponto (pode ser 0 se ainda não persistido)
     * @param data Data do ponto
     * @return Caminho relativo da imagem salva, ou null em caso de erro
     */
    fun saveFromUri(
        uri: Uri,
        empregoId: Long,
        pontoId: Long,
        data: LocalDate
    ): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) return null

            val resizedBitmap = resizeIfNeeded(originalBitmap)
            saveBitmap(resizedBitmap, empregoId, pontoId, data).also {
                if (resizedBitmap != originalBitmap) {
                    resizedBitmap.recycle()
                }
                originalBitmap.recycle()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Salva uma imagem de comprovante a partir de um Bitmap.
     *
     * @param bitmap Bitmap da imagem
     * @param empregoId ID do emprego associado
     * @param pontoId ID do ponto
     * @param data Data do ponto
     * @return Caminho relativo da imagem salva, ou null em caso de erro
     */
    fun saveFromBitmap(
        bitmap: Bitmap,
        empregoId: Long,
        pontoId: Long,
        data: LocalDate
    ): String? {
        return try {
            val resizedBitmap = resizeIfNeeded(bitmap)
            saveBitmap(resizedBitmap, empregoId, pontoId, data).also {
                if (resizedBitmap != bitmap) {
                    resizedBitmap.recycle()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveBitmap(
        bitmap: Bitmap,
        empregoId: Long,
        pontoId: Long,
        data: LocalDate
    ): String? {
        return try {
            val directory = getOrCreateDirectory(empregoId, data)
            val fileName = generateFileName(pontoId)
            val file = File(directory, fileName)

            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream)
                outputStream.flush()
            }

            // Retorna caminho relativo a partir do diretório de comprovantes
            getRelativePath(empregoId, data, fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ========================================================================
    // CARREGAR IMAGEM
    // ========================================================================

    /**
     * Carrega uma imagem de comprovante como Bitmap.
     *
     * @param relativePath Caminho relativo retornado por saveFromUri/saveFromBitmap
     * @return Bitmap da imagem ou null se não encontrada
     */
    fun loadBitmap(relativePath: String): Bitmap? {
        return try {
            val file = getFileFromRelativePath(relativePath)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Carrega uma thumbnail da imagem para exibição em listas.
     *
     * @param relativePath Caminho relativo da imagem
     * @return Bitmap da thumbnail ou null se não encontrada
     */
    fun loadThumbnail(relativePath: String): Bitmap? {
        return try {
            val file = getFileFromRelativePath(relativePath)
            if (!file.exists()) return null

            // Decodifica apenas as dimensões primeiro
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)

            // Calcula o fator de escala
            val scaleFactor = maxOf(
                options.outWidth / THUMBNAIL_SIZE,
                options.outHeight / THUMBNAIL_SIZE
            ).coerceAtLeast(1)

            // Decodifica com escala reduzida
            options.apply {
                inJustDecodeBounds = false
                inSampleSize = scaleFactor
            }

            BitmapFactory.decodeFile(file.absolutePath, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtém o arquivo absoluto a partir do caminho relativo.
     *
     * @param relativePath Caminho relativo da imagem
     * @return File apontando para a imagem
     */
    fun getAbsoluteFile(relativePath: String): File {
        return getFileFromRelativePath(relativePath)
    }

    /**
     * Verifica se a imagem existe no armazenamento.
     *
     * @param relativePath Caminho relativo da imagem
     * @return true se o arquivo existe
     */
    fun exists(relativePath: String): Boolean {
        return try {
            getFileFromRelativePath(relativePath).exists()
        } catch (e: Exception) {
            false
        }
    }

    // ========================================================================
    // DELETAR IMAGEM
    // ========================================================================

    /**
     * Deleta uma imagem de comprovante.
     *
     * @param relativePath Caminho relativo da imagem
     * @return true se deletado com sucesso ou se não existia
     */
    fun delete(relativePath: String): Boolean {
        return try {
            val file = getFileFromRelativePath(relativePath)
            if (file.exists()) {
                file.delete()
            } else {
                true // Arquivo já não existe
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Deleta todas as imagens de comprovantes de um emprego.
     * Útil quando o emprego é excluído.
     *
     * @param empregoId ID do emprego
     * @return Número de arquivos deletados
     */
    fun deleteAllForEmprego(empregoId: Long): Int {
        return try {
            val empregoDir = File(getRootDirectory(), "emprego_$empregoId")
            if (empregoDir.exists()) {
                val count = empregoDir.walkTopDown().filter { it.isFile }.count()
                empregoDir.deleteRecursively()
                count
            } else {
                0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    // ========================================================================
    // MANUTENÇÃO
    // ========================================================================

    /**
     * Obtém o tamanho total ocupado por comprovantes em bytes.
     */
    fun getTotalStorageSize(): Long {
        return try {
            getRootDirectory().walkTopDown()
                .filter { it.isFile }
                .sumOf { it.length() }
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Obtém o tamanho formatado (ex: "12.5 MB").
     */
    fun getTotalStorageSizeFormatted(): String {
        val bytes = getTotalStorageSize()
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
            bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024))
            else -> String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024))
        }
    }

    /**
     * Conta o número total de comprovantes armazenados.
     */
    fun getTotalImageCount(): Int {
        return try {
            getRootDirectory().walkTopDown()
                .filter { it.isFile && it.extension.lowercase() == "jpg" }
                .count()
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Limpa imagens órfãs (sem ponto associado no banco).
     * Deve ser chamado passando a lista de caminhos válidos.
     *
     * @param validPaths Lista de caminhos que ainda estão em uso
     * @return Número de arquivos removidos
     */
    fun cleanupOrphanImages(validPaths: Set<String>): Int {
        var removedCount = 0
        try {
            getRootDirectory().walkTopDown()
                .filter { it.isFile && it.extension.lowercase() == "jpg" }
                .forEach { file ->
                    val relativePath = file.absolutePath
                        .removePrefix(getRootDirectory().absolutePath)
                        .removePrefix("/")

                    if (relativePath !in validPaths) {
                        if (file.delete()) {
                            removedCount++
                        }
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return removedCount
    }

    // ========================================================================
    // HELPERS PRIVADOS
    // ========================================================================

    private fun getRootDirectory(): File {
        return File(context.filesDir, ROOT_DIR).apply {
            if (!exists()) mkdirs()
        }
    }

    private fun getOrCreateDirectory(empregoId: Long, data: LocalDate): File {
        val path = "emprego_$empregoId/${data.year}/${String.format("%02d", data.monthValue)}"
        return File(getRootDirectory(), path).apply {
            if (!exists()) mkdirs()
        }
    }

    private fun generateFileName(pontoId: Long): String {
        val timestamp = LocalDateTime.now().format(timestampFormatter)
        return "ponto_${pontoId}_$timestamp.jpg"
    }

    private fun getRelativePath(empregoId: Long, data: LocalDate, fileName: String): String {
        return "emprego_$empregoId/${data.year}/${String.format("%02d", data.monthValue)}/$fileName"
    }

    private fun getFileFromRelativePath(relativePath: String): File {
        return File(getRootDirectory(), relativePath)
    }

    private fun resizeIfNeeded(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= MAX_IMAGE_DIMENSION && height <= MAX_IMAGE_DIMENSION) {
            return bitmap
        }

        val ratio = minOf(
            MAX_IMAGE_DIMENSION.toFloat() / width,
            MAX_IMAGE_DIMENSION.toFloat() / height
        )

        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
