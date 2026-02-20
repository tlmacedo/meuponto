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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.tlmacedo.meuponto.domain.model.BancoHoras
import br.com.tlmacedo.meuponto.domain.model.ResumoDia
import br.com.tlmacedo.meuponto.domain.model.VersaoJornada
import br.com.tlmacedo.meuponto.presentation.theme.Error
import br.com.tlmacedo.meuponto.presentation.theme.ErrorLight
import br.com.tlmacedo.meuponto.presentation.theme.Info
import br.com.tlmacedo.meuponto.presentation.theme.InfoLight
import br.com.tlmacedo.meuponto.presentation.theme.Success
import br.com.tlmacedo.meuponto.presentation.theme.SuccessLight
import java.time.LocalDateTime
import kotlin.math.abs

/**
 * Card compacto de resumo do dia com valores dinâmicos e cores semânticas.
 *
 * Cores dinâmicas:
 * - **Trabalhado**: vermelho (< jornada) → branco (= jornada) → verde (> jornada)
 * - **Saldo Dia**: verde/↑ (positivo) | branco/→ (zero) | vermelho/↓ (negativo)
 * - **Banco**: verde (positivo) | branco/cinza (zero) | vermelho (negativo)
 *
 * Formato dos valores:
 * - Com horas: "+ 01h 30min" ou "- 01h 30min"
 * - Sem horas: "+ 45min" ou "- 45min"
 * - Zero: "00min"
 *
 * @author Thiago
 * @since 1.0.0
 * @updated 3.0.0 - Layout compactado, valores dinâmicos com cores semânticas e contraste
 */
