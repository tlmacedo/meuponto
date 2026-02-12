// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/PontoButton.kt
package br.com.tlmacedo.meuponto.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.tlmacedo.meuponto.domain.model.TipoPonto

/**
 * Botão circular principal para registro de ponto.
 *
 * Exibe um botão grande e destacado que alterna sua aparência
 * entre os estados de ENTRADA (verde) e SAÍDA (laranja).
 *
 * @param proximoTipo Tipo do próximo ponto a ser registrado
 * @param onClick Callback chamado quando o botão é clicado
 * @param modifier Modificador opcional para customização do layout
 * @param enabled Se false, o botão fica desabilitado
 *
 * @author Thiago
 * @since 1.0.0
 */
@Composable
fun PontoButton(
    proximoTipo: TipoPonto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    // Define texto e cor baseado no tipo
    val texto = proximoTipo.descricao
    val containerColor = when (proximoTipo) {
        TipoPonto.ENTRADA -> MaterialTheme.colorScheme.primary
        TipoPonto.SAIDA -> MaterialTheme.colorScheme.tertiary
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier.size(180.dp)
    ) {
        Text(
            text = texto,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
