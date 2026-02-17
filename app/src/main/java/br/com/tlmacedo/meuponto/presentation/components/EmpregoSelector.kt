// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/EmpregoSelector.kt
package br.com.tlmacedo.meuponto.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.tlmacedo.meuponto.domain.model.Emprego

/**
 * Chip seletor de emprego para exibição no header.
 *
 * Comportamentos:
 * - **Sem emprego**: Clique abre tela de criar novo emprego
 * - **Com emprego (único)**: Clique longo abre menu com opções
 * - **Com emprego (múltiplos)**: Clique abre seletor, clique longo abre menu
 *
 * @param empregoAtivo Emprego atualmente selecionado
 * @param temMultiplosEmpregos Se há mais de um emprego disponível
 * @param showMenu Se o menu dropdown está visível
 * @param onClick Callback ao clicar no chip
 * @param onLongClick Callback ao clicar e segurar no chip
 * @param onNovoEmprego Callback para criar novo emprego
 * @param onEditarEmprego Callback para editar emprego atual
 * @param onDismissMenu Callback ao fechar o menu
 * @param modifier Modificador opcional
 *
 * @author Thiago
 * @since 2.0.0
 * @updated 2.7.0 - Adicionado suporte a long press e menu de opções
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmpregoSelectorChip(
    empregoAtivo: Emprego?,
    temMultiplosEmpregos: Boolean,
    showMenu: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onNovoEmprego: () -> Unit = {},
    onEditarEmprego: () -> Unit = {},
    onDismissMenu: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    val backgroundColor by animateColorAsState(
        targetValue = if (empregoAtivo != null) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.errorContainer
        },
        label = "bg_color"
    )

    val contentColor by animateColorAsState(
        targetValue = if (empregoAtivo != null) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onErrorContainer
        },
        label = "content_color"
    )

    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(backgroundColor)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick()
                    }
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = empregoAtivo?.nome ?: "Sem emprego",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Ícone de seta apenas se tiver múltiplos empregos
            if (temMultiplosEmpregos) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Trocar emprego",
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Menu dropdown para opções do emprego
        EmpregoDropdownMenu(
            expanded = showMenu,
            temEmpregoAtivo = empregoAtivo != null,
            nomeEmprego = empregoAtivo?.nome,
            onDismiss = onDismissMenu,
            onNovoEmprego = {
                onDismissMenu()
                onNovoEmprego()
            },
            onEditarEmprego = {
                onDismissMenu()
                onEditarEmprego()
            }
        )
    }
}

/**
 * Menu dropdown com opções do emprego.
 */
@Composable
private fun EmpregoDropdownMenu(
    expanded: Boolean,
    temEmpregoAtivo: Boolean,
    nomeEmprego: String?,
    onDismiss: () -> Unit,
    onNovoEmprego: () -> Unit,
    onEditarEmprego: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        // Opção: Novo emprego
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Novo emprego")
                }
            },
            onClick = onNovoEmprego
        )

        // Opção: Editar emprego (apenas se houver emprego ativo)
        if (temEmpregoAtivo && nomeEmprego != null) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Editar emprego")
                            Text(
                                text = nomeEmprego,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                onClick = onEditarEmprego
            )
        }
    }
}

/**
 * Bottom Sheet para seleção de emprego.
 *
 * Lista todos os empregos disponíveis e permite selecionar um como ativo.
 *
 * @param empregos Lista de empregos disponíveis
 * @param empregoAtivoId ID do emprego atualmente ativo
 * @param onSelecionarEmprego Callback ao selecionar um emprego
 * @param onDismiss Callback ao fechar o sheet
 *
 * @author Thiago
 * @since 2.0.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpregoSelectorBottomSheet(
    empregos: List<Emprego>,
    empregoAtivoId: Long?,
    onSelecionarEmprego: (Emprego) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Selecionar Emprego",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Escolha o emprego para visualizar e registrar pontos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = empregos,
                    key = { it.id }
                ) { emprego ->
                    EmpregoSelectorItem(
                        emprego = emprego,
                        isSelected = emprego.id == empregoAtivoId,
                        onClick = { onSelecionarEmprego(emprego) }
                    )
                }
            }
        }
    }
}

/**
 * Item individual na lista de seleção de empregos.
 */
@Composable
private fun EmpregoSelectorItem(
    emprego: Emprego,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Ícone do emprego
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Informações do emprego
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = emprego.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                emprego.descricao?.let { descricao ->
                    Text(
                        text = descricao,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Indicador de seleção
            if (isSelected) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selecionado",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
