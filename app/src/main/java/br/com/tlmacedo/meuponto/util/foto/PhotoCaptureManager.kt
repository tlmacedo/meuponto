// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/util/foto/PhotoCaptureManager.kt
package br.com.tlmacedo.meuponto.util.foto

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import br.com.tlmacedo.meuponto.domain.model.FotoOrigem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador de captura de fotos.
 *
 * Coordena o fluxo de captura de fotos via câmera ou seleção da galeria,
 * gerenciando arquivos temporários e estado da captura.
 *
 * ## Fluxo de Captura via Câmera:
 * 1. Chamar `prepareForCameraCapture()` para criar arquivo temporário e obter URI
 * 2. Usar o URI com ActivityResultLauncher do TakePicture
 * 3. Após captura bem-sucedida, chamar `processCapturedPhoto()` ou `onCameraCaptureSuccess()`
 * 4. Se cancelado, chamar `onCameraCaptureCancelled()` para limpar
 *
 * ## Fluxo de Seleção via Galeria:
 * 1. Usar ActivityResultLauncher do GetContent ou PickVisualMedia
 * 2. Com o URI retornado, chamar `processSelectedPhoto()`
 *
 * @author Thiago
 * @since 10.0.0
 */
@Singleton
class PhotoCaptureManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storageManager: FotoStorageManager
) {

    companion object {
        private const val TEMP_DIR = "temp_camera"
        private const val FILE_PROVIDER_SUFFIX = ".fileprovider"
    }

    private val timestampFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")

    // Estado atual da captura
    private val _captureState = MutableStateFlow<CaptureState>(CaptureState.Idle)
    val captureState: StateFlow<CaptureState> = _captureState.asStateFlow()

    // Arquivo temporário atual (para captura via câmera)
    private var currentTempFile: File? = null
    private var currentTempUri: Uri? = null

    /**
     * Prepara para captura via câmera.
     *
     * Cria um arquivo temporário e retorna o URI para uso com Intent da câmera.
     *
     * @return URI para captura ou null em caso de erro
     */
    fun prepareForCameraCapture(): Uri? {
        return try {
            // Limpar temporário anterior se existir
            cleanupTempFile()

            // Criar diretório temporário
            val tempDir = File(context.cacheDir, TEMP_DIR).apply {
                if (!exists()) mkdirs()
            }

            // Criar arquivo temporário
            val timestamp = LocalDateTime.now().format(timestampFormatter)
            val fileName = "CAPTURE_$timestamp.jpg"
            currentTempFile = File(tempDir, fileName)

            // Criar URI via FileProvider
            val authority = "${context.packageName}$FILE_PROVIDER_SUFFIX"
            currentTempUri = FileProvider.getUriForFile(context, authority, currentTempFile!!)

            _captureState.value = CaptureState.WaitingForCamera(currentTempUri!!)

            currentTempUri
        } catch (e: Exception) {
            e.printStackTrace()
            _captureState.value = CaptureState.Error("Erro ao preparar câmera: ${e.message}")
            null
        }
    }

    /**
     * Chamado quando a captura via câmera foi bem-sucedida.
     *
     * O arquivo temporário já contém a foto capturada.
     */
    fun onCameraCaptureSuccess() {
        currentTempFile?.let { file ->
            if (file.exists() && file.length() > 0) {
                _captureState.value = CaptureState.PhotoCaptured(
                    uri = currentTempUri!!,
                    file = file,
                    source = FotoOrigem.CAMERA
                )
            } else {
                _captureState.value = CaptureState.Error("Arquivo de captura vazio ou inexistente")
                cleanupTempFile()
            }
        } ?: run {
            _captureState.value = CaptureState.Error("Arquivo temporário não encontrado")
        }
    }

    /**
     * Chamado quando a captura via câmera foi cancelada.
     */
    fun onCameraCaptureCancelled() {
        cleanupTempFile()
        _captureState.value = CaptureState.Cancelled
    }

    /**
     * Chamado quando uma foto foi selecionada da galeria.
     *
     * @param uri URI da foto selecionada
     */
    fun onGalleryPhotoSelected(uri: Uri) {
        _captureState.value = CaptureState.PhotoCaptured(
            uri = uri,
            file = null, // Galeria não usa arquivo temporário
            source = FotoOrigem.GALERIA
        )
    }

    /**
     * Processa e salva a foto capturada/selecionada.
     *
     * @param empregoId ID do emprego
     * @param pontoId ID do ponto
     * @param data Data do ponto
     * @param config Configuração do emprego
     * @param gpsData Dados de localização (opcional)
     * @return Resultado do salvamento
     */
    suspend fun processAndSavePhoto(
        empregoId: Long,
        pontoId: Long,
        data: LocalDate,
        config: ConfiguracaoEmprego,
        gpsData: GpsData? = null
    ): SavePhotoResult {
        val currentState = _captureState.value

        if (currentState !is CaptureState.PhotoCaptured) {
            return SavePhotoResult.Error("Nenhuma foto capturada para processar")
        }

        _captureState.value = CaptureState.Processing

        return try {
            val result = when (currentState.source) {
                FotoOrigem.CAMERA -> {
                    // Para câmera, usar o arquivo temporário
                    currentState.file?.let { file ->
                        storageManager.savePhoto(
                            sourceFile = file,
                            empregoId = empregoId,
                            pontoId = pontoId,
                            data = data,
                            config = config,
                            gpsData = gpsData
                        )
                    } ?: SavePhotoResult.Error("Arquivo temporário não disponível")
                }
                FotoOrigem.GALERIA -> {
                    // Para galeria, usar o URI
                    storageManager.savePhoto(
                        sourceUri = currentState.uri,
                        empregoId = empregoId,
                        pontoId = pontoId,
                        data = data,
                        config = config,
                        gpsData = gpsData
                    )
                }
            }

            when (result) {
                is SavePhotoResult.Success -> {
                    _captureState.value = CaptureState.Completed(result)
                    cleanupTempFile()
                }
                is SavePhotoResult.Error -> {
                    _captureState.value = CaptureState.Error(result.message)
                }
            }

            result
        } catch (e: Exception) {
            e.printStackTrace()
            val error = SavePhotoResult.Error("Erro ao processar foto: ${e.message}")
            _captureState.value = CaptureState.Error(error.message)
            error
        }
    }

    /**
     * Reseta o estado para idle.
     */
    fun reset() {
        cleanupTempFile()
        _captureState.value = CaptureState.Idle
    }

    /**
     * Limpa arquivos temporários antigos.
     *
     * @param maxAgeMinutes Idade máxima em minutos
     * @return Número de arquivos removidos
     */
    fun cleanupOldTempFiles(maxAgeMinutes: Int = 60): Int {
        return try {
            val tempDir = File(context.cacheDir, TEMP_DIR)
            if (!tempDir.exists()) return 0

            val maxAgeMillis = maxAgeMinutes * 60 * 1000L
            val cutoffTime = System.currentTimeMillis() - maxAgeMillis
            var count = 0

            tempDir.listFiles()?.forEach { file ->
                if (file.lastModified() < cutoffTime) {
                    if (file.delete()) count++
                }
            }

            count
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Verifica se tem foto capturada pronta para processar.
     */
    fun hasPhotoCaptured(): Boolean {
        return _captureState.value is CaptureState.PhotoCaptured
    }

    /**
     * Obtém a origem da foto capturada.
     */
    fun getCapturedPhotoSource(): FotoOrigem? {
        return (_captureState.value as? CaptureState.PhotoCaptured)?.source
    }

    /**
     * Obtém o URI da foto capturada (para preview).
     */
    fun getCapturedPhotoUri(): Uri? {
        return (_captureState.value as? CaptureState.PhotoCaptured)?.uri
    }

    // ════════════════════════════════════════════════════════════════════════
    // MÉTODOS PRIVADOS
    // ════════════════════════════════════════════════════════════════════════

    private fun cleanupTempFile() {
        currentTempFile?.let { file ->
            try {
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                // Ignora erro na limpeza
            }
        }
        currentTempFile = null
        currentTempUri = null
    }
}

/**
 * Estados possíveis do fluxo de captura.
 */
sealed class CaptureState {
    /** Aguardando início da captura */
    object Idle : CaptureState()

    /** Aguardando retorno da câmera */
    data class WaitingForCamera(val uri: Uri) : CaptureState()

    /** Foto capturada/selecionada, pronta para processar */
    data class PhotoCaptured(
        val uri: Uri,
        val file: File?,
        val source: FotoOrigem
    ) : CaptureState()

    /** Processando a foto */
    object Processing : CaptureState()

    /** Captura completada com sucesso */
    data class Completed(val result: SavePhotoResult.Success) : CaptureState()

    /** Captura cancelada pelo usuário */
    object Cancelled : CaptureState()

    /** Erro na captura */
    data class Error(val message: String) : CaptureState()
}
