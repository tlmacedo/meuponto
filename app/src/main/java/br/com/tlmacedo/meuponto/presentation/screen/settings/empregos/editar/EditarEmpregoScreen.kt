// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/empregos/editar/EditarEmpregoScreen.kt
package br.com.tlmacedo.meuponto.presentation.screen.settings.empregos.editar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.tlmacedo.meuponto.domain.model.DiaSemana
import br.com.tlmacedo.meuponto.domain.model.TipoNsr
import br.com.tlmacedo.meuponto.presentation.components.MeuPontoTopBar
import kotlinx.coroutines.flow.collectLatest
import java.time.Duration

/**
 * Tela de edição/criação de emprego.
 *
 * Formulário completo para configurar um emprego com suas
 * configurações de jornada, tolerâncias, NSR e banco de horas.
 *
 * @param onNavigateBack Callback para voltar à tela anterior
 * @param modifier Modificador opcional
 * @param viewModel ViewModel da tela
 *
 * @author Thiago
 * @since 2.0.0
 */
@Composable
fun EditarEmpregoScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditarEmpregoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Coleta eventos do ViewModel
    LaunchedEffect(Unit) {
        viewModel.eventos.collectLatest { evento ->
            when (evento) {
                is EditarEmpregoEvent.SalvoComSucesso -> {
                    snackbarHostState.showSnackbar(evento.mensagem)
                    onNavigateBack()
                }
                is EditarEmpregoEvent.MostrarErro -> {
                    snackbarHostState.showSnackbar(evento.mensagem)
                }
                is EditarEmpregoEvent.Voltar -> {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            MeuPontoTopBar(
                title = uiState.tituloTela,
                showBackButton = true,
                onBackClick = { viewModel.onAction(EditarEmpregoAction.Cancelar) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        if (uiState.isLoading) {
            // Estado de carregamento
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                CircularProgressIndicator()
            }
        } else {
            EditarEmpregoContent(
                uiState = uiState,
                onAction = viewModel::onAction,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

/**
 * Conteúdo do formulário de edição de emprego.
 */
@Composable
private fun EditarEmpregoContent(
    uiState: EditarEmpregoUiState,
    onAction: (EditarEmpregoAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // Seção: Dados Básicos
        item {
            FormSection(
                title = "Dados Básicos",
                icon = Icons.Default.Business,
                isExpanded = uiState.secaoExpandida == SecaoFormulario.DADOS_BASICOS,
                onToggle = { onAction(EditarEmpregoAction.ToggleSecao(SecaoFormulario.DADOS_BASICOS)) }
            ) {
                OutlinedTextField(
                    value = uiState.nome,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarNome(it)) },
                    label = { Text("Nome do Emprego") },
                    placeholder = { Text("Ex: Empresa ABC") },
                    isError = uiState.nomeErro != null,
                    supportingText = uiState.nomeErro?.let { { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Seção: Jornada de Trabalho
        item {
            FormSection(
                title = "Jornada de Trabalho",
                icon = Icons.Default.Schedule,
                isExpanded = uiState.secaoExpandida == SecaoFormulario.JORNADA,
                onToggle = { onAction(EditarEmpregoAction.ToggleSecao(SecaoFormulario.JORNADA)) }
            ) {
                // Carga horária diária
                DurationSlider(
                    label = "Carga Horária Diária",
                    value = uiState.cargaHorariaDiaria,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarCargaHorariaDiaria(it)) },
                    minHours = 1,
                    maxHours = 12,
                    stepMinutes = 30
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Jornada máxima diária
                MinutesSlider(
                    label = "Jornada Máxima Diária",
                    value = uiState.jornadaMaximaDiariaMinutos,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarJornadaMaximaDiaria(it)) },
                    minMinutes = 60,
                    maxMinutes = 720,
                    step = 30,
                    formatAsHours = true,
                    helperText = "Limite CLT: 10 horas"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Intervalo mínimo obrigatório
                MinutesSlider(
                    label = "Intervalo Mínimo",
                    value = uiState.intervaloMinimoMinutos,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarIntervaloMinimo(it)) },
                    minMinutes = 0,
                    maxMinutes = 120,
                    step = 15,
                    formatAsHours = false,
                    helperText = "Mínimo CLT: 1 hora para jornadas > 6h"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Intervalo interjornada
                MinutesSlider(
                    label = "Intervalo Entre Jornadas",
                    value = uiState.intervaloInterjornadaMinutos,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarIntervaloInterjornada(it)) },
                    minMinutes = 0,
                    maxMinutes = 720,
                    step = 30,
                    formatAsHours = true,
                    helperText = "Mínimo CLT: 11 horas"
                )
            }
        }

        // Seção: Tolerâncias
        item {
            FormSection(
                title = "Tolerâncias",
                icon = Icons.Default.Timer,
                isExpanded = uiState.secaoExpandida == SecaoFormulario.TOLERANCIAS,
                onToggle = { onAction(EditarEmpregoAction.ToggleSecao(SecaoFormulario.TOLERANCIAS)) }
            ) {
                // Tolerância de entrada
                MinutesSlider(
                    label = "Tolerância de Entrada",
                    value = uiState.toleranciaEntradaMinutos,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarToleranciaEntrada(it)) },
                    minMinutes = 0,
                    maxMinutes = 30,
                    step = 5,
                    formatAsHours = false,
                    helperText = "Margem para atrasos na entrada"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tolerância de saída
                MinutesSlider(
                    label = "Tolerância de Saída",
                    value = uiState.toleranciaSaidaMinutos,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarToleranciaSaida(it)) },
                    minMinutes = 0,
                    maxMinutes = 30,
                    step = 5,
                    formatAsHours = false,
                    helperText = "Margem para saídas antecipadas"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tolerância de intervalo
                MinutesSlider(
                    label = "Tolerância de Intervalo",
                    value = uiState.toleranciaIntervaloMinutos,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarToleranciaIntervalo(it)) },
                    minMinutes = 0,
                    maxMinutes = 15,
                    step = 1,
                    formatAsHours = false,
                    helperText = "Margem para retorno do intervalo"
                )
            }
        }

        // Seção: NSR e Localização
        item {
            FormSection(
                title = "NSR e Localização",
                icon = Icons.Default.LocationOn,
                isExpanded = uiState.secaoExpandida == SecaoFormulario.NSR_LOCALIZACAO,
                onToggle = { onAction(EditarEmpregoAction.ToggleSecao(SecaoFormulario.NSR_LOCALIZACAO)) }
            ) {
                // NSR
                SwitchOption(
                    title = "Habilitar NSR",
                    description = "Número Sequencial de Registro",
                    checked = uiState.habilitarNsr,
                    onCheckedChange = { onAction(EditarEmpregoAction.AlterarHabilitarNsr(it)) }
                )

                AnimatedVisibility(visible = uiState.habilitarNsr) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        TipoNsrSelector(
                            selected = uiState.tipoNsr,
                            onSelect = { onAction(EditarEmpregoAction.AlterarTipoNsr(it)) }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Localização
                SwitchOption(
                    title = "Habilitar Localização",
                    description = "Capturar localização ao registrar ponto",
                    checked = uiState.habilitarLocalizacao,
                    onCheckedChange = { onAction(EditarEmpregoAction.AlterarHabilitarLocalizacao(it)) }
                )

                AnimatedVisibility(visible = uiState.habilitarLocalizacao) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        SwitchOption(
                            title = "Captura Automática",
                            description = "Capturar localização automaticamente",
                            checked = uiState.localizacaoAutomatica,
                            onCheckedChange = { onAction(EditarEmpregoAction.AlterarLocalizacaoAutomatica(it)) }
                        )
                    }
                }
            }
        }

        // Seção: Banco de Horas
        item {
            FormSection(
                title = "Banco de Horas",
                icon = Icons.Default.AccountBalance,
                isExpanded = uiState.secaoExpandida == SecaoFormulario.BANCO_HORAS,
                onToggle = { onAction(EditarEmpregoAction.ToggleSecao(SecaoFormulario.BANCO_HORAS)) }
            ) {
                // Período do banco de horas
                MinutesSlider(
                    label = "Período do Banco",
                    value = uiState.periodoBancoHorasMeses,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarPeriodoBancoHoras(it)) },
                    minMinutes = 0,
                    maxMinutes = 12,
                    step = 1,
                    formatAsHours = false,
                    suffix = if (uiState.periodoBancoHorasMeses == 0) "Desabilitado" 
                             else if (uiState.periodoBancoHorasMeses == 1) "mês" 
                             else "meses",
                    helperText = "Período para fechamento do banco"
                )

                AnimatedVisibility(visible = uiState.temBancoHoras) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Primeiro dia da semana
                        DiaSemanaSelector(
                            label = "Primeiro Dia da Semana",
                            selected = uiState.primeiroDiaSemana,
                            onSelect = { onAction(EditarEmpregoAction.AlterarPrimeiroDiaSemana(it)) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Primeiro dia do mês
                        MinutesSlider(
                            label = "Dia de Fechamento",
                            value = uiState.primeiroDiaMes,
                            onValueChange = { onAction(EditarEmpregoAction.AlterarPrimeiroDiaMes(it)) },
                            minMinutes = 1,
                            maxMinutes = 28,
                            step = 1,
                            formatAsHours = false,
                            suffix = "",
                            helperText = "Dia do mês para fechamento"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SwitchOption(
                            title = "Zerar Saldo Mensalmente",
                            description = "Reiniciar saldo a cada fechamento",
                            checked = uiState.zerarSaldoMensal,
                            onCheckedChange = { onAction(EditarEmpregoAction.AlterarZerarSaldoMensal(it)) }
                        )
                    }
                }
            }
        }

        // Seção: Configurações Avançadas
        item {
            FormSection(
                title = "Avançado",
                icon = Icons.Default.Settings,
                isExpanded = uiState.secaoExpandida == SecaoFormulario.AVANCADO,
                onToggle = { onAction(EditarEmpregoAction.ToggleSecao(SecaoFormulario.AVANCADO)) }
            ) {
                SwitchOption(
                    title = "Exigir Justificativa",
                    description = "Exigir justificativa para registros inconsistentes",
                    checked = uiState.exigeJustificativaInconsistencia,
                    onCheckedChange = { onAction(EditarEmpregoAction.AlterarExigeJustificativa(it)) }
                )
            }
        }

        // Botão Salvar
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onAction(EditarEmpregoAction.Salvar) },
                enabled = uiState.formularioValido && !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(uiState.textoBotaoSalvar)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
