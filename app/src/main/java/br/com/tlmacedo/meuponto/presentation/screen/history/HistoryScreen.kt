// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/history/HistoryScreen.kt
package br.com.tlmacedo.meuponto.presentation.screen.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.tlmacedo.meuponto.domain.model.RegistroDiario
import br.com.tlmacedo.meuponto.presentation.components.EmptyState
import br.com.tlmacedo.meuponto.presentation.components.LoadingIndicator
import br.com.tlmacedo.meuponto.presentation.components.MeuPontoTopBar
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Tela de histórico de pontos.
 *
 * Exibe os registros de ponto agrupados por dia, permitindo
 * navegação entre os meses.
 *
 * @param onNavigateBack Callback para voltar à tela anterior
 * @param onNavigateToEditPonto Callback para navegar à edição de ponto
 * @param modifier Modificador opcional para customização do layout
 * @param viewModel ViewModel da tela (injetado pelo Hilt)
 *
 * @author Thiago
 * @since 1.0.0
 */
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditPonto: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MeuPontoTopBar(
                title = "Histórico",
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Seletor de mês
            MonthSelector(
                mesSelecionado = uiState.mesSelecionado,
                onMesAnterior = viewModel::mesAnterior,
                onProximoMes = viewModel::proximoMes
            )

            // Conteúdo
            when {
                uiState.isLoading -> {
                    LoadingIndicator()
                }
                !uiState.hasRegistros -> {
                    EmptyState(
                        title = "Nenhum registro",
                        message = "Não há registros de ponto neste mês.",
                        icon = Icons.Outlined.DateRange
                    )
                }
                else -> {
                    HistoryList(
                        registros = uiState.registrosPorDia,
                        onPontoClick = onNavigateToEditPonto
                    )
                }
            }
        }
    }
}

/**
 * Seletor de mês com navegação.
 */
@Composable
private fun MonthSelector(
    mesSelecionado: java.time.LocalDate,
    onMesAnterior: () -> Unit,
    onProximoMes: () -> Unit
) {
    val formatador = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale("pt", "BR"))
    val mesFormatado = mesSelecionado.format(formatador).replaceFirstChar { it.uppercase() }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        IconButton(onClick = onMesAnterior) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Mês anterior"
            )
        }

        Text(
            text = mesFormatado,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onProximoMes) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Próximo mês"
            )
        }
    }
}

/**
 * Lista de registros diários.
 */
@Composable
private fun HistoryList(
    registros: List<RegistroDiario>,
    onPontoClick: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = registros,
            key = { it.data.toString() }
        ) { registro ->
            DayHistoryCard(
                registro = registro,
                onPontoClick = onPontoClick
            )
        }
    }
}

/**
 * Card de histórico de um dia.
 */
@Composable
private fun DayHistoryCard(
    registro: RegistroDiario,
    onPontoClick: (Long) -> Unit
) {
    val formatadorData = DateTimeFormatter.ofPattern("EEEE, dd", Locale("pt", "BR"))
    val dataFormatada = registro.data.format(formatadorData).replaceFirstChar { it.uppercase() }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Cabeçalho com data e status
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dataFormatada,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = registro.determinarStatus().descricao,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (registro.determinarStatus().isConsistente) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }

            // Lista de pontos do dia
            registro.pontosOrdenados.forEach { ponto ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = ponto.tipo.descricao,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = ponto.horaFormatada,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Resumo do dia
            registro.calcularMinutosTrabalhados()?.let { minutos ->
                val horas = minutos / 60
                val mins = minutos % 60
                Text(
                    text = "Total: ${String.format("%02d:%02d", horas, mins)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
