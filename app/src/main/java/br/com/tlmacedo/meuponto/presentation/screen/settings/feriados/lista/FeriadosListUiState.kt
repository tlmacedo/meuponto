// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/feriados/lista/FeriadosListUiState.kt
package br.com.tlmacedo.meuponto.presentation.screen.settings.feriados.lista

import br.com.tlmacedo.meuponto.domain.model.feriado.Feriado
import br.com.tlmacedo.meuponto.domain.model.feriado.TipoFeriado

/**
 * Direção da ordenação dos feriados.
 *
 * @author Thiago
 * @since 5.3.0
 */
enum class OrdemData(val descricao: String, val emoji: String) {
    CRESCENTE("Mais próximos primeiro", "⬆️"),
    DECRESCENTE("Mais distantes primeiro", "⬇️")
}

/**
 * Estado da UI da tela de listagem de feriados.
 *
 * @author Thiago
 * @since 3.0.0
 * @updated 5.3.0 - Adicionado suporte a múltiplos tipos e ordenação
 */
data class FeriadosListUiState(
    val isLoading: Boolean = true,
    val feriados: List<Feriado> = emptyList(),
    val feriadosFiltrados: List<Feriado> = emptyList(),

    /** Filtro por múltiplos tipos (set vazio = todos) */
    val filtroTipos: Set<TipoFeriado> = emptySet(),

    val filtroAno: Int? = null,
    val anosDisponiveis: List<Int> = emptyList(),
    val searchQuery: String = "",

    /** Direção da ordenação por data */
    val ordemData: OrdemData = OrdemData.CRESCENTE,

    val showImportDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val feriadoParaExcluir: Feriado? = null,
    val importacaoEmAndamento: Boolean = false,
    val mensagemSucesso: String? = null,
    val mensagemErro: String? = null
) {
    val feriadosAgrupados: Map<TipoFeriado, List<Feriado>>
        get() = feriadosFiltrados.groupBy { it.tipo }

    val totalFeriados: Int
        get() = feriados.size

    val totalFeriadosFiltrados: Int
        get() = feriadosFiltrados.size

    val temFiltrosAtivos: Boolean
        get() = filtroTipos.isNotEmpty() || filtroAno != null || searchQuery.isNotBlank()

    /** Quantidade de filtros de tipo ativos */
    val quantidadeTiposSelecionados: Int
        get() = filtroTipos.size
}

/**
 * Eventos da tela de listagem de feriados.
 */
sealed class FeriadosListEvent {
    data class OnSearchQueryChange(val query: String) : FeriadosListEvent()

    /** Toggle de um tipo específico (adiciona/remove do set) */
    data class OnToggleTipo(val tipo: TipoFeriado) : FeriadosListEvent()

    data class OnFiltroAnoChange(val ano: Int?) : FeriadosListEvent()

    /** Alterna a direção da ordenação */
    data object OnToggleOrdem : FeriadosListEvent()

    data object OnLimparFiltros : FeriadosListEvent()
    data object OnShowImportDialog : FeriadosListEvent()
    data object OnDismissImportDialog : FeriadosListEvent()
    data object OnImportarFeriados : FeriadosListEvent()
    data class OnShowDeleteDialog(val feriado: Feriado) : FeriadosListEvent()
    data object OnDismissDeleteDialog : FeriadosListEvent()
    data object OnConfirmarExclusao : FeriadosListEvent()
    data class OnToggleAtivo(val feriado: Feriado) : FeriadosListEvent()
    data object OnDismissMessage : FeriadosListEvent()
}
