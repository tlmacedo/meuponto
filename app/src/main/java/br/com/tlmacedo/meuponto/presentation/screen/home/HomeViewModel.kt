// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/home/HomeViewModel.kt
package br.com.tlmacedo.meuponto.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoJornada
import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.model.RegistroDiario
import br.com.tlmacedo.meuponto.domain.model.SaldoHoras
import br.com.tlmacedo.meuponto.domain.model.TipoPonto
import br.com.tlmacedo.meuponto.domain.usecase.ponto.DeletarPontoUseCase
import br.com.tlmacedo.meuponto.domain.usecase.ponto.ObterPontosDoDiaUseCase
import br.com.tlmacedo.meuponto.domain.usecase.ponto.RegistrarPontoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel da tela Home.
 *
 * Gerencia o estado da tela inicial, incluindo registro de pontos,
 * cálculo de horas trabalhadas e atualização do relógio em tempo real.
 *
 * @property registrarPontoUseCase Caso de uso para registrar pontos
 * @property obterPontosDoDiaUseCase Caso de uso para obter pontos do dia
 * @property deletarPontoUseCase Caso de uso para deletar pontos
 *
 * @author Thiago
 * @since 1.0.0
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val registrarPontoUseCase: RegistrarPontoUseCase,
    private val obterPontosDoDiaUseCase: ObterPontosDoDiaUseCase,
    private val deletarPontoUseCase: DeletarPontoUseCase
) : ViewModel() {

    // Estado da UI
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Eventos únicos
    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent: SharedFlow<HomeUiEvent> = _uiEvent.asSharedFlow()

    // Job do relógio
    private var clockJob: Job? = null

    // Configuração de jornada
    private val configuracao = ConfiguracaoJornada.default()

    init {
        observarPontosHoje()
        iniciarRelogio()
    }

    /**
     * Processa as ações da tela.
     *
     * @param action Ação disparada pelo usuário
     */
    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.RegistrarPonto -> registrarPonto()
            is HomeAction.ExcluirPonto -> excluirPonto(action.ponto)
            is HomeAction.EditarPonto -> navegarParaEdicao(action.pontoId)
            is HomeAction.AtualizarRelogio -> atualizarRelogio()
            is HomeAction.LimparErro -> limparErro()
            is HomeAction.Recarregar -> recarregar()
        }
    }

    /**
     * Observa os pontos do dia atual de forma reativa.
     */
    private fun observarPontosHoje() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            obterPontosDoDiaUseCase(LocalDate.now()).collect { pontos ->
                Timber.d("Pontos do dia atualizados: ${pontos.size}")
                atualizarEstadoComPontos(pontos)
            }
        }
    }

    /**
     * Atualiza o estado da UI com base nos pontos recebidos.
     */
    private fun atualizarEstadoComPontos(pontos: List<Ponto>) {
        val registroDiario = RegistroDiario(
            data = LocalDate.now(),
            pontos = pontos,
            cargaHorariaDiariaMinutos = configuracao.cargaHorariaDiariaMinutos,
            intervaloMinimoMinutos = configuracao.intervaloMinimoMinutos,
            toleranciaMinutos = configuracao.toleranciaMinutos,
            jornadaMaximaMinutos = configuracao.jornadaMaximaDiariaMinutos
        )

        val horasTrabalhadas = registroDiario.calcularMinutosTrabalhados()
        val saldoMinutos = registroDiario.calcularSaldoMinutos()
        val saldo = saldoMinutos?.let { SaldoHoras(it) }
        val status = registroDiario.determinarStatus()
        val proximoTipo = registroDiario.proximoPontoEsperado

        _uiState.update { state ->
            state.copy(
                pontosHoje = pontos.sortedBy { it.dataHora },
                proximoTipo = proximoTipo,
                horasTrabalhadas = horasTrabalhadas,
                saldoDia = saldo,
                statusDia = status,
                isLoading = false
            )
        }
    }

    /**
     * Inicia o relógio que atualiza a cada segundo.
     */
    private fun iniciarRelogio() {
        clockJob?.cancel()
        clockJob = viewModelScope.launch {
            while (true) {
                _uiState.update { it.copy(horaAtual = LocalDateTime.now()) }
                delay(1000L)
            }
        }
    }

    /**
     * Atualiza o horário atual manualmente.
     */
    private fun atualizarRelogio() {
        _uiState.update { it.copy(horaAtual = LocalDateTime.now()) }
    }

    /**
     * Registra um novo ponto.
     */
    private fun registrarPonto() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRegistrando = true) }

            val resultado = registrarPontoUseCase()

            resultado.fold(
                onSuccess = { ponto ->
                    Timber.d("Ponto registrado: $ponto")
                    _uiEvent.emit(
                        HomeUiEvent.PontoRegistrado(
                            "Ponto de ${ponto.tipo.descricao} registrado às ${ponto.horaFormatada}"
                        )
                    )
                },
                onFailure = { erro ->
                    Timber.e(erro, "Erro ao registrar ponto")
                    _uiState.update { it.copy(errorMessage = erro.message) }
                }
            )

            _uiState.update { it.copy(isRegistrando = false) }
        }
    }

    /**
     * Exclui um ponto existente.
     */
    private fun excluirPonto(ponto: Ponto) {
        viewModelScope.launch {
            val resultado = deletarPontoUseCase(ponto)

            resultado.fold(
                onSuccess = {
                    Timber.d("Ponto excluído: ${ponto.id}")
                    _uiEvent.emit(HomeUiEvent.PontoExcluido)
                    _uiEvent.emit(HomeUiEvent.ShowSnackbar("Ponto excluído com sucesso"))
                },
                onFailure = { erro ->
                    Timber.e(erro, "Erro ao excluir ponto")
                    _uiState.update { it.copy(errorMessage = erro.message) }
                }
            )
        }
    }

    /**
     * Navega para a tela de edição de ponto.
     */
    private fun navegarParaEdicao(pontoId: Long) {
        viewModelScope.launch {
            _uiEvent.emit(HomeUiEvent.NavigateToEditPonto(pontoId))
        }
    }

    /**
     * Limpa a mensagem de erro.
     */
    private fun limparErro() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Recarrega os dados da tela.
     */
    private fun recarregar() {
        observarPontosHoje()
    }

    override fun onCleared() {
        super.onCleared()
        clockJob?.cancel()
    }
}
