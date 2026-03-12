// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/editponto/EditPontoViewModel.kt
package br.com.tlmacedo.meuponto.presentation.screen.editponto

import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.tlmacedo.meuponto.domain.model.MotivoEdicao
import br.com.tlmacedo.meuponto.domain.model.TipoPonto
import br.com.tlmacedo.meuponto.domain.repository.ConfiguracaoEmpregoRepository
import br.com.tlmacedo.meuponto.domain.repository.PontoRepository
import br.com.tlmacedo.meuponto.domain.usecase.ponto.EditarPontoUseCase
import br.com.tlmacedo.meuponto.domain.usecase.ponto.ExcluirPontoUseCase
import br.com.tlmacedo.meuponto.presentation.navigation.MeuPontoDestinations
import br.com.tlmacedo.meuponto.util.ComprovanteImageStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

/**
 * ViewModel da tela de edição de ponto.
 *
 * @author Thiago
 * @since 3.5.0
 * @updated 9.0.0 - Adicionado suporte a foto de comprovante
 */
@HiltViewModel
class EditPontoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val pontoRepository: PontoRepository,
    private val configuracaoEmpregoRepository: ConfiguracaoEmpregoRepository,
    private val editarPontoUseCase: EditarPontoUseCase,
    private val excluirPontoUseCase: ExcluirPontoUseCase,
    private val comprovanteImageStorage: ComprovanteImageStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPontoUiState())
    val uiState: StateFlow<EditPontoUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<EditPontoUiEvent>()
    val uiEvent: SharedFlow<EditPontoUiEvent> = _uiEvent.asSharedFlow()

    private val pontoId: Long = savedStateHandle.get<Long>(MeuPontoDestinations.ARG_PONTO_ID) ?: -1L

    init {
        if (pontoId > 0) {
            carregarPonto()
        }
    }

    fun onAction(action: EditPontoAction) {
        when (action) {
            // Campos
            is EditPontoAction.AtualizarData -> atualizarData(action.data)
            is EditPontoAction.AtualizarHora -> atualizarHora(action.hora)
            is EditPontoAction.AtualizarNsr -> atualizarNsr(action.nsr)
            is EditPontoAction.AtualizarLocalizacao -> atualizarLocalizacao(
                action.latitude, action.longitude, action.endereco
            )
            is EditPontoAction.AtualizarObservacao -> atualizarObservacao(action.observacao)

            // Motivo
            is EditPontoAction.SelecionarMotivo -> selecionarMotivo(action.motivo)
            is EditPontoAction.AtualizarMotivoDetalhes -> atualizarMotivoDetalhes(action.detalhes)
            EditPontoAction.AbrirMotivoDropdown -> _uiState.update { it.copy(showMotivoDropdown = true) }
            EditPontoAction.FecharMotivoDropdown -> _uiState.update { it.copy(showMotivoDropdown = false) }

            // Foto de comprovante
            is EditPontoAction.SelecionarFotoComprovante -> selecionarFotoComprovante(action.uri)
            EditPontoAction.RemoverFotoComprovante -> removerFotoComprovante()
            EditPontoAction.AbrirVisualizadorFoto -> _uiState.update { it.copy(showFotoPreview = true) }
            EditPontoAction.FecharVisualizadorFoto -> _uiState.update { it.copy(showFotoPreview = false) }

            // Dialogs
            EditPontoAction.AbrirTimePicker -> _uiState.update { it.copy(showTimePicker = true) }
            EditPontoAction.FecharTimePicker -> _uiState.update { it.copy(showTimePicker = false) }
            EditPontoAction.AbrirDatePicker -> _uiState.update { it.copy(showDatePicker = true) }
            EditPontoAction.FecharDatePicker -> _uiState.update { it.copy(showDatePicker = false) }
            EditPontoAction.AbrirLocationPicker -> _uiState.update { it.copy(showLocationPicker = true) }
            EditPontoAction.FecharLocationPicker -> _uiState.update { it.copy(showLocationPicker = false) }
            EditPontoAction.CapturarLocalizacao -> capturarLocalizacao()
            EditPontoAction.LimparLocalizacao -> limparLocalizacao()

            // Ações principais
            EditPontoAction.Salvar -> salvar()
            EditPontoAction.SolicitarExclusao -> _uiState.update { it.copy(showDeleteConfirmDialog = true) }
            EditPontoAction.ConfirmarExclusao -> confirmarExclusao()
            EditPontoAction.CancelarExclusao -> _uiState.update { it.copy(showDeleteConfirmDialog = false) }
            EditPontoAction.Cancelar -> cancelar()
            EditPontoAction.LimparErro -> _uiState.update { it.copy(erro = null) }
        }
    }

    // ========================================================================
    // Carregamento
    // ========================================================================

    private fun carregarPonto() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val ponto = pontoRepository.buscarPorId(pontoId)
                if (ponto == null) {
                    _uiState.update { it.copy(isLoading = false, erro = "Ponto não encontrado") }
                    return@launch
                }

                // Buscar índice do ponto para determinar tipo
                val pontosDoDia = pontoRepository.buscarPorEmpregoEData(
                    ponto.empregoId,
                    ponto.data
                ).sortedBy { it.dataHora }
                val indice = pontosDoDia.indexOfFirst { it.id == ponto.id }
                val tipoPonto = TipoPonto.getTipoPorIndice(indice)

                // Buscar configuração do emprego
                val configuracao = configuracaoEmpregoRepository.buscarPorEmpregoId(ponto.empregoId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        pontoId = ponto.id,
                        pontoOriginal = ponto,
                        empregoId = ponto.empregoId,
                        tipoPonto = tipoPonto,
                        indice = indice,
                        data = ponto.data,
                        hora = ponto.hora,
                        nsr = ponto.nsr ?: "",
                        latitude = ponto.latitude,
                        longitude = ponto.longitude,
                        endereco = ponto.endereco ?: "",
                        observacao = ponto.observacao ?: "",
                        fotoComprovantePath = ponto.fotoComprovantePath,
                        configuracao = configuracao
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, erro = "Erro ao carregar ponto: ${e.message}")
                }
            }
        }
    }

    // ========================================================================
    // Atualizações de campos
    // ========================================================================

    private fun atualizarData(data: LocalDate) {
        _uiState.update { it.copy(data = data, showDatePicker = false) }
    }

    private fun atualizarHora(hora: LocalTime) {
        _uiState.update { it.copy(hora = hora, showTimePicker = false) }
    }

    private fun atualizarNsr(nsr: String) {
        val state = _uiState.value
        val nsrFiltrado = when (state.tipoNsr) {
            br.com.tlmacedo.meuponto.domain.model.TipoNsr.NUMERICO -> nsr.filter { it.isDigit() }
            br.com.tlmacedo.meuponto.domain.model.TipoNsr.ALFANUMERICO -> nsr.filter { it.isLetterOrDigit() }.uppercase()
        }
        _uiState.update { it.copy(nsr = nsrFiltrado) }
    }

    private fun atualizarLocalizacao(latitude: Double, longitude: Double, endereco: String?) {
        _uiState.update {
            it.copy(
                latitude = latitude,
                longitude = longitude,
                endereco = endereco ?: "",
                showLocationPicker = false
            )
        }
    }

    private fun atualizarObservacao(observacao: String) {
        _uiState.update { it.copy(observacao = observacao) }
    }

    private fun selecionarMotivo(motivo: MotivoEdicao) {
        _uiState.update {
            it.copy(
                motivoSelecionado = motivo,
                motivoDetalhes = if (motivo.requerDetalhes) it.motivoDetalhes else "",
                showMotivoDropdown = false
            )
        }
    }

    private fun atualizarMotivoDetalhes(detalhes: String) {
        _uiState.update { it.copy(motivoDetalhes = detalhes) }
    }

    private fun capturarLocalizacao() {
        _uiState.update {
            it.copy(
                showLocationPicker = false,
                erro = "Captura de localização será implementada em breve"
            )
        }
    }

    private fun limparLocalizacao() {
        _uiState.update {
            it.copy(latitude = null, longitude = null, endereco = "")
        }
    }

    // ========================================================================
    // Foto de comprovante
    // ========================================================================

    private fun selecionarFotoComprovante(uri: Uri) {
        _uiState.update { it.copy(fotoComprovanteUri = uri) }
    }

    private fun removerFotoComprovante() {
        _uiState.update {
            it.copy(
                fotoComprovanteUri = null,
                fotoComprovantePath = null
            )
        }
    }

    /**
     * Cria URI temporário para captura de foto com a câmera.
     */
    fun criarCameraUri(): Uri? {
        return try {
            val state = _uiState.value
            val tempFile = comprovanteImageStorage.createTempFileForCamera(
                empregoId = state.empregoId,
                data = state.data
            )
            FileProvider.getUriForFile(
                comprovanteImageStorage.appContext,
                "${comprovanteImageStorage.appContext.packageName}.fileprovider",
                tempFile
            )
        } catch (e: Exception) {
            android.util.Log.e("EditPontoViewModel", "Erro ao criar URI da câmera: ${e.message}")
            null
        }
    }

    /**
     * Obtém o diretório base para imagens de comprovantes.
     */
    fun getComprovantesDirectory(): File? {
        return try {
            comprovanteImageStorage.getComprovantesDirectory()
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun salvarFotoComprovante(uri: Uri, pontoId: Long): String? {
        return try {
            val state = _uiState.value
            comprovanteImageStorage.saveFromUri(
                uri = uri,
                empregoId = state.empregoId,
                pontoId = pontoId,
                dataHora = LocalDateTime.of(state.data, state.hora)
            )
        } catch (e: Exception) {
            android.util.Log.e("EditPontoViewModel", "Erro ao salvar foto: ${e.message}")
            null
        }
    }

    private fun deletarFotoAnterior(path: String?) {
        if (path.isNullOrBlank()) return
        try {
            comprovanteImageStorage.delete(path)
        } catch (e: Exception) {
            android.util.Log.e("EditPontoViewModel", "Erro ao deletar foto anterior: ${e.message}")
        }
    }

    // ========================================================================
    // Ações principais
    // ========================================================================

    private fun salvar() {
        val state = _uiState.value

        if (!state.podeSalvar) {
            val erros = listOfNotNull(
                state.erroMotivo,
                state.erroNsr,
                state.erroLocalizacao,
                state.erroFoto
            )
            _uiState.update { it.copy(erro = erros.joinToString("\n")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                // Processar foto primeiro
                var novoFotoPath: String? = state.fotoComprovantePath
                val fotoAnteriorPath = state.pontoOriginal?.fotoComprovantePath

                // Se tem nova foto selecionada
                if (state.fotoComprovanteUri != null) {
                    // Deletar foto anterior se existia
                    deletarFotoAnterior(fotoAnteriorPath)

                    // Salvar nova foto
                    novoFotoPath = salvarFotoComprovante(state.fotoComprovanteUri, state.pontoId)
                    if (novoFotoPath == null && state.fotoObrigatoria) {
                        _uiState.update {
                            it.copy(isSaving = false, erro = "Erro ao salvar foto do comprovante")
                        }
                        return@launch
                    }
                } else if (state.fotoRemovida) {
                    // Foto foi removida
                    deletarFotoAnterior(fotoAnteriorPath)
                    novoFotoPath = null
                }

                // Editar ponto
                val parametros = EditarPontoUseCase.Parametros(
                    pontoId = state.pontoId,
                    dataHora = LocalDateTime.of(state.data, state.hora),
                    nsr = state.nsr.ifBlank { null },
                    latitude = state.latitude,
                    longitude = state.longitude,
                    endereco = state.endereco.ifBlank { null },
                    observacao = state.observacao.ifBlank { null },
                    motivo = state.motivo
                )

                when (val resultado = editarPontoUseCase(parametros)) {
                    is EditarPontoUseCase.Resultado.Sucesso -> {
                        // Atualizar foto se foi alterada
                        if (state.fotoAlterada) {
                            pontoRepository.atualizarFotoComprovante(state.pontoId, novoFotoPath)
                        }

                        _uiEvent.emit(EditPontoUiEvent.Salvo("Ponto atualizado com sucesso"))
                        _uiEvent.emit(EditPontoUiEvent.Voltar)
                    }
                    is EditarPontoUseCase.Resultado.Erro -> {
                        _uiState.update { it.copy(isSaving = false, erro = resultado.mensagem) }
                    }
                    is EditarPontoUseCase.Resultado.NaoEncontrado -> {
                        _uiState.update { it.copy(isSaving = false, erro = "Ponto não encontrado") }
                    }
                    is EditarPontoUseCase.Resultado.Validacao -> {
                        _uiState.update { it.copy(isSaving = false, erro = resultado.erros.joinToString("\n")) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, erro = "Erro ao salvar: ${e.message}")
                }
            }
        }
    }

    private fun confirmarExclusao() {
        val state = _uiState.value

        if (state.motivo.isBlank()) {
            _uiState.update {
                it.copy(
                    showDeleteConfirmDialog = false,
                    erro = "Informe o motivo da exclusão"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, showDeleteConfirmDialog = false) }

            val parametros = ExcluirPontoUseCase.Parametros(
                pontoId = state.pontoId,
                motivo = state.motivo
            )

            when (val resultado = excluirPontoUseCase(parametros)) {
                is ExcluirPontoUseCase.Resultado.Sucesso -> {
                    // Deletar foto ao excluir ponto
                    deletarFotoAnterior(state.pontoOriginal?.fotoComprovantePath)

                    _uiEvent.emit(EditPontoUiEvent.Excluido("Ponto excluído com sucesso"))
                    _uiEvent.emit(EditPontoUiEvent.Voltar)
                }
                is ExcluirPontoUseCase.Resultado.Erro -> {
                    _uiState.update { it.copy(isSaving = false, erro = resultado.mensagem) }
                }
                is ExcluirPontoUseCase.Resultado.NaoEncontrado -> {
                    _uiState.update { it.copy(isSaving = false, erro = "Ponto não encontrado") }
                }
                is ExcluirPontoUseCase.Resultado.Validacao -> {
                    _uiState.update { it.copy(isSaving = false, erro = resultado.erros.joinToString("\n")) }
                }
            }
        }
    }

    private fun cancelar() {
        viewModelScope.launch {
            _uiEvent.emit(EditPontoUiEvent.Voltar)
        }
    }
}