@Composable
fun ResumoCard(
    resumoDia: ResumoDia,
    bancoHoras: BancoHoras,
    versaoJornada: VersaoJornada? = null,
    dataHoraInicioContador: LocalDateTime? = null,
    mostrarContador: Boolean = false,
    modifier: Modifier = Modifier
) {
    val textoPrincipal = Color.White
    val textoSecundario = Color.White.copy(alpha = 0.85f)
    val textoTerciario = Color.White.copy(alpha = 0.7f)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Cabeçalho compacto
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Resumo do Dia",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = textoPrincipal
                    )
                    JornadaVersaoInfoCompact(
                        cargaHorariaFormatada = resumoDia.cargaHorariaDiariaFormatada,
                        versaoJornada = versaoJornada,
                        corTexto = textoTerciario
                    )
                }

                if (mostrarContador) {
                    StatusBadgeCompact(texto = "Em andamento")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contador em tempo real (quando trabalhando)
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
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "Tempo de trabalho",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = textoSecundario
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        LiveCounter(
                            dataHoraInicio = inicio,
                            fontSize = 28.sp,
                            showIcon = true,
                            showBackground = true
                        )
                    }

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            // Resumo em três colunas compactas
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // TRABALHADO: vermelho (< jornada) | branco (= jornada) | verde (> jornada)
                ResumoItemTrabalhado(
                    titulo = "Trabalhado no dia",
                    minutosTrabalhados = resumoDia.horasTrabalhadasMinutos,
                    minutosJornada = resumoDia.cargaHorariaDiariaMinutos,
                    corTitulo = textoTerciario,
                    modifier = Modifier.weight(1f)
                )

                // SALDO DIA: ↑ verde (positivo) | → branco (zero) | ↓ vermelho (negativo)
                ResumoItemSaldo(
                    titulo = "Saldo do dia",
                    saldoMinutos = resumoDia.saldoDiaMinutos,
                    corTitulo = textoTerciario,
                    modifier = Modifier.weight(1f)
                )

                // BANCO: verde (positivo) | branco/cinza (zero) | vermelho (negativo)
                ResumoItemBanco(
                    titulo = "Banco de horas",
                    bancoHoras = bancoHoras,
                    corTitulo = textoTerciario,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Info compacta de jornada e versão.
 */
@Composable
private fun JornadaVersaoInfoCompact(
    cargaHorariaFormatada: String,
    versaoJornada: VersaoJornada?,
    corTexto: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(top = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = corTexto,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = cargaHorariaFormatada,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            color = corTexto
        )

        versaoJornada?.let { versao ->
            Text(
                text = " • ",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                color = corTexto.copy(alpha = 0.6f)
            )
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = corTexto,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = versao.periodoFormatado,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                color = corTexto,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Badge de status compacto.
 */
@Composable
private fun StatusBadgeCompact(
    texto: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.PlayCircle,
            contentDescription = null,
            tint = Success,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

/**
 * Item de resumo para Trabalhado.
 * Cor: vermelho (< jornada) | branco (= jornada) | verde (> jornada)
 */
@Composable
private fun ResumoItemTrabalhado(
    titulo: String,
    minutosTrabalhados: Int,
    minutosJornada: Int,
    corTitulo: Color,
    modifier: Modifier = Modifier
) {
    val isAbaixo = minutosTrabalhados < minutosJornada
    val isIgual = minutosTrabalhados == minutosJornada
    val isAcima = minutosTrabalhados > minutosJornada

    val corValor = when {
        isAbaixo -> Error
        isIgual -> Color.White
        else -> Success
    }

    val corIcone = when {
        isAbaixo -> Error
        isIgual -> Info
        else -> Success
    }

    val corFundo = when {
        isAbaixo -> ErrorLight
        isIgual -> InfoLight
        else -> SuccessLight
    }

    // Fundo para contraste do valor
    val corFundoValor = when {
        isAbaixo -> Color.Black.copy(alpha = 0.25f)
        isIgual -> Color.Transparent
        else -> Color.Black.copy(alpha = 0.2f)
    }

    val valorFormatado = formatarDuracaoCompacta(minutosTrabalhados)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(corFundo)
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = titulo,
                tint = corIcone,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = corTitulo
        )
        Spacer(modifier = Modifier.height(2.dp))
        // Valor com fundo para contraste
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(corFundoValor)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = valorFormatado,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = corValor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Item de resumo para Saldo do Dia.
 * Ícone: ↑ (positivo) | — (zero) | ↓ (negativo)
 * Cor: verde (positivo) | branco (zero) | vermelho (negativo)
 */
@Composable
private fun ResumoItemSaldo(
    titulo: String,
    saldoMinutos: Int,
    corTitulo: Color,
    modifier: Modifier = Modifier
) {
    val isPositivo = saldoMinutos > 0
    val isZero = saldoMinutos == 0
    val isNegativo = saldoMinutos < 0

    val icone = when {
        isPositivo -> Icons.AutoMirrored.Filled.TrendingUp
        isNegativo -> Icons.AutoMirrored.Filled.TrendingDown
        else -> Icons.Default.Remove
    }

    val corIcone = when {
        isPositivo -> Success
        isNegativo -> Error
        else -> Color.White
    }

    val corFundo = when {
        isPositivo -> SuccessLight
        isNegativo -> ErrorLight
        else -> Color.White.copy(alpha = 0.3f)
    }

    val corValor = when {
        isPositivo -> Success
        isNegativo -> Error
        else -> Color.White
    }

    // Fundo para contraste do valor
    val corFundoValor = when {
        isPositivo -> Color.Black.copy(alpha = 0.2f)
        isNegativo -> Color.Black.copy(alpha = 0.25f)
        else -> Color.Transparent
    }

    val valorFormatado = formatarSaldoCompacto(saldoMinutos)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(corFundo)
        ) {
            Icon(
                imageVector = icone,
                contentDescription = titulo,
                tint = corIcone,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = corTitulo
        )
        Spacer(modifier = Modifier.height(2.dp))
        // Valor com fundo para contraste
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(corFundoValor)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = valorFormatado,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = corValor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Item de resumo para Banco de Horas.
 * Cor: verde (positivo) | branco/cinza (zero) | vermelho (negativo)
 */
@Composable
private fun ResumoItemBanco(
    titulo: String,
    bancoHoras: BancoHoras,
    corTitulo: Color,
    modifier: Modifier = Modifier
) {
    val saldoMinutos = bancoHoras.saldoTotalMinutos
    val isPositivo = saldoMinutos > 0
    val isZero = saldoMinutos == 0
    val isNegativo = saldoMinutos < 0

    val corIcone = when {
        isPositivo -> Success
        isNegativo -> Error
        else -> Color.White.copy(alpha = 0.7f)
    }

    val corFundo = when {
        isPositivo -> SuccessLight
        isNegativo -> ErrorLight
        else -> Color.White.copy(alpha = 0.2f)
    }

    val corValor = when {
        isPositivo -> Success
        isNegativo -> Error
        else -> Color.White
    }

    // Fundo para contraste do valor
    val corFundoValor = when {
        isPositivo -> Color.Black.copy(alpha = 0.2f)
        isNegativo -> Color.Black.copy(alpha = 0.25f)
        else -> Color.Transparent
    }

    val valorFormatado = formatarSaldoCompacto(saldoMinutos)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(corFundo)
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = titulo,
                tint = corIcone,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = corTitulo
        )
        Spacer(modifier = Modifier.height(2.dp))
        // Valor com fundo para contraste
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(corFundoValor)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = valorFormatado,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = corValor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Formata duração (sem sinal) para exibição compacta.
 * - Com horas: "01h 30min"
 * - Sem horas: "45min"
 * - Zero: "00min"
 */
private fun formatarDuracaoCompacta(minutos: Int): String {
    val minutosAbs = abs(minutos)
    val horas = minutosAbs / 60
    val mins = minutosAbs % 60

    return if (horas > 0) {
        "%02dh %02dmin".format(horas, mins)
    } else {
        "%02dmin".format(mins)
    }
}

/**
 * Formata saldo com sinal para exibição compacta.
 * - Positivo com horas: "+ 01h 30min"
 * - Positivo sem horas: "+ 45min"
 * - Negativo com horas: "- 01h 30min"
 * - Negativo sem horas: "- 45min"
 * - Zero: "00min"
 */
private fun formatarSaldoCompacto(minutos: Int): String {
    if (minutos == 0) return "00min"

    val minutosAbs = abs(minutos)
    val horas = minutosAbs / 60
    val mins = minutosAbs % 60

    val sinal = if (minutos > 0) "+ " else "- "

    return if (horas > 0) {
        "$sinal%02dh %02dmin".format(horas, mins)
    } else {
        "$sinal%02dmin".format(mins)
    }
}
