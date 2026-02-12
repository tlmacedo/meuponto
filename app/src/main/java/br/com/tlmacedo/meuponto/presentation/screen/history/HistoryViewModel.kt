// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/history/HistoryViewModel.kt
package br.com.tlmacedo.meuponto.presentation.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoJornada
import br.com.tlmacedo.meuponto.domain.model.RegistroDiario
import br.com.tlmacedo.meuponto.domain.repository.PontoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel da tela de Histórico.
 *
 * Gerencia o estado da tela de histórico, carregando os registros
 * de ponto do mês selecionado e agrupando-os por dia.
 *
 * @property repository Repositório de pontos para busca dos dados
 *
 * @author Thiago
 * @since 1.0.0
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: PontoRepository
) : ViewModel() {

    // Estado da UI
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    // Configuração de jornada
    private val configuracao = ConfiguracaoJornada.default()

    init {
        carregarHistorico()
    }

    /**
     * Carrega o histórico do mês selecionado.
     */
    fun carregarHistorico() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val mesSelecionado = _uiState.value.mesSelecionado
                val primeiroDia = mesSelecionado.withDayOfMonth(1)
                val ultimoDia = mesSelecionado.withDayOfMonth(mesSelecionado.lengthOfMonth())

                // Observa pontos do período
                repository.observarPontosPorPeriodo(primeiroDia, ultimoDia)
                    .collect { pontos ->
                        // Agrupa pontos por dia
                        val registrosPorDia = pontos
                            .groupBy { it.data }
                            .map { (data, pontosData) ->
                                RegistroDiario(
                                    data = data,
                                    pontos = pontosData,
                                    cargaHorariaDiariaMinutos = configuracao.cargaHorariaDiariaMinutos,
                                    intervaloMinimoMinutos = configuracao.intervaloMinimoMinutos,
                                    toleranciaMinutos = configuracao.toleranciaMinutos,
                                    jornadaMaximaMinutos = configuracao.jornadaMaximaDiariaMinutos
                                )
                            }
                            .sortedByDescending { it.data }

                        _uiState.update { state ->
                            state.copy(
                                registrosPorDia = registrosPorDia,
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Erro ao carregar histórico")
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }

    /**
     * Altera o mês selecionado para visualização.
     *
     * @param novoMes Nova data representando o mês
     */
    fun selecionarMes(novoMes: LocalDate) {
        _uiState.update { it.copy(mesSelecionado = novoMes) }
        carregarHistorico()
    }

    /**
     * Navega para o mês anterior.
     */
    fun mesAnterior() {
        val novoMes = _uiState.value.mesSelecionado.minusMonths(1)
        selecionarMes(novoMes)
    }

    /**
     * Navega para o próximo mês.
     */
    fun proximoMes() {
        val novoMes = _uiState.value.mesSelecionado.plusMonths(1)
        // Não permite navegar para meses futuros
        if (!novoMes.isAfter(LocalDate.now())) {
            selecionarMes(novoMes)
        }
    }
}
