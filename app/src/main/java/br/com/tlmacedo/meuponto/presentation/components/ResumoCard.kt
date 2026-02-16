// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/ResumoCard.kt
package br.com.tlmacedo.meuponto.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.tlmacedo.meuponto.domain.model.BancoHoras
import br.com.tlmacedo.meuponto.domain.model.ResumoDia
import br.com.tlmacedo.meuponto.presentation.theme.Error
import br.com.tlmacedo.meuponto.presentation.theme.ErrorLight
import br.com.tlmacedo.meuponto.presentation.theme.Info
import br.com.tlmacedo.meuponto.presentation.theme.InfoLight
import br.com.tlmacedo.meuponto.presentation.theme.Success
import br.com.tlmacedo.meuponto.presentation.theme.SuccessLight
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.abs

/**
 * Card de resumo do dia com horas trabalhadas, saldo, banco de horas
 * e contador em tempo real quando há jornada em andamento.
 *
 * Apresenta as informações principais de forma visual e intuitiva,
 * com indicadores coloridos para facilitar a compreensão.
 *
 * @param resumoDia Resumo do dia atual
 * @param bancoHoras Banco de horas acumulado
 * @param dataHoraInicioContador Data/hora de início para o contador (se jornada em andamento)
 * @param mostrarContador Se deve exibir o contador em tempo real
 * @param modifier Modificador opcional
 *
 * @author Thiago
 * @since 1.0.0
 */
@Composable
fun ResumoCard(
    resumoDia: ResumoDia,
    bancoHoras: BancoHoras,
    dataHoraInicioContador: LocalDateTime? = null,
    mostrarContador: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Cabeçalho
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Resumo do Dia",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                // Badge de status quando jornada em andamento
                if (mostrarContador) {
                    StatusBadge(texto = "Em andamento")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contador em tempo real (quando há jornada em andamento)
            AnimatedVisibility(
                visible = mostrarContador && dataHoraInicioContador != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                dataHoraInicioContador?.let { inicio ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Tempo de trabalho",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LiveCounter(
                            dataHoraInicio = inicio,
                            fontSize = 32.sp,
                            showIcon = true,
                            showBackground = true
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            // Resumo em três colunas
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Horas Trabalhadas
                ResumoItem(
                    icone = Icons.Default.AccessTime,
                    titulo = "Trabalhado",
                    valor = formatarDuracao(resumoDia.horasTrabalhadas),
                    corIcone = Info,
                    corFundo = InfoLight,
                    modifier = Modifier.weight(1f)
                )

                // Saldo do Dia
                ResumoItem(
                    icone = if (resumoDia.temSaldoPositivo) {
                        Icons.AutoMirrored.Filled.TrendingUp
                    } else {
                        Icons.AutoMirrored.Filled.TrendingDown
                    },
                    titulo = "Saldo Dia",
                    valor = formatarSaldo(resumoDia.saldoDia),
                    corIcone = if (resumoDia.temSaldoNegativo) Error else Success,
                    corFundo = if (resumoDia.temSaldoNegativo) ErrorLight else SuccessLight,
                    modifier = Modifier.weight(1f)
                )

                // Banco de Horas
                ResumoItem(
                    icone = Icons.Default.AccountBalance,
                    titulo = "Banco",
                    valor = bancoHoras.formatarSaldo(),
                    corIcone = if (bancoHoras.negativo) Error else Success,
                    corFundo = if (bancoHoras.negativo) ErrorLight else SuccessLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Badge de status para indicar jornada em andamento.
 */
@Composable
private fun StatusBadge(
    texto: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Success.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.PlayCircle,
            contentDescription = null,
            tint = Success,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = Success
        )
    }
}

/**
 * Item individual do resumo.
 */
@Composable
private fun ResumoItem(
    icone: ImageVector,
    titulo: String,
    valor: String,
    corIcone: Color,
    corFundo: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(corFundo)
        ) {
            Icon(
                imageVector = icone,
                contentDescription = titulo,
                tint = corIcone,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Formata uma duração para exibição (compatível com API 26+).
 */
private fun formatarDuracao(duracao: Duration): String {
    val totalMinutos = duracao.toMinutes()
    val horas = totalMinutos / 60
    val minutos = abs(totalMinutos % 60)
    return "${horas}h${minutos.toString().padStart(2, '0')}"
}

/**
 * Formata o saldo com sinal (compatível com API 26+).
 */
private fun formatarSaldo(duracao: Duration): String {
    val totalMinutos = duracao.toMinutes()
    val horas = abs(totalMinutos / 60)
    val minutos = abs(totalMinutos % 60)
    val sinal = when {
        totalMinutos > 0 -> "+"
        totalMinutos < 0 -> "-"
        else -> ""
    }
    return "$sinal${horas}h${minutos.toString().padStart(2, '0')}"
}
