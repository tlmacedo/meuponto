// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/home/HomeAction.kt
package br.com.tlmacedo.meuponto.presentation.screen.home

import android.net.Uri
import br.com.tlmacedo.meuponto.domain.model.Emprego
import br.com.tlmacedo.meuponto.domain.model.Ponto
import java.time.LocalDate
import java.time.LocalTime

/**
 * Ações possíveis na tela Home.
 *
 * @author Thiago
 * @since 2.0.0
 * @updated 9.0.0 - Adicionadas ações para foto de comprovante
 */
sealed interface HomeAction {

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES DE REGISTRO DE PONTO
    // ══════════════════════════════════════════════════════════════════════

    data object RegistrarPontoAgora : HomeAction
    data object AbrirTimePickerDialog : HomeAction
    data object FecharTimePickerDialog : HomeAction
    data class RegistrarPontoManual(val hora: LocalTime) : HomeAction

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES DE NSR
    // ══════════════════════════════════════════════════════════════════════

    data class AtualizarNsr(val nsr: String) : HomeAction
    data object ConfirmarRegistroComNsr : HomeAction
    data object CancelarNsrDialog : HomeAction

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES DE FOTO DE COMPROVANTE
    // ══════════════════════════════════════════════════════════════════════

    /** Seleciona uma imagem de comprovante (da galeria ou câmera) */
    data class SelecionarFotoComprovante(val uri: Uri) : HomeAction

    /** Remove a foto de comprovante selecionada */
    data object RemoverFotoComprovante : HomeAction

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES DE EXCLUSÃO DE PONTO
    // ══════════════════════════════════════════════════════════════════════

    data class SolicitarExclusao(val ponto: Ponto) : HomeAction
    data object CancelarExclusao : HomeAction
    data class AtualizarMotivoExclusao(val motivo: String) : HomeAction
    data object ConfirmarExclusao : HomeAction

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES DE NAVEGAÇÃO POR DATA
    // ══════════════════════════════════════════════════════════════════════

    data object DiaAnterior : HomeAction
    data object ProximoDia : HomeAction
    data object IrParaHoje : HomeAction
    data class SelecionarData(val data: LocalDate) : HomeAction
    data object AbrirDatePicker : HomeAction
    data object FecharDatePicker : HomeAction

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES DE EMPREGO
    // ══════════════════════════════════════════════════════════════════════

    data object AbrirSeletorEmprego : HomeAction
    data object FecharSeletorEmprego : HomeAction
    data class SelecionarEmprego(val emprego: Emprego) : HomeAction
    data object NavegarParaNovoEmprego : HomeAction
    data object NavegarParaEditarEmprego : HomeAction
    data object AbrirMenuEmprego : HomeAction
    data object FecharMenuEmprego : HomeAction

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES DE NAVEGAÇÃO
    // ══════════════════════════════════════════════════════════════════════

    data class EditarPonto(val pontoId: Long) : HomeAction
    data object NavegarParaHistorico : HomeAction
    data object NavegarParaConfiguracoes : HomeAction

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES INTERNAS
    // ══════════════════════════════════════════════════════════════════════

    data object AtualizarHora : HomeAction
    data object LimparErro : HomeAction
    data object RecarregarDados : HomeAction

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES DE CICLO DE BANCO DE HORAS
    // ══════════════════════════════════════════════════════════════════════

    data object AbrirDialogFechamentoCiclo : HomeAction
    data object FecharDialogFechamentoCiclo : HomeAction
    data object ConfirmarFechamentoCiclo : HomeAction
    data object NavegarParaHistoricoCiclos : HomeAction
}
