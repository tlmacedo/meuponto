// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/empregos/GerenciarEmpregosScreen.kt
package br.com.tlmacedo.meuponto.presentation.screen.settings.empregos

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.tlmacedo.meuponto.domain.model.Emprego
import br.com.tlmacedo.meuponto.presentation.components.MeuPontoTopBar
import kotlinx.coroutines.flow.collectLatest

/**
 * Tela de gerenciamento de empregos.
 *
 * Exibe a lista de empregos ativos e arquivados, permitindo ao usuário
 * definir o emprego ativo, arquivar, desarquivar ou excluir empregos.
 *
 * @param onNavigateBack Callback para voltar à tela anterior
 * @param onNavigateToEditarEmprego Callback para navegar à tela de edição de emprego
 * @param onNavigateToNovoEmprego Callback para navegar à tela de criação de emprego
 * @param modifier Modificador opcional para customização do layout
 * @param viewModel ViewModel da tela de gerenciamento de empregos
 *
 * @author Thiago
 * @since 2.0.0
 */
@Composable
fun GerenciarEmpregosScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditarEmprego: (Long) -> Unit,
    onNavigateToNovoEmprego: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GerenciarEmpregosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Coleta eventos do ViewModel
    LaunchedEffect(Unit) {
        viewModel.eventos.collectLatest { evento ->
            when (evento) {
                is GerenciarEmpregosEvent.MostrarMensagem -> {
                    snackbarHostState.showSnackbar(evento.mensagem)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            MeuPontoTopBar(
                title = "Gerenciar Empregos",
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNovoEmprego,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Novo emprego"
                )
            }
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
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Seção: Empregos ativos
                if (uiState.empregos.isNotEmpty()) {
                    item {
                        Text(
                            text = "Empregos Ativos",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(
                        items = uiState.empregos,
                        key = { it.id }
                    ) { emprego ->
                        EmpregoCard(
                            emprego = emprego,
                            isAtivo = emprego.id == uiState.empregoAtivoId,
                            onDefinirAtivo = {
                                viewModel.onAction(GerenciarEmpregosAction.DefinirAtivo(emprego))
                            },
                            onEditar = { onNavigateToEditarEmprego(emprego.id) },
                            onArquivar = {
                                viewModel.onAction(GerenciarEmpregosAction.Arquivar(emprego))
                            },
                            onExcluir = {
                                viewModel.onAction(GerenciarEmpregosAction.SolicitarExclusao(emprego))
                            }
                        )
                    }
                }

                // Seção: Empregos arquivados
                if (uiState.empregosArquivados.isNotEmpty()) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Arquivados (${uiState.empregosArquivados.size})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = {
                                    viewModel.onAction(GerenciarEmpregosAction.ToggleMostrarArquivados)
                                }
                            ) {
                                Text(if (uiState.mostrarArquivados) "Ocultar" else "Mostrar")
                            }
                        }
                    }

                    if (uiState.mostrarArquivados) {
                        items(
                            items = uiState.empregosArquivados,
                            key = { it.id }
                        ) { emprego ->
                            EmpregoArquivadoCard(
                                emprego = emprego,
                                onRestaurar = {
                                    viewModel.onAction(GerenciarEmpregosAction.Desarquivar(emprego))
                                },
                                onExcluir = {
                                    viewModel.onAction(GerenciarEmpregosAction.SolicitarExclusao(emprego))
                                }
                            )
                        }
                    }
                }

                // Estado vazio
                if (uiState.empregos.isEmpty() && uiState.empregosArquivados.isEmpty()) {
                    item {
                        EmptyState()
                    }
                }
            }
        }
    }

    // Dialog de confirmação de exclusão
    uiState.dialogConfirmacaoExclusao?.let { emprego ->
        ConfirmacaoExclusaoDialog(
            nomeEmprego = emprego.nome,
            onConfirmar = { viewModel.onAction(GerenciarEmpregosAction.ConfirmarExclusao) },
            onCancelar = { viewModel.onAction(GerenciarEmpregosAction.CancelarExclusao) }
        )
    }
}

/**
 * Card de emprego ativo.
 *
 * Exibe informações do emprego com menu de ações (definir ativo, editar, arquivar, excluir).
 */
@Composable
private fun EmpregoCard(
    emprego: Emprego,
    isAtivo: Boolean,
    onDefinirAtivo: () -> Unit,
    onEditar: () -> Unit,
    onArquivar: () -> Unit,
    onExcluir: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isAtivo) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Ícone indicando status
            Icon(
                imageVector = if (isAtivo) Icons.Default.CheckCircle else Icons.Default.Business,
                contentDescription = null,
                tint = if (isAtivo) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            // Nome e status
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = emprego.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isAtivo) {
                    Text(
                        text = "Emprego ativo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Menu de ações
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Mais opções"
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    // Opção: Definir como ativo (apenas se não for o ativo)
                    if (!isAtivo) {
                        DropdownMenuItem(
                            text = { Text("Definir como ativo") },
                            onClick = {
                                menuExpanded = false
                                onDefinirAtivo()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                            }
                        )
                    }

                    // Opção: Editar
                    DropdownMenuItem(
                        text = { Text("Editar") },
                        onClick = {
                            menuExpanded = false
                            onEditar()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    )

                    // Opções apenas para empregos não ativos
                    if (!isAtivo) {
                        // Opção: Arquivar
                        DropdownMenuItem(
                            text = { Text("Arquivar") },
                            onClick = {
                                menuExpanded = false
                                onArquivar()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Archive, contentDescription = null)
                            }
                        )

                        HorizontalDivider()

                        // Opção: Excluir
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Excluir",
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onExcluir()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card de emprego arquivado.
 *
 * Exibe o emprego de forma esmaecida com opções de restaurar ou excluir.
 */
@Composable
private fun EmpregoArquivadoCard(
    emprego: Emprego,
    onRestaurar: () -> Unit,
    onExcluir: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Archive,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )

            Text(
                text = emprego.nome,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            TextButton(onClick = onRestaurar) {
                Text("Restaurar")
            }

            IconButton(onClick = onExcluir) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Estado vazio quando não há empregos cadastrados.
 */
@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nenhum emprego cadastrado",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Toque em + para adicionar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

/**
 * Dialog de confirmação de exclusão de emprego.
 */
@Composable
private fun ConfirmacaoExclusaoDialog(
    nomeEmprego: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("Excluir Emprego") },
        text = {
            Text(
                "Tem certeza que deseja excluir \"$nomeEmprego\"?\n\n" +
                "Esta ação é irreversível e todos os registros de ponto associados serão perdidos."
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmar,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Excluir")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}
