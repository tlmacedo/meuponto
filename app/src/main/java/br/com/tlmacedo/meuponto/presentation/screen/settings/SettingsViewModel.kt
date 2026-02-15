// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/SettingsViewModel.kt
package br.com.tlmacedo.meuponto.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.tlmacedo.meuponto.domain.model.Emprego
import br.com.tlmacedo.meuponto.domain.usecase.emprego.ObterEmpregoAtivoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado da UI da tela de configurações.
 *
 * @property empregoAtivo Emprego atualmente selecionado pelo usuário
 * @property isLoading Indica se está carregando dados
 * @property appVersion Versão atual do aplicativo
 *
 * @author Thiago
 * @since 2.0.0
 */
data class SettingsUiState(
    val empregoAtivo: Emprego? = null,
    val isLoading: Boolean = true,
    val appVersion: String = "2.0.0-alpha"
)

/**
 * ViewModel da tela principal de configurações.
 *
 * Gerencia o estado da tela de configurações, incluindo a exibição
 * do emprego ativo atual e navegação para sub-telas.
 *
 * @property obterEmpregoAtivoUseCase Caso de uso para obter o emprego ativo
 *
 * @author Thiago
 * @since 2.0.0
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val obterEmpregoAtivoUseCase: ObterEmpregoAtivoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observarEmpregoAtivo()
    }

    /**
     * Observa mudanças no emprego ativo de forma reativa.
     */
    private fun observarEmpregoAtivo() {
        viewModelScope.launch {
            obterEmpregoAtivoUseCase.observar()
                .collect { emprego ->
                    _uiState.update { state ->
                        state.copy(
                            empregoAtivo = emprego,
                            isLoading = false
                        )
                    }
                }
        }
    }
}
