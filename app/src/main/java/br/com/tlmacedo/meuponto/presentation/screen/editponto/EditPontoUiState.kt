// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/editponto/EditPontoUiState.kt
package br.com.tlmacedo.meuponto.presentation.screen.editponto

import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import br.com.tlmacedo.meuponto.domain.model.MotivoEdicao
import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.model.TipoNsr
import br.com.tlmacedo.meuponto.domain.model.TipoPonto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Estado da tela de edição de ponto.
 *
 * @author Thiago
 * @since 3.5.0
 */
data class EditPontoUiState(
    // Dados do ponto
    val pontoId: Long = 0,
    val pontoOriginal: Ponto? = null,
    val empregoId: Long = 0,
    val tipoPonto: TipoPonto = TipoPonto.ENTRADA,
    val indice: Int = 0,

    // Campos editáveis
    val data: LocalDate = LocalDate.now(),
    val hora: LocalTime = LocalTime.now(),
    val nsr: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val endereco: String = "",
    val observacao: String = "",

    // Motivo com dropdown
    val motivoSelecionado: MotivoEdicao = MotivoEdicao.NENHUM,
    val motivoDetalhes: String = "",
    val showMotivoDropdown: Boolean = false,

    // Configurações do emprego
    val configuracao: ConfiguracaoEmprego? = null,

    // Estados de UI
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val showTimePicker: Boolean = false,
    val showDatePicker: Boolean = false,
    val showLocationPicker: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val erro: String? = null
) {
    companion object {
        private val localeBR = Locale("pt", "BR")
        private val formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy", localeBR)
        private val formatterHora = DateTimeFormatter.ofPattern("HH:mm", localeBR)
        private val formatterDataCompleta = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", localeBR)
    }

    // ========================================================================
    // Flags de configuração
    // ========================================================================

    val habilitarNsr: Boolean
        get() = configuracao?.habilitarNsr == true

    val tipoNsr: TipoNsr
        get() = configuracao?.tipoNsr ?: TipoNsr.NUMERICO

    val habilitarLocalizacao: Boolean
        get() = configuracao?.habilitarLocalizacao == true

    val localizacaoAutomatica: Boolean
        get() = configuracao?.localizacaoAutomatica == true

    // ========================================================================
    // Formatação
    // ========================================================================

    val dataFormatada: String
        get() = data.format(formatterData)

    val horaFormatada: String
        get() = hora.format(formatterHora)

    val dataCompletaFormatada: String
        get() = data.format(formatterDataCompleta).replaceFirstChar { it.uppercase() }

    val dataHora: LocalDateTime
        get() = LocalDateTime.of(data, hora)

    val localizacaoFormatada: String
        get() = when {
            endereco.isNotBlank() -> endereco
            latitude != null && longitude != null -> "%.6f, %.6f".format(latitude, longitude)
            else -> "Não definida"
        }

    val temLocalizacao: Boolean
        get() = latitude != null && longitude != null

    // ========================================================================
    // Motivo - Lógica do dropdown
    // ========================================================================

    /**
     * Motivo final que será salvo (combinação do selecionado + detalhes).
     */
    val motivo: String
        get() = when {
            motivoSelecionado == MotivoEdicao.NENHUM -> ""
            motivoSelecionado == MotivoEdicao.OUTRO -> motivoDetalhes.trim()
            motivoSelecionado.requerDetalhes && motivoDetalhes.isNotBlank() ->
                "${motivoSelecionado.descricao}: ${motivoDetalhes.trim()}"
            else -> motivoSelecionado.descricao
        }

    /**
     * Indica se o campo de detalhes deve ser exibido.
     */
    val mostrarCampoDetalhes: Boolean
        get() = motivoSelecionado.requerDetalhes && motivoSelecionado != MotivoEdicao.NENHUM

    /**
     * Texto exibido no dropdown.
     */
    val motivoDisplayText: String
        get() = if (motivoSelecionado == MotivoEdicao.NENHUM) {
            "Selecione um motivo..."
        } else {
            motivoSelecionado.descricao
        }

    // ========================================================================
    // Validações
    // ========================================================================

    val motivoValido: Boolean
        get() = when {
            motivoSelecionado == MotivoEdicao.NENHUM -> false
            motivoSelecionado == MotivoEdicao.OUTRO -> motivoDetalhes.trim().length >= 5
            motivoSelecionado.requerDetalhes -> motivoDetalhes.trim().length >= 5
            else -> true
        }

    val nsrValido: Boolean
        get() = !habilitarNsr || nsr.isNotBlank()

    val localizacaoValida: Boolean
        get() = !habilitarLocalizacao || temLocalizacao

    val podeSalvar: Boolean
        get() = motivoValido && nsrValido && localizacaoValida && !isSaving

    val erroMotivo: String?
        get() = when {
            motivoSelecionado == MotivoEdicao.NENHUM -> "Selecione um motivo"
            motivoSelecionado == MotivoEdicao.OUTRO && motivoDetalhes.trim().length < 5 ->
                "Especifique o motivo (mín. 5 caracteres)"
            motivoSelecionado.requerDetalhes && motivoDetalhes.trim().length < 5 ->
                "Detalhe o motivo (mín. 5 caracteres)"
            else -> null
        }

    val erroNsr: String?
        get() = if (habilitarNsr && nsr.isBlank()) "NSR é obrigatório" else null

    val erroLocalizacao: String?
        get() = if (habilitarLocalizacao && !temLocalizacao) "Localização é obrigatória" else null

    // ========================================================================
    // Verificação de alterações
    // ========================================================================

    val temAlteracoes: Boolean
        get() {
            val original = pontoOriginal ?: return false
            return dataHora != original.dataHora ||
                    nsr != (original.nsr ?: "") ||
                    latitude != original.latitude ||
                    longitude != original.longitude ||
                    endereco != (original.endereco ?: "") ||
                    observacao != (original.observacao ?: "")
        }

    val alteracoesResumo: String
        get() {
            val original = pontoOriginal ?: return ""
            val alteracoes = mutableListOf<String>()

            if (dataHora != original.dataHora) {
                alteracoes.add("Horário: ${original.horaFormatada} → $horaFormatada")
            }
            if (nsr != (original.nsr ?: "")) {
                alteracoes.add("NSR: ${original.nsr ?: "(vazio)"} → ${nsr.ifBlank { "(vazio)" }}")
            }
            if (latitude != original.latitude || longitude != original.longitude) {
                alteracoes.add("Localização alterada")
            }
            if (observacao != (original.observacao ?: "")) {
                alteracoes.add("Observação alterada")
            }

            return alteracoes.joinToString("\n")
        }
}
