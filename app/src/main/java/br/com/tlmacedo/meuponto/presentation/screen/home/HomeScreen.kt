// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/home/HomeScreen.kt
package br.com.tlmacedo.meuponto.presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.tlmacedo.meuponto.presentation.components.DateTimeDisplay
import br.com.tlmacedo.meuponto.presentation.components.LoadingIndicator
import br.com.tlmacedo.meuponto.presentation.components.MeuPontoTopBar
import br.com.tlmacedo.meuponto.presentation.components.PontoButton
import br.com.tlmacedo.meuponto.presentation.components.PontoCard
import br.com.tlmacedo.meuponto.presentation.components.SummaryCard
import kotlinx.coroutines.flow.collectLatest

/**
 * Tela inicial do aplicativo.
 *
 * Exibe o relógio, botão de registro de ponto, resumo do dia
 * e lista de pontos registrados.
 *
 * @param onNavigateToHistory Callback para navegar ao histórico
 * @param onNavigateToEditPonto Callback para navegar à edição de ponto
 * @param modifier Modificador opcional para customização do layout
 * @param viewModel ViewModel da tela (injetado pelo Hilt)
 *
 * @author Thiago
 * @since 1.0.0
 */
@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToEditPonto: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observa eventos únicos
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is HomeUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is HomeUiEvent.NavigateToEditPonto -> {
                    onNavigateToEditPonto(event.pontoId)
                }
                is HomeUiEvent.NavigateToHistory -> {
                    onNavigateToHistory()
                }
                is HomeUiEvent.PontoRegistrado -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is HomeUiEvent.PontoExcluido -> {
                    // Já tratado com ShowSnackbar
                }
            }
        }
    }

    Scaffold(
        topBar = {
            MeuPontoTopBar(title = "Meu Ponto")
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            HomeContent(
                uiState = uiState,
                onAction = viewModel::onAction,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

/**
 * Conteúdo principal da tela Home.
 *
 * @param uiState Estado atual da UI
 * @param onAction Callback para processar ações
 * @param modifier Modificador opcional
 */
@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // Relógio
        item {
            DateTimeDisplay(
                dataHora = uiState.horaAtual
            )
        }

        // Botão de registro
        item {
            Spacer(modifier = Modifier.height(16.dp))

            PontoButton(
                proximoTipo = uiState.proximoTipo,
                onClick = { onAction(HomeAction.RegistrarPonto) },
                enabled = uiState.canRegisterPonto
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Card de resumo
        item {
            SummaryCard(
                horasTrabalhadas = uiState.horasTrabalhadas,
                saldo = uiState.saldoDia,
                statusDia = uiState.statusDia
            )
        }

        // Lista de pontos do dia
        if (uiState.pontosHoje.isNotEmpty()) {
            items(
                items = uiState.pontosHoje,
                key = { it.id }
            ) { ponto ->
                PontoCard(
                    ponto = ponto,
                    onEditClick = { onAction(HomeAction.EditarPonto(ponto.id)) },
                    onDeleteClick = { onAction(HomeAction.ExcluirPonto(ponto)) }
                )
            }
        }
    }
}
