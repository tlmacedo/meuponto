// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/components/NsrInputDialog.kt
package br.com.tlmacedo.meuponto.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.tlmacedo.meuponto.domain.model.TipoNsr

/**
 * Dialog para entrada do NSR (Número Sequencial de Registro).
 *
 * @param tipoNsr Tipo de NSR (NUMERICO ou ALFANUMERICO)
 * @param valor Valor atual do NSR
 * @param tipoPonto Descrição do tipo de ponto (ex: "Entrada", "Saída")
 * @param onValorChange Callback quando o valor muda
 * @param onConfirm Callback ao confirmar
 * @param onDismiss Callback ao cancelar
 *
 * @author Thiago
 * @since 3.7.0
 */
@Composable
fun NsrInputDialog(
    tipoNsr: TipoNsr,
    valor: String,
    tipoPonto: String,
    onValorChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val keyboardType = when (tipoNsr) {
        TipoNsr.NUMERICO -> KeyboardType.Number
        TipoNsr.ALFANUMERICO -> KeyboardType.Text
    }

    val labelText = when (tipoNsr) {
        TipoNsr.NUMERICO -> "NSR (apenas números)"
        TipoNsr.ALFANUMERICO -> "NSR (letras e números)"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Numbers,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(text = "Registrar $tipoPonto")
        },
        text = {
            Column {
                Text(
                    text = "Informe o NSR do ponto eletrônico:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = valor,
                    onValueChange = { newValue ->
                        // Filtrar caracteres conforme o tipo
                        val filtered = when (tipoNsr) {
                            TipoNsr.NUMERICO -> newValue.filter { it.isDigit() }
                            TipoNsr.ALFANUMERICO -> newValue.filter { it.isLetterOrDigit() }
                        }
                        onValorChange(filtered)
                    },
                    label = { Text(labelText) },
                    placeholder = { Text("Ex: 123456") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        capitalization = if (tipoNsr == TipoNsr.ALFANUMERICO) {
                            KeyboardCapitalization.Characters
                        } else {
                            KeyboardCapitalization.None
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = valor.isNotBlank()
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
