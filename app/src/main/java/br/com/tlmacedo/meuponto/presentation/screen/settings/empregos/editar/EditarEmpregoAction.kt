// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/empregos/editar/EditarEmpregoAction.kt
package br.com.tlmacedo.meuponto.presentation.screen.settings.empregos.editar

import br.com.tlmacedo.meuponto.domain.model.DiaSemana
import br.com.tlmacedo.meuponto.domain.model.TipoNsr
import java.time.Duration
import java.time.LocalDate

/**
 * Sealed class que representa as ações possíveis na tela de edição de emprego.
 */
sealed class EditarEmpregoAction {
    // Dados básicos
    data class AlterarNome(val nome: String) : EditarEmpregoAction()
    data class AlterarDataInicioTrabalho(val data: LocalDate?) : EditarEmpregoAction()

    // Jornada
    data class AlterarCargaHorariaDiaria(val duracao: Duration) : EditarEmpregoAction()
    data class AlterarJornadaMaximaDiaria(val minutos: Int) : EditarEmpregoAction()
    data class AlterarIntervaloMinimo(val minutos: Int) : EditarEmpregoAction()
    data class AlterarIntervaloInterjornada(val minutos: Int) : EditarEmpregoAction()

    // Tolerâncias
    data class AlterarToleranciaIntervaloMais(val minutos: Int) : EditarEmpregoAction()

    // NSR
    data class AlterarHabilitarNsr(val habilitado: Boolean) : EditarEmpregoAction()
    data class AlterarTipoNsr(val tipo: TipoNsr) : EditarEmpregoAction()

    // Localização
    data class AlterarHabilitarLocalizacao(val habilitado: Boolean) : EditarEmpregoAction()
    data class AlterarLocalizacaoAutomatica(val automatica: Boolean) : EditarEmpregoAction()

    // Validações
    data class AlterarExigeJustificativa(val exigir: Boolean) : EditarEmpregoAction()

    // Banco de Horas
    data class AlterarPrimeiroDiaSemana(val dia: DiaSemana) : EditarEmpregoAction()
    data class AlterarPrimeiroDiaMes(val dia: Int) : EditarEmpregoAction()
    data class AlterarPeriodoBancoHoras(val valor: Int) : EditarEmpregoAction()
    data class AlterarZerarSaldoMensal(val zerar: Boolean) : EditarEmpregoAction()
    data class AlterarZerarBancoAntesPeriodo(val zerar: Boolean) : EditarEmpregoAction()
    data class AlterarUltimoFechamentoBanco(val data: LocalDate?) : EditarEmpregoAction()

    // UI
    data class ToggleSecao(val secao: SecaoFormulario) : EditarEmpregoAction()

    // Ações principais
    data object Salvar : EditarEmpregoAction()
    data object Cancelar : EditarEmpregoAction()
    data object LimparErro : EditarEmpregoAction()
}
