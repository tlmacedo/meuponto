// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/history/HistoryUiState.kt
package br.com.tlmacedo.meuponto.presentation.screen.history

import br.com.tlmacedo.meuponto.domain.model.RegistroDiario
import java.time.LocalDate

/**
 * Estado da interface da tela de Histórico.
 *
 * Contém os dados necessários para renderizar a lista de registros
 * de pontos agrupados por dia.
 *
 * @property registrosPorDia Lista de registros diários ordenados por data
 * @property mesSelecionado Mês atualmente selecionado para filtro
 * @property isLoading Indica se está carregando dados
 * @property errorMessage Mensagem de erro para exibição
 *
 * @author Thiago
 * @since 1.0.0
 */
data class HistoryUiState(
    val registrosPorDia: List<RegistroDiario> = emptyList(),
    val mesSelecionado: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    /**
     * Verifica se há registros para exibir.
     */
    val hasRegistros: Boolean
        get() = registrosPorDia.isNotEmpty()

    /**
     * Retorna o total de dias com registro no período.
     */
    val totalDiasComRegistro: Int
        get() = registrosPorDia.size
}
