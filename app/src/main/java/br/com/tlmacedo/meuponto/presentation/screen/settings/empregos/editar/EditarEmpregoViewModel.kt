// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/empregos/editar/EditarEmpregoViewModel.kt
package br.com.tlmacedo.meuponto.presentation.screen.settings.empregos.editar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import br.com.tlmacedo.meuponto.domain.model.DiaSemana
import br.com.tlmacedo.meuponto.domain.model.Emprego
import br.com.tlmacedo.meuponto.domain.model.TipoNsr
import br.com.tlmacedo.meuponto.domain.repository.ConfiguracaoEmpregoRepository
import br.com.tlmacedo.meuponto.domain.repository.EmpregoRepository
import br.com.tlmacedo.meuponto.domain.usecase.emprego.ObterEmpregoComConfiguracaoUseCase
import br.com.tlmacedo.meuponto.domain.usecase.validacao.ValidarEmpregoUseCase
import br.com.tlmacedo.meuponto.presentation.navigation.MeuPontoDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel da tela de edição/criação de emprego.
 *
 * @author Thiago
 * @since 2.0.0
 * @updated 2.3.3 - Adicionado suporte a data de início e último fechamento.
 */
@HiltViewModel
class EditarEmpregoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val empregoRepository: EmpregoRepository,
    private val configuracaoEmpregoRepository: ConfiguracaoEmpregoRepository,
    private val obterEmpregoComConfiguracaoUseCase: ObterEmpregoComConfiguracaoUseCase,
    private val validarEmpregoUseCase: ValidarEmpregoUseCase
) : ViewModel() {

    private val empregoId: Long = savedStateHandle.get<Long>(MeuPontoDestinations.ARG_EMPREGO_ID) ?: -1L

    private val _uiState = MutableStateFlow(EditarEmpregoUiState())
    val uiState: StateFlow<EditarEmpregoUiState> = _uiState.asStateFlow()

    private val _eventos = MutableSharedFlow<EditarEmpregoEvent>()
    val eventos: SharedFlow<EditarEmpregoEvent> = _eventos.asSharedFlow()

    init {
        if (empregoId > 0) {
            carregarEmprego(empregoId)
        } else {
            _uiState.update { it.copy(isNovoEmprego = true, isLoading = false) }
        }
    }

    fun onAction(action: EditarEmpregoAction) {
        when (action) {
            is EditarEmpregoAction.AlterarNome -> alterarNome(action.nome)
            is EditarEmpregoAction.AlterarDataInicioTrabalho -> alterarDataInicioTrabalho(action.data)
            is EditarEmpregoAction.AlterarCargaHorariaDiaria -> alterarCargaHorariaDiaria(action.duracao)
            is EditarEmpregoAction.AlterarJornadaMaximaDiaria -> alterarJornadaMaximaDiaria(action.minutos)
            is EditarEmpregoAction.AlterarIntervaloMinimo -> alterarIntervaloMinimo(action.minutos)
            is EditarEmpregoAction.AlterarIntervaloInterjornada -> alterarIntervaloInterjornada(action.minutos)
            is EditarEmpregoAction.AlterarToleranciaIntervaloMais -> alterarToleranciaIntervaloMais(action.minutos)
            is EditarEmpregoAction.AlterarHabilitarNsr -> alterarHabilitarNsr(action.habilitado)
            is EditarEmpregoAction.AlterarTipoNsr -> alterarTipoNsr(action.tipo)
            is EditarEmpregoAction.AlterarHabilitarLocalizacao -> alterarHabilitarLocalizacao(action.habilitado)
            is EditarEmpregoAction.AlterarLocalizacaoAutomatica -> alterarLocalizacaoAutomatica(action.automatica)
            is EditarEmpregoAction.AlterarExigeJustificativa -> alterarExigeJustificativa(action.exigir)
            is EditarEmpregoAction.AlterarPrimeiroDiaSemana -> alterarPrimeiroDiaSemana(action.dia)
            is EditarEmpregoAction.AlterarPrimeiroDiaMes -> alterarPrimeiroDiaMes(action.dia)
            is EditarEmpregoAction.AlterarPeriodoBancoHoras -> alterarPeriodoBancoHoras(action.valor)
            is EditarEmpregoAction.AlterarZerarSaldoMensal -> alterarZerarSaldoMensal(action.zerar)
            is EditarEmpregoAction.AlterarZerarBancoAntesPeriodo -> alterarZerarBancoAntesPeriodo(action.zerar)
            is EditarEmpregoAction.AlterarUltimoFechamentoBanco -> alterarUltimoFechamentoBanco(action.data)
            is EditarEmpregoAction.ToggleSecao -> toggleSecao(action.secao)
            is EditarEmpregoAction.Salvar -> salvar()
            is EditarEmpregoAction.Cancelar -> cancelar()
            is EditarEmpregoAction.LimparErro -> limparErro()
        }
    }

    private fun carregarEmprego(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val resultado = obterEmpregoComConfiguracaoUseCase(id)) {
                is ObterEmpregoComConfiguracaoUseCase.Resultado.Sucesso -> {
                    val emprego = resultado.emprego
                    val config = resultado.configuracao

                    _uiState.update {
                        it.copy(
                            empregoId = emprego.id,
                            isNovoEmprego = false,
                            nome = emprego.nome,
                            dataInicioTrabalho = emprego.dataInicioTrabalho,
                            cargaHorariaDiaria = Duration.ofMinutes(config.cargaHorariaDiariaMinutos.toLong()),
                            jornadaMaximaDiariaMinutos = config.jornadaMaximaDiariaMinutos,
                            intervaloMinimoMinutos = config.intervaloMinimoMinutos,
                            intervaloInterjornadaMinutos = config.intervaloMinimoInterjornadaMinutos,
                            toleranciaIntervaloMaisMinutos = config.toleranciaIntervaloMaisMinutos,
                            habilitarNsr = config.habilitarNsr,
                            tipoNsr = config.tipoNsr,
                            habilitarLocalizacao = config.habilitarLocalizacao,
                            localizacaoAutomatica = config.localizacaoAutomatica,
                            exigeJustificativaInconsistencia = config.exigeJustificativaInconsistencia,
                            primeiroDiaSemana = config.primeiroDiaSemana,
                            primeiroDiaMes = config.primeiroDiaMes,
                            periodoBancoHorasValor = config.periodoBancoHorasMeses,
                            zerarSaldoMensal = config.zerarSaldoMensal,
                            zerarBancoAntesPeriodo = config.zerarBancoAntesPeriodo,
                            ultimoFechamentoBanco = config.ultimoFechamentoBanco,
                            isLoading = false
                        )
                    }
                }
                is ObterEmpregoComConfiguracaoUseCase.Resultado.NaoEncontrado -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _eventos.emit(EditarEmpregoEvent.MostrarErro("Emprego não encontrado"))
                    _eventos.emit(EditarEmpregoEvent.Voltar)
                }
                is ObterEmpregoComConfiguracaoUseCase.Resultado.Erro -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _eventos.emit(EditarEmpregoEvent.MostrarErro(resultado.mensagem))
                }
            }
        }
    }

    private fun alterarNome(nome: String) {
        val erro = when {
            nome.isBlank() -> "Nome é obrigatório"
            nome.length < 2 -> "Nome muito curto"
            else -> null
        }
        _uiState.update { it.copy(nome = nome, nomeErro = erro) }
    }

    private fun alterarDataInicioTrabalho(data: LocalDate?) {
        _uiState.update { it.copy(dataInicioTrabalho = data, showInicioTrabalhoPicker = false) }
    }

    private fun alterarCargaHorariaDiaria(duracao: Duration) {
        _uiState.update { it.copy(cargaHorariaDiaria = duracao) }
    }

    private fun alterarJornadaMaximaDiaria(minutos: Int) {
        _uiState.update { it.copy(jornadaMaximaDiariaMinutos = minutos) }
    }

    private fun alterarIntervaloMinimo(minutos: Int) {
        _uiState.update { it.copy(intervaloMinimoMinutos = minutos) }
    }

    private fun alterarIntervaloInterjornada(minutos: Int) {
        _uiState.update { it.copy(intervaloInterjornadaMinutos = minutos) }
    }

    private fun alterarToleranciaIntervaloMais(minutos: Int) {
        _uiState.update { it.copy(toleranciaIntervaloMaisMinutos = minutos) }
    }

    private fun alterarHabilitarNsr(habilitado: Boolean) {
        _uiState.update { it.copy(habilitarNsr = habilitado) }
    }

    private fun alterarTipoNsr(tipo: TipoNsr) {
        _uiState.update { it.copy(tipoNsr = tipo) }
    }

    private fun alterarHabilitarLocalizacao(habilitado: Boolean) {
        _uiState.update {
            it.copy(
                habilitarLocalizacao = habilitado,
                localizacaoAutomatica = if (!habilitado) false else it.localizacaoAutomatica
            )
        }
    }

    private fun alterarLocalizacaoAutomatica(automatica: Boolean) {
        _uiState.update { it.copy(localizacaoAutomatica = automatica) }
    }

    private fun alterarExigeJustificativa(exigir: Boolean) {
        _uiState.update { it.copy(exigeJustificativaInconsistencia = exigir) }
    }

    private fun alterarPrimeiroDiaSemana(dia: DiaSemana) {
        _uiState.update { it.copy(primeiroDiaSemana = dia) }
    }

    private fun alterarPrimeiroDiaMes(dia: Int) {
        _uiState.update { it.copy(primeiroDiaMes = dia) }
    }

    private fun alterarPeriodoBancoHoras(valor: Int) {
        _uiState.update { it.copy(periodoBancoHorasValor = valor) }
    }

    private fun alterarZerarSaldoMensal(zerar: Boolean) {
        _uiState.update { it.copy(zerarSaldoMensal = zerar) }
    }

    private fun alterarZerarBancoAntesPeriodo(zerar: Boolean) {
        _uiState.update { it.copy(zerarBancoAntesPeriodo = zerar) }
    }

    private fun alterarUltimoFechamentoBanco(data: LocalDate?) {
        _uiState.update { it.copy(ultimoFechamentoBanco = data, showUltimoFechamentoPicker = false) }
    }

    private fun toggleSecao(secao: SecaoFormulario) {
        _uiState.update {
            it.copy(secaoExpandida = if (it.secaoExpandida == secao) SecaoFormulario.DADOS_BASICOS else secao)
        }
    }

    fun setShowInicioTrabalhoPicker(show: Boolean) {
        _uiState.update { it.copy(showInicioTrabalhoPicker = show) }
    }

    fun setShowUltimoFechamentoPicker(show: Boolean) {
        _uiState.update { it.copy(showUltimoFechamentoPicker = show) }
    }

    private fun salvar() {
        val state = _uiState.value
        if (state.nome.isBlank()) {
            _uiState.update { it.copy(nomeErro = "Nome é obrigatório") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                if (state.isNovoEmprego) {
                    criarNovoEmprego(state)
                } else {
                    atualizarEmprego(state)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
                _eventos.emit(EditarEmpregoEvent.MostrarErro("Erro ao salvar: ${e.message}"))
            }
        }
    }

    private suspend fun criarNovoEmprego(state: EditarEmpregoUiState) {
        val emprego = Emprego(
            nome = state.nome.trim(),
            dataInicioTrabalho = state.dataInicioTrabalho,
            criadoEm = LocalDateTime.now(),
            atualizadoEm = LocalDateTime.now()
        )
        val empregoId = empregoRepository.inserir(emprego)
        val configuracao = criarConfiguracao(empregoId, state)
        configuracaoEmpregoRepository.inserir(configuracao)
        _uiState.update { it.copy(isSaving = false) }
        _eventos.emit(EditarEmpregoEvent.SalvoComSucesso("Emprego criado com sucesso"))
    }

    private suspend fun atualizarEmprego(state: EditarEmpregoUiState) {
        val empregoId = state.empregoId ?: return
        val empregoExistente = empregoRepository.buscarPorId(empregoId) ?: return
        val empregoAtualizado = empregoExistente.copy(
            nome = state.nome.trim(),
            dataInicioTrabalho = state.dataInicioTrabalho,
            atualizadoEm = LocalDateTime.now()
        )
        empregoRepository.atualizar(empregoAtualizado)
        val configExistente = configuracaoEmpregoRepository.buscarPorEmpregoId(empregoId)
        val configuracao = criarConfiguracao(empregoId, state).copy(
            id = configExistente?.id ?: 0,
            criadoEm = configExistente?.criadoEm ?: LocalDateTime.now()
        )
        if (configExistente != null) {
            configuracaoEmpregoRepository.atualizar(configuracao)
        } else {
            configuracaoEmpregoRepository.inserir(configuracao)
        }
        _uiState.update { it.copy(isSaving = false) }
        _eventos.emit(EditarEmpregoEvent.SalvoComSucesso("Emprego atualizado com sucesso"))
    }

    private fun criarConfiguracao(empregoId: Long, state: EditarEmpregoUiState): ConfiguracaoEmprego {
        return ConfiguracaoEmprego(
            empregoId = empregoId,
            cargaHorariaDiariaMinutos = state.cargaHorariaDiaria.toMinutes().toInt(),
            jornadaMaximaDiariaMinutos = state.jornadaMaximaDiariaMinutos,
            intervaloMinimoInterjornadaMinutos = state.intervaloInterjornadaMinutos,
            intervaloMinimoMinutos = state.intervaloMinimoMinutos,
            toleranciaIntervaloMaisMinutos = state.toleranciaIntervaloMaisMinutos,
            exigeJustificativaInconsistencia = state.exigeJustificativaInconsistencia,
            habilitarNsr = state.habilitarNsr,
            tipoNsr = state.tipoNsr,
            habilitarLocalizacao = state.habilitarLocalizacao,
            localizacaoAutomatica = state.localizacaoAutomatica,
            primeiroDiaSemana = state.primeiroDiaSemana,
            primeiroDiaMes = state.primeiroDiaMes,
            periodoBancoHorasMeses = state.periodoBancoHorasValor,
            zerarSaldoMensal = state.zerarSaldoMensal,
            zerarBancoAntesPeriodo = state.zerarBancoAntesPeriodo,
            ultimoFechamentoBanco = state.ultimoFechamentoBanco,
            atualizadoEm = LocalDateTime.now()
        )
    }

    private fun cancelar() {
        viewModelScope.launch { _eventos.emit(EditarEmpregoEvent.Voltar) }
    }

    private fun limparErro() {
        _uiState.update { it.copy(erro = null) }
    }
}
