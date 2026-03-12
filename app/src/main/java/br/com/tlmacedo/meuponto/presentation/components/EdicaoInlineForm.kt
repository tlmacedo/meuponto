// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/EdicaoInlineForm.kt
package br.com.tlmacedo.meuponto.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.tlmacedo.meuponto.domain.model.MotivoEdicao
import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.model.TipoNsr
import br.com.tlmacedo.meuponto.presentation.screen.home.EdicaoInlineState
import java.time.format.DateTimeFormatter

/**
 * Formulário compacto de edição inline de ponto.
 *
 * Exibe campos essenciais para edição rápida:
 * - Hora (com TimePicker)
 * - NSR (se habilitado)
 * - Motivo da alteração (obrigatório)
 *
 * @param ponto Ponto sendo editado
 * @param state Estado atual da edição
 * @param descricaoTipoPonto Descrição do tipo (ex: "1ª Entrada", "1ª Saída")
 * @param habilitarNsr Se deve exibir campo de NSR
 * @param tipoNsr Tipo de NSR (numérico ou alfanumérico)
 * @param onHoraClick Callback para abrir TimePicker
 * @param onNsrChange Callback para alteração do NSR
 * @param onMotivoChange Callback para seleção de motivo
 * @param onMotivoDetalhesChange Callback para detalhes do motivo
 * @param onSalvar Callback para salvar alterações
 * @param onCancelar Callback para cancelar edição
 * @param modifier Modificador opcional
 *
 * @author Thiago
 * @since 7.0.0
 */
@Composable
fun EdicaoInlineForm(
    ponto: Ponto,
    state: EdicaoInlineState,
    descricaoTipoPonto: String = "Ponto",
    habilitarNsr: Boolean = false,
    tipoNsr: TipoNsr = TipoNsr.NUMERICO,
    onHoraClick: () -> Unit,
    onNsrChange: (String) -> Unit,
    onMotivoChange: (MotivoEdicao) -> Unit,
    onMotivoDetalhesChange: (String) -> Unit,
    onSalvar: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatadorHora = DateTimeFormatter.ofPattern("HH:mm")
    var showMotivoDropdown by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Editar $descricaoTipoPonto",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.weight(1f))

                // Hora original (referência)
                Text(
                    text = "Original: ${ponto.hora.format(formatadorHora)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Campos em Row para economia de espaço
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Campo de Hora
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nova Hora",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onHoraClick() }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = state.hora.format(formatadorHora),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = if (state.hora != ponto.hora) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }

                // Campo de NSR (se habilitado)
                if (habilitarNsr) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "NSR",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = state.nsr,
                            onValueChange = onNsrChange,
                            placeholder = { Text("Nº") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Dropdown de Motivo
            Column {
                Text(
                    text = "Motivo da Alteração *",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                Box {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = 1.dp,
                                color = if (state.motivoSelecionado == MotivoEdicao.NENHUM) {
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.outline
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { showMotivoDropdown = true }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (state.motivoSelecionado == MotivoEdicao.NENHUM) {
                                    "Selecione um motivo..."
                                } else {
                                    state.motivoSelecionado.descricao
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (state.motivoSelecionado == MotivoEdicao.NENHUM) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Expandir",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showMotivoDropdown,
                        onDismissRequest = { showMotivoDropdown = false }
                    ) {
                        MotivoEdicao.entries
                            .filter { it != MotivoEdicao.NENHUM }
                            .forEach { motivo ->
                                DropdownMenuItem(
                                    text = { Text(motivo.descricao) },
                                    onClick = {
                                        onMotivoChange(motivo)
                                        showMotivoDropdown = false
                                    }
                                )
                            }
                    }
                }
            }

            // Campo de detalhes do motivo (quando necessário)
            AnimatedVisibility(
                visible = state.motivoSelecionado.requerDetalhes,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                OutlinedTextField(
                    value = state.motivoDetalhes,
                    onValueChange = onMotivoDetalhesChange,
                    label = {
                        Text(
                            if (state.motivoSelecionado == MotivoEdicao.OUTRO) {
                                "Especifique o motivo *"
                            } else {
                                "Detalhes adicionais *"
                            }
                        )
                    },
                    placeholder = { Text("Mínimo 5 caracteres") },
                    isError = state.motivoDetalhes.isNotEmpty() &&
                            state.motivoDetalhes.trim().length < 5,
                    supportingText = if (state.motivoDetalhes.isNotEmpty() &&
                        state.motivoDetalhes.trim().length < 5
                    ) {
                        { Text("Mínimo 5 caracteres") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Erro (se houver)
            state.erro?.let { erro ->
                Text(
                    text = erro,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Botões de ação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelar,
                    enabled = !state.isSaving,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cancelar")
                }

                Button(
                    onClick = onSalvar,
                    enabled = state.podeSalvar,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Salvar")
                }
            }
        }
    }
}
