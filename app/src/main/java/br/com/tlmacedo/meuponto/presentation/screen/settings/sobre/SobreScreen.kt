// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/sobre/SobreScreen.kt
package br.com.tlmacedo.meuponto.presentation.screen.settings.sobre

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.tlmacedo.meuponto.presentation.components.MeuPontoTopBar

/**
 * Tela "Sobre" do aplicativo.
 *
 * Exibe informações sobre o aplicativo, versão e desenvolvedor.
 *
 * @param onNavigateBack Callback para voltar à tela anterior
 * @param modifier Modificador opcional para customização do layout
 *
 * @author Thiago
 * @since 2.0.0
 */
@Composable
fun SobreScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            MeuPontoTopBar(
                title = "Sobre",
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Ícone do app
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )

            // Nome do app
            Text(
                text = "MeuPonto",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Descrição
            Text(
                text = "Controle de ponto simples e eficiente",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card com informações
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    InfoRow(label = "Versão", value = "2.0.0-alpha")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow(label = "Desenvolvedor", value = "Thiago Macedo")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow(label = "Contato", value = "thiago@tlmacedo.com.br")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Rodapé
            Text(
                text = "© 2025 TLMacedo - Todos os direitos reservados",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Linha de informação com label e valor.
 */
@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