// COMPONENTES DO FORMULÁRIO
// ══════════════════════════════════════════════════════════════════════

/**
 * Seção expansível do formulário.
 */
@Composable
private fun FormSection(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Header clicável
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Recolher" else "Expandir"
                    )
                }
            }

            // Conteúdo expansível
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
                    content()
                }
            }
        }
    }
}

/**
 * Slider para seleção de duração (Duration).
 */
@Composable
private fun DurationSlider(
    label: String,
    value: Duration,
    onValueChange: (Duration) -> Unit,
    minHours: Int,
    maxHours: Int,
    stepMinutes: Int
) {
    val totalMinutes = value.toMinutes().toInt()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = String.format("%02d:%02d", hours, minutes),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = totalMinutes.toFloat(),
            onValueChange = { onValueChange(Duration.ofMinutes(it.toLong())) },
            valueRange = (minHours * 60f)..(maxHours * 60f),
            steps = ((maxHours - minHours) * 60 / stepMinutes) - 1
        )
    }
}

/**
 * Slider para seleção de minutos.
 */
@Composable
private fun MinutesSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    minMinutes: Int,
    maxMinutes: Int,
    step: Int,
    formatAsHours: Boolean,
    suffix: String = "min",
    helperText: String? = null
) {
    val displayValue = if (formatAsHours) {
        val h = value / 60
        val m = value % 60
        if (m == 0) "${h}h" else "${h}h${m}min"
    } else {
        if (suffix.isEmpty()) "$value" else "$value $suffix"
    }

    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = displayValue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = minMinutes.toFloat()..maxMinutes.toFloat(),
            steps = if (step > 0) ((maxMinutes - minMinutes) / step) - 1 else 0
        )
        helperText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Switch com título e descrição.
 */
@Composable
private fun SwitchOption(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * Seletor de tipo de NSR.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TipoNsrSelector(
    selected: TipoNsr,
    onSelect: (TipoNsr) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = when (selected) {
                TipoNsr.NUMERICO -> "Numérico"
                TipoNsr.ALFANUMERICO -> "Alfanumérico"
            },
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo do NSR") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Numérico") },
                onClick = {
                    onSelect(TipoNsr.NUMERICO)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Alfanumérico") },
                onClick = {
                    onSelect(TipoNsr.ALFANUMERICO)
                    expanded = false
                }
            )
        }
    }
}

/**
 * Seletor de dia da semana.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaSemanaSelector(
    label: String,
    selected: DiaSemana,
    onSelect: (DiaSemana) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selected.descricao,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DiaSemana.entries.forEach { dia ->
                    DropdownMenuItem(
                        text = { Text(dia.descricao) },
                        onClick = {
                            onSelect(dia)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
