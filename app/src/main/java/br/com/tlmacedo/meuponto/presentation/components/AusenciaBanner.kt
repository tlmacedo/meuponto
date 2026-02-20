// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/AusenciaBanner.kt
package br.com.tlmacedo.meuponto.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.tlmacedo.meuponto.domain.model.ausencia.Ausencia
import br.com.tlmacedo.meuponto.domain.model.ausencia.TipoAusencia

/**
 * Banner que exibe informações sobre ausências do dia (férias, atestado, folga, etc.).
 *
 * @param ausencia Ausência do dia
 * @param modifier Modifier opcional
 *
 * @author Thiago
 * @since 4.0.0
 */
@Composable
fun AusenciaBanner(
    ausencia: Ausencia,
    modifier: Modifier = Modifier
) {
    val backgroundColor = ausencia.tipo.getBackgroundColor()
    val contentColor = ausencia.tipo.getContentColor()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header com ícone e tipo da ausência
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Ícone do tipo de ausência
                Icon(
                    imageVector = ausencia.tipo.getIcon(),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Tipo e descrição
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ausencia.tipo.descricao,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Descrição adicional se houver
                    ausencia.descricao?.takeIf { it != ausencia.tipo.descricao }?.let { desc ->
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Badge de status (justificada ou não)
                AusenciaStatusBadge(
                    isJustificada = ausencia.isJustificada,
                    contentColor = contentColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Período da ausência
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Chip com período
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = contentColor.copy(alpha = 0.12f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = ausencia.formatarPeriodo(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = contentColor
                        )
                    }
                }

                // Quantidade de dias (se for período)
                if (ausencia.isPeriodo) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = contentColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${ausencia.quantidadeDias} dias",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = contentColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Observação (se houver)
            ausencia.observacao?.let { obs ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = obs,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/**
 * Badge de status (justificada ou não).
 */
@Composable
private fun AusenciaStatusBadge(
    isJustificada: Boolean,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val icon = if (isJustificada) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel
    val texto = if (isJustificada) "Justificada" else "Injustificada"

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = contentColor.copy(alpha = 0.1f),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

// ============================================================================
// Extensões para TipoAusencia
// ============================================================================

/**
 * Retorna a cor de fundo apropriada para cada tipo de ausência.
 */
private fun TipoAusencia.getBackgroundColor(): Color = when (this) {
    TipoAusencia.FERIAS -> Color(0xFFE0F7FA)           // Ciano claro
    TipoAusencia.ATESTADO -> Color(0xFFFFEBEE)         // Vermelho claro
    TipoAusencia.DECLARACAO -> Color(0xFFFFF8E1)       // Âmbar claro
    TipoAusencia.FOLGA -> Color(0xFFE8F5E9)            // Verde claro
    TipoAusencia.FALTA_JUSTIFICADA -> Color(0xFFE3F2FD) // Azul claro
    TipoAusencia.FALTA_INJUSTIFICADA -> Color(0xFFFFCDD2) // Vermelho mais forte
}

/**
 * Retorna a cor de conteúdo apropriada para cada tipo de ausência.
 */
private fun TipoAusencia.getContentColor(): Color = when (this) {
    TipoAusencia.FERIAS -> Color(0xFF00695C)           // Ciano escuro
    TipoAusencia.ATESTADO -> Color(0xFFC62828)         // Vermelho escuro
    TipoAusencia.DECLARACAO -> Color(0xFFFF8F00)       // Âmbar escuro
    TipoAusencia.FOLGA -> Color(0xFF2E7D32)            // Verde escuro
    TipoAusencia.FALTA_JUSTIFICADA -> Color(0xFF1565C0) // Azul escuro
    TipoAusencia.FALTA_INJUSTIFICADA -> Color(0xFFB71C1C) // Vermelho muito escuro
}

/**
 * Retorna o ícone apropriado para cada tipo de ausência.
 */
private fun TipoAusencia.getIcon(): ImageVector = when (this) {
    TipoAusencia.FERIAS -> Icons.Default.BeachAccess
    TipoAusencia.ATESTADO -> Icons.Default.LocalHospital
    TipoAusencia.DECLARACAO -> Icons.Default.Receipt
    TipoAusencia.FOLGA -> Icons.Default.Home
    TipoAusencia.FALTA_JUSTIFICADA -> Icons.Default.EventBusy
    TipoAusencia.FALTA_INJUSTIFICADA -> Icons.Default.EventBusy
}
