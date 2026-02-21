// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/ausencias/AusenciaFormAction.kt
package br.com.tlmacedo.meuponto.presentation.screen.ausencias

import br.com.tlmacedo.meuponto.domain.model.ausencia.TipoAusencia
import java.time.LocalDate
import java.time.LocalTime

/**
 * Ações do formulário de ausência.
 *
 * @author Thiago
 * @since 4.0.0
 * @updated 5.5.0 - Removido SubTipoFolga
 */
sealed interface AusenciaFormAction {
    // ========================================================================
    // TIPO DE AUSÊNCIA
    // ========================================================================
    data class SelecionarTipo(val tipo: TipoAusencia) : AusenciaFormAction
    data object AbrirTipoSelector : AusenciaFormAction
    data object FecharTipoSelector : AusenciaFormAction

    // ========================================================================
    // PERÍODO
    // ========================================================================
    data class SelecionarModoPeriodo(val modo: ModoPeriodo) : AusenciaFormAction
    data class SelecionarDataInicio(val data: LocalDate) : AusenciaFormAction
    data class SelecionarDataFim(val data: LocalDate) : AusenciaFormAction
    data class AtualizarQuantidadeDias(val dias: Int) : AusenciaFormAction
    data object AbrirDatePickerInicio : AusenciaFormAction
    data object FecharDatePickerInicio : AusenciaFormAction
    data object AbrirDatePickerFim : AusenciaFormAction
    data object FecharDatePickerFim : AusenciaFormAction

    // ========================================================================
    // HORÁRIOS (DECLARAÇÃO)
    // ========================================================================
    data class SelecionarHoraInicio(val hora: LocalTime) : AusenciaFormAction
    data class AtualizarDuracaoDeclaracao(val horas: Int, val minutos: Int) : AusenciaFormAction
    data class AtualizarDuracaoAbono(val horas: Int, val minutos: Int) : AusenciaFormAction
    data object AbrirTimePickerInicio : AusenciaFormAction
    data object FecharTimePickerInicio : AusenciaFormAction
    data object AbrirDuracaoDeclaracaoPicker : AusenciaFormAction
    data object FecharDuracaoDeclaracaoPicker : AusenciaFormAction
    data object AbrirDuracaoAbonoPicker : AusenciaFormAction
    data object FecharDuracaoAbonoPicker : AusenciaFormAction

    // ========================================================================
    // TEXTOS
    // ========================================================================
    data class AtualizarDescricao(val descricao: String) : AusenciaFormAction
    data class AtualizarObservacao(val observacao: String) : AusenciaFormAction
    data class AtualizarPeriodoAquisitivo(val periodo: String) : AusenciaFormAction

    // ========================================================================
    // ANEXO DE IMAGEM
    // ========================================================================
    data class SelecionarImagem(val uri: String, val nome: String?) : AusenciaFormAction
    data object RemoverImagem : AusenciaFormAction
    data object AbrirImagePicker : AusenciaFormAction
    data object FecharImagePicker : AusenciaFormAction
    data object AbrirCamera : AusenciaFormAction
    data object AbrirGaleria : AusenciaFormAction

    // ========================================================================
    // AÇÕES PRINCIPAIS
    // ========================================================================
    data object Salvar : AusenciaFormAction
    data object Cancelar : AusenciaFormAction
    data object LimparErro : AusenciaFormAction
}
