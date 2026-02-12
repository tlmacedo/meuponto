// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/SettingsScreen.kt
package br.com.tlmacedo.meuponto.presentation.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.tlmacedo.meuponto.presentation.components.MeuPontoTopBar

/**
 * Tela de configurações do aplicativo.
 *
 * Permite ao usuário personalizar as configurações de jornada,
 * notificações e outras preferências do app.
 *
 * @param onNavigateBack Callback para voltar à tela anterior
 * @param modifier Modificador opcional para customização do layout
 *
 * @author Thiago
 * @since 1.0.0
 */
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            MeuPontoTopBar(
                title = "Configurações",
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Card de configurações de jornada
            SettingsSection(
                title = "Jornada de Trabalho",
                items = listOf(
                    "Carga horária diária: 08:00",
                    "Carga horária semanal: 44:00",
                    "Intervalo mínimo: 01:00",
                    "Tolerância: 10 minutos"
                )
            )

            // Card de horários padrão
            SettingsSection(
                title = "Horários Padrão",
                items = listOf(
                    "Entrada: 08:00",
                    "Saída almoço: 12:00",
                    "Retorno almoço: 13:00",
                    "Saída: 17:00"
                ),
                modifier = Modifier.padding(top = 16.dp)
            )

            // Card sobre o app
            SettingsSection(
                title = "Sobre",
                items = listOf(
                    "Versão: 1.0.0",
                    "Desenvolvido por: Thiago"
                ),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

/**
 * Seção de configurações agrupadas.
 *
 * @param title Título da seção
 * @param items Lista de itens para exibir
 * @param modifier Modificador opcional
 */
@Composable
private fun SettingsSection(
    title: String,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            items.forEach { item ->
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
