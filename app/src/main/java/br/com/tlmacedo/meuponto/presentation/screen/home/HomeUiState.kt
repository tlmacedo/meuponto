// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/home/HomeUiState.kt
package br.com.tlmacedo.meuponto.presentation.screen.home

import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.model.SaldoHoras
import br.com.tlmacedo.meuponto.domain.model.StatusDia
import br.com.tlmacedo.meuponto.domain.model.TipoPonto
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Estado da interface da tela Home.
 *
 * Contém todos os dados necessários para renderizar a tela inicial,
 * incluindo pontos do dia, saldo, status e estado de carregamento.
 *
 * @property dataAtual Data atualmente selecionada
 * @property horaAtual Hora atual para exibição do relógio
 * @property pontosHoje Lista de pontos registrados no dia
 * @property proximoTipo Próximo tipo de ponto esperado
 * @property horasTrabalhadas Total de minutos trabalhados no dia
 * @property saldoDia Saldo de horas do dia
 * @property statusDia Status de consistência dos registros
 * @property isLoading Indica se está carregando dados
 * @property isRegistrando Indica se está registrando um ponto
 * @property errorMessage Mensagem de erro para exibição
 *
 * @author Thiago
 * @since 1.0.0
 */
data class HomeUiState(
    val dataAtual: LocalDate = LocalDate.now(),
    val horaAtual: LocalDateTime = LocalDateTime.now(),
    val pontosHoje: List<Ponto> = emptyList(),
    val proximoTipo: TipoPonto = TipoPonto.ENTRADA,
    val horasTrabalhadas: Int? = null,
    val saldoDia: SaldoHoras? = null,
    val statusDia: StatusDia = StatusDia.SEM_REGISTRO,
    val isLoading: Boolean = false,
    val isRegistrando: Boolean = false,
    val errorMessage: String? = null
) {
    /**
     * Verifica se o botão de registro deve estar habilitado.
     */
    val canRegisterPonto: Boolean
        get() = !isRegistrando && !isLoading && pontosHoje.size < TipoPonto.MAX_PONTOS

    /**
     * Quantidade de pontos registrados no dia.
     */
    val quantidadePontos: Int
        get() = pontosHoje.size
}
