// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/empregos/editar/EditarEmpregoScreen.kt
package br.com.tlmacedo.meuponto.presentation.screen.settings.empregos.editar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Tela de edição/criação de emprego.
 */
@Composable
fun EditarEmpregoScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditarEmpregoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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
                onSetShowInicioTrabalhoPicker = viewModel::setShowInicioTrabalhoPicker,
                onSetShowUltimoFechamentoPicker = viewModel::setShowUltimoFechamentoPicker,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditarEmpregoContent(
    uiState: EditarEmpregoUiState,
    onAction: (EditarEmpregoAction) -> Unit,
    onSetShowInicioTrabalhoPicker: (Boolean) -> Unit,
    onSetShowUltimoFechamentoPicker: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // DATE PICKER DIALOGS
    if (uiState.showInicioTrabalhoPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.dataInicioTrabalho?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { onSetShowInicioTrabalhoPicker(false) },
            confirmButton = {
                TextButton(onClick = {
                    val date = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    onAction(EditarEmpregoAction.AlterarDataInicioTrabalho(date))
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { onSetShowInicioTrabalhoPicker(false) }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (uiState.showUltimoFechamentoPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.ultimoFechamentoBanco?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { onSetShowUltimoFechamentoPicker(false) },
            confirmButton = {
                TextButton(onClick = {
                    val date = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    onAction(EditarEmpregoAction.AlterarUltimoFechamentoBanco(date))
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { onSetShowUltimoFechamentoPicker(false) }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // DADOS BÁSICOS
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
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // DATA DE INÍCIO NO TRABALHO
                OutlinedTextField(
                    value = uiState.dataInicioTrabalhoFormatada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Data de início no trabalho") },
                    trailingIcon = {
                        IconButton(onClick = { onSetShowInicioTrabalhoPicker(true) }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Selecionar data")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSetShowInicioTrabalhoPicker(true) }
                )
            }
        }

        // JORNADA DE TRABALHO
        item {
            FormSection(
                title = "Jornada de Trabalho",
                icon = Icons.Default.Schedule,
                isExpanded = uiState.secaoExpandida == SecaoFormulario.JORNADA,
                onToggle = { onAction(EditarEmpregoAction.ToggleSecao(SecaoFormulario.JORNADA)) }
            ) {
                MinutesSliderWithSteppers(
                    label = "Carga Horária Diária",
                    value = uiState.cargaHorariaDiaria.toMinutes().toInt(),
                    onValueChange = { onAction(EditarEmpregoAction.AlterarCargaHorariaDiaria(Duration.ofMinutes(it.toLong()))) },
                    valueRange = 0..720,
                    sliderStep = 30,
                    formatAsHours = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                MinutesSliderWithSteppers(
                    label = "Jornada Máxima Diária",
                    value = uiState.jornadaMaximaDiariaMinutos,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarJornadaMaximaDiaria(it)) },
                    valueRange = 0..720,
                    sliderStep = 30,
                    formatAsHours = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                MinutesSliderWithSteppers(
                    label = "Intervalo Mínimo",
                    value = uiState.intervaloMinimoMinutos,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarIntervaloMinimo(it)) },
                    valueRange = 0..120,
                    sliderStep = 15,
                    formatAsHours = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                MinutesSliderWithSteppers(
                    label = "Intervalo Entre Jornadas",
                    value = uiState.intervaloInterjornadaMinutos,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarIntervaloInterjornada(it)) },
                    valueRange = 0..2880,
                    sliderStep = 60,
                    formatAsHours = true
                )
            }
        }

        // TOLERÂNCIAS
        item {
            FormSection(
                title = "Tolerâncias",
                icon = Icons.Default.Timer,
                isExpanded = uiState.secaoExpandida == SecaoFormulario.TOLERANCIAS,
                onToggle = { onAction(EditarEmpregoAction.ToggleSecao(SecaoFormulario.TOLERANCIAS)) }
            ) {
                MinutesSliderWithSteppers(
                    label = "Tolerância Intervalo (+)",
                    value = uiState.toleranciaIntervaloMaisMinutos,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarToleranciaIntervaloMais(it)) },
                    valueRange = 0..60,
                    sliderStep = 1,
                    formatAsHours = false
                )
            }
        }

        // NSR E LOCALIZAÇÃO
        item {
            FormSection(
                title = "NSR e Localização",
                icon = Icons.Default.LocationOn,
                isExpanded = uiState.secaoExpandida == SecaoFormulario.NSR_LOCALIZACAO,
                onToggle = { onAction(EditarEmpregoAction.ToggleSecao(SecaoFormulario.NSR_LOCALIZACAO)) }
            ) {
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

        // BANCO DE HORAS
        item {
            FormSection(
                title = "Banco de Horas",
                icon = Icons.Default.AccountBalance,
                isExpanded = uiState.secaoExpandida == SecaoFormulario.BANCO_HORAS,
                onToggle = { onAction(EditarEmpregoAction.ToggleSecao(SecaoFormulario.BANCO_HORAS)) }
            ) {
                MinutesSliderWithSteppers(
                    label = "Período do Banco",
                    value = uiState.periodoBancoHorasValor,
                    onValueChange = { onAction(EditarEmpregoAction.AlterarPeriodoBancoHoras(it)) },
                    valueRange = 0..15,
                    sliderStep = 1,
                    formatAsHours = false,
                    displayFormatter = { valState -> 
                        when (valState) {
                            0 -> "Desabilitado"
                            1 -> "1 semana"
                            2 -> "2 semanas"
                            3 -> "3 semanas"
                            else -> "${valState - 3} mês(es)"
                        }
                    }
                )

                AnimatedVisibility(visible = uiState.temBancoHoras) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = uiState.zerarBancoAntesPeriodo,
                                onCheckedChange = { onAction(EditarEmpregoAction.AlterarZerarBancoAntesPeriodo(it)) }
                            )
                            Text(
                                text = "Zerar banco de horas antes do período",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        DiaSemanaSelector(
                            label = "Primeiro Dia da Semana",
                            selected = uiState.primeiroDiaSemana,
                            onSelect = { onAction(EditarEmpregoAction.AlterarPrimeiroDiaSemana(it)) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        MinutesSliderWithSteppers(
                            label = "Dia de Fechamento (RH)",
                            value = uiState.primeiroDiaMes,
                            onValueChange = { onAction(EditarEmpregoAction.AlterarPrimeiroDiaMes(it)) },
                            valueRange = 1..30,
                            sliderStep = 1,
                            formatAsHours = false,
                            suffix = ""
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SwitchOption(
                            title = uiState.labelZerarSaldoDinamico,
                            description = "Reiniciar saldo a cada fechamento de período",
                            checked = uiState.zerarSaldoMensal,
                            onCheckedChange = { onAction(EditarEmpregoAction.AlterarZerarSaldoMensal(it)) }
                        )

                        // DATA DO ÚLTIMO FECHAMENTO (Só exibe se Zerar Saldo estiver ativado)
                        AnimatedVisibility(visible = uiState.zerarSaldoMensal) {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = uiState.ultimoFechamentoBancoFormatado,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Última data de fechamento do banco") },
                                    trailingIcon = {
                                        IconButton(onClick = { onSetShowUltimoFechamentoPicker(true) }) {
                                            Icon(Icons.Default.CalendarMonth, contentDescription = "Selecionar data")
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSetShowUltimoFechamentoPicker(true) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // AVANÇADO
        item {
            FormSection(
                title = "Avançado",
                icon = Icons.Default.Settings,
                isExpanded = uiState.secaoExpandida == SecaoFormulario.AVANCADO,
                onToggle = { onAction(EditarEmpregoAction.ToggleSecao(SecaoFormulario.AVANCADO)) }
            ) {
                SwitchOption(
                    title = "Exigir Justificativa",
                    description = "Exigir justificativa para registros fora da tolerância",
                    checked = uiState.exigeJustificativaInconsistencia,
                    onCheckedChange = { onAction(EditarEmpregoAction.AlterarExigeJustificativa(it)) }
                )
            }
        }

        // SALVAR
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

@Composable
private fun MinutesSliderWithSteppers(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,
    sliderStep: Int,
    formatAsHours: Boolean,
    suffix: String = "min",
    helperText: String? = null,
    displayFormatter: ((Int) -> String)? = null
) {
    val displayValue = when {
        displayFormatter != null -> displayFormatter(value)
        formatAsHours -> {
            val h = value / 60
            val m = value % 60
            String.format("%02d:%02d", h, m)
        }
        else -> if (suffix.isEmpty()) "$value" else "$value $suffix"
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = displayValue,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { onValueChange((value - 1).coerceIn(valueRange)) },
                enabled = value > valueRange.first
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Diminuir 1 unidade")
            }
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
                steps = if (sliderStep > 0 && (valueRange.last - valueRange.first) > sliderStep) 
                            ((valueRange.last - valueRange.first) / sliderStep) - 1 
                        else 0,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { onValueChange((value + 1).coerceIn(valueRange)) },
                enabled = value < valueRange.last
            ) {
                Icon(Icons.Default.Add, contentDescription = "Aumentar 1 unidade")
            }
        }

        helperText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

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
