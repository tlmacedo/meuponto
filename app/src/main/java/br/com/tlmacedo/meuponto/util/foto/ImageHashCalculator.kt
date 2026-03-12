// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/util/foto/ImageHashCalculator.kt
package br.com.tlmacedo.meuponto.util.foto

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculador de hash MD5 para verificação de integridade de imagens.
 *
 * O hash MD5 é usado para:
 * - Verificar integridade do arquivo após transferências
 * - Detectar modificações não autorizadas
 * - Identificar duplicatas
 * - Auditoria e rastreabilidade
 *
 * @author Thiago
 * @since 10.0.0
 */
@Singleton
class ImageHashCalculator @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        /** Algoritmo de hash utilizado */
        private const val HASH_ALGORITHM = "MD5"

        /** Tamanho do buffer para leitura de arquivos */
        private const val BUFFER_SIZE = 8192
    }

    /**
     * Calcula o hash MD5 de um arquivo.
     *
     * @param file Arquivo para calcular o hash
     * @return Hash MD5 em formato hexadecimal (32 caracteres) ou null em caso de erro
     */
    fun calculateMd5(file: File): String? {
        return try {
            FileInputStream(file).use { inputStream ->
                calculateMd5(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calcula o hash MD5 de um URI.
     *
     * @param uri URI do arquivo
     * @return Hash MD5 em formato hexadecimal (32 caracteres) ou null em caso de erro
     */
    fun calculateMd5(uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                calculateMd5(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calcula o hash MD5 de um InputStream.
     *
     * @param inputStream Stream de dados
     * @return Hash MD5 em formato hexadecimal (32 caracteres) ou null em caso de erro
     */
    fun calculateMd5(inputStream: InputStream): String? {
        return try {
            val digest = MessageDigest.getInstance(HASH_ALGORITHM)
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }

            bytesToHex(digest.digest())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calcula o hash MD5 de um ByteArray.
     *
     * @param data Dados para calcular o hash
     * @return Hash MD5 em formato hexadecimal (32 caracteres) ou null em caso de erro
     */
    fun calculateMd5(data: ByteArray): String? {
        return try {
            val digest = MessageDigest.getInstance(HASH_ALGORITHM)
            bytesToHex(digest.digest(data))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Verifica se o hash de um arquivo corresponde ao esperado.
     *
     * @param file Arquivo para verificar
     * @param expectedHash Hash esperado
     * @return true se os hashes correspondem
     */
    fun verifyHash(file: File, expectedHash: String): Boolean {
        val calculatedHash = calculateMd5(file) ?: return false
        return calculatedHash.equals(expectedHash, ignoreCase = true)
    }

    /**
     * Verifica se o hash de um URI corresponde ao esperado.
     *
     * @param uri URI para verificar
     * @param expectedHash Hash esperado
     * @return true se os hashes correspondem
     */
    fun verifyHash(uri: Uri, expectedHash: String): Boolean {
        val calculatedHash = calculateMd5(uri) ?: return false
        return calculatedHash.equals(expectedHash, ignoreCase = true)
    }

    /**
     * Verifica se dois arquivos são idênticos baseado no hash.
     *
     * @param file1 Primeiro arquivo
     * @param file2 Segundo arquivo
     * @return true se os arquivos são idênticos
     */
    fun areFilesIdentical(file1: File, file2: File): Boolean {
        val hash1 = calculateMd5(file1) ?: return false
        val hash2 = calculateMd5(file2) ?: return false
        return hash1.equals(hash2, ignoreCase = true)
    }

    /**
     * Converte array de bytes para string hexadecimal.
     *
     * @param bytes Array de bytes
     * @return String hexadecimal
     */
    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { byte ->
            "%02x".format(byte)
        }
    }

    /**
     * Valida formato de hash MD5.
     *
     * @param hash String para validar
     * @return true se é um hash MD5 válido (32 caracteres hexadecimais)
     */
    fun isValidMd5Format(hash: String): Boolean {
        return hash.length == 32 && hash.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
    }
}

/**
 * Resultado da verificação de integridade.
 */
data class IntegrityCheckResult(
    val isValid: Boolean,
    val expectedHash: String,
    val actualHash: String?,
    val errorMessage: String? = null
) {
    val hasError: Boolean get() = errorMessage != null
}
