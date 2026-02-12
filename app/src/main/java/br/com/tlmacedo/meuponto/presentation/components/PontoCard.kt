// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/PontoCard.kt
package br.com.tlmacedo.meuponto.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.model.TipoPonto

/**
 * Card que exibe as informações de um registro de ponto.
 *
 * Mostra o tipo do ponto, horário, observação (se houver) e
 * indicador de edição manual. Possui botões para editar e excluir.
 *
 * @param ponto Dados do ponto a ser exibido
 * @param onEditClick Callback chamado quando o botão editar é clicado
 * @param onDeleteClick Callback chamado quando o botão excluir é clicado
 * @param modifier Modificador opcional para customização do layout
 *
 * @author Thiago
 * @since 1.0.0
 */
@Composable
fun PontoCard(
    ponto: Ponto,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Define cor do tipo baseado em entrada/saída
    val tipoColor = when (ponto.tipo) {
        TipoPonto.ENTRADA -> MaterialTheme.colorScheme.primary
        TipoPonto.SAIDA -> MaterialTheme.colorScheme.tertiary
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Informações do ponto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Tipo do ponto
                Text(
                    text = ponto.tipo.descricao,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = tipoColor
                )

                // Horário
                Text(
                    text = ponto.horaFormatada,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )

                // Observação (se houver)
                ponto.observacao?.let { obs ->
                    Text(
                        text = obs,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Indicador de edição manual
                if (ponto.isEditadoManualmente) {
                    Text(
                        text = "Editado manualmente",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Botões de ação
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar ponto",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir ponto",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
