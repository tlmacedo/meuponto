// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/MainActivity.kt
package br.com.tlmacedo.meuponto.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.tlmacedo.meuponto.presentation.components.MeuPontoBottomBar
import br.com.tlmacedo.meuponto.presentation.navigation.BottomNavItem
import br.com.tlmacedo.meuponto.presentation.navigation.MeuPontoNavHost
import br.com.tlmacedo.meuponto.presentation.theme.MeuPontoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal da aplicação MeuPonto.
 *
 * Ponto de entrada do aplicativo, configurada com Hilt para injeção
 * de dependências e Jetpack Compose para a interface do usuário.
 * Gerencia a navegação principal através do NavController.
 *
 * @author Thiago
 * @since 1.0.0
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MeuPontoTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Define quais rotas exibem a bottom bar
                val showBottomBar = currentRoute in BottomNavItem.getItems().map { it.route }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            MeuPontoBottomBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        // Evita múltiplas cópias da mesma tela na pilha
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    MeuPontoNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
