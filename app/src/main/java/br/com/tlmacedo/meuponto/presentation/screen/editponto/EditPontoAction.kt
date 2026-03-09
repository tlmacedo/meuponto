// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/editponto/EditPontoAction.kt
package br.com.tlmacedo.meuponto.presentation.screen.editponto

import android.net.Uri
import br.com.tlmacedo.meuponto.domain.model.MotivoEdicao
import java.time.LocalDate
import java.time.LocalTime

/**
 * Ações possíveis na tela de edição de ponto.
 *
 * @author Thiago
 * @since 3.5.0
 * @updated 9.0.0 - Adicionadas ações para foto de comprovante
 */
sealed interface EditPontoAction {

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES DE CAMPOS
    // ══════════════════════════════════════════════════════════════════════

    data class AtualizarData(val data: LocalDate) : EditPontoAction
    data class AtualizarHora(val hora: LocalTime) : EditPontoAction
    data class AtualizarNsr(val nsr: String) : EditPontoAction
    data class AtualizarLocalizacao(
        val latitude: Double,
        val longitude: Double,
        val endereco: String? = null
    ) : EditPontoAction
    data class AtualizarObservacao(val observacao: String) : EditPontoAction
    data class SelecionarMotivo(val motivo: MotivoEdicao) : EditPontoAction
    data class AtualizarMotivoDetalhes(val detalhes: String) : EditPontoAction
    data object AbrirMotivoDropdown : EditPontoAction
    data object FecharMotivoDropdown : EditPontoAction

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES DE FOTO DE COMPROVANTE
    // ══════════════════════════════════════════════════════════════════════

    /** Seleciona uma nova foto de comprovante */
    data class SelecionarFotoComprovante(val uri: Uri) : EditPontoAction

    /** Remove a foto de comprovante */
    data object RemoverFotoComprovante : EditPontoAction

    /** Abre o visualizador de foto em tela cheia */
    data object AbrirVisualizadorFoto : EditPontoAction

    /** Fecha o visualizador de foto */
    data object FecharVisualizadorFoto : EditPontoAction

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES DE DIALOGS
    // ══════════════════════════════════════════════════════════════════════

    data object AbrirTimePicker : EditPontoAction
    data object FecharTimePicker : EditPontoAction
    data object AbrirDatePicker : EditPontoAction
    data object FecharDatePicker : EditPontoAction
    data object AbrirLocationPicker : EditPontoAction
    data object FecharLocationPicker : EditPontoAction
    data object CapturarLocalizacao : EditPontoAction
    data object LimparLocalizacao : EditPontoAction

    // ══════════════════════════════════════════════════════════════════════
    // AÇÕES PRINCIPAIS
    // ══════════════════════════════════════════════════════════════════════

    data object Salvar : EditPontoAction
    data object SolicitarExclusao : EditPontoAction
    data object ConfirmarExclusao : EditPontoAction
    data object CancelarExclusao : EditPontoAction
    data object Cancelar : EditPontoAction
    data object LimparErro : EditPontoAction
}
