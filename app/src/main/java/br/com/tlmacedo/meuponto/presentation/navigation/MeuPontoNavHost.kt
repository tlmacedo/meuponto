// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/navigation/MeuPontoNavHost.kt
package br.com.tlmacedo.meuponto.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.com.tlmacedo.meuponto.presentation.components.MeuPontoTopBar
import br.com.tlmacedo.meuponto.presentation.screen.editponto.EditPontoScreen
import br.com.tlmacedo.meuponto.presentation.screen.history.HistoryScreen
import br.com.tlmacedo.meuponto.presentation.screen.home.HomeScreen
import br.com.tlmacedo.meuponto.presentation.screen.settings.SettingsScreen
import br.com.tlmacedo.meuponto.presentation.screen.settings.empregos.GerenciarEmpregosScreen
import br.com.tlmacedo.meuponto.presentation.screen.settings.sobre.SobreScreen

/**
 * NavHost principal da aplicação MeuPonto.
 *
 * Define todas as rotas de navegação e suas respectivas telas,
 * gerenciando a pilha de navegação do aplicativo.
 *
 * @param navController Controlador de navegação
 * @param modifier Modificador para o container
 * @param startDestination Destino inicial (padrão: HOME)
 *
 * @author Thiago
 * @since 1.0.0
 */
@Composable
fun MeuPontoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = MeuPontoDestinations.HOME
) {
    Box(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            // ===== TELAS PRINCIPAIS =====

            // Tela inicial (Home)
            composable(MeuPontoDestinations.HOME) {
                HomeScreen(
                    onNavigateToHistory = {
                        navController.navigate(MeuPontoDestinations.HISTORY)
                    },
                    onNavigateToSettings = {
                        navController.navigate(MeuPontoDestinations.SETTINGS)
                    },
                    onNavigateToEditPonto = { pontoId ->
                        navController.navigate(MeuPontoDestinations.editPonto(pontoId))
                    }
                )
            }

            // Tela de histórico
            composable(MeuPontoDestinations.HISTORY) {
                HistoryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEditPonto = { pontoId ->
                        navController.navigate(MeuPontoDestinations.editPonto(pontoId))
                    }
                )
            }

            // Tela de edição de ponto
            composable(
                route = MeuPontoDestinations.EDIT_PONTO,
                arguments = listOf(
                    navArgument(MeuPontoDestinations.ARG_PONTO_ID) {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                EditPontoScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ===== CONFIGURAÇÕES =====

            // Tela principal de configurações
            composable(MeuPontoDestinations.SETTINGS) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEmpregos = {
                        navController.navigate(MeuPontoDestinations.GERENCIAR_EMPREGOS)
                    },
                    onNavigateToJornada = {
                        navController.navigate(MeuPontoDestinations.CONFIGURACAO_JORNADA)
                    },
                    onNavigateToHorarios = {
                        navController.navigate(MeuPontoDestinations.HORARIOS_TRABALHO)
                    },
                    onNavigateToAjustesBancoHoras = {
                        navController.navigate(MeuPontoDestinations.AJUSTES_BANCO_HORAS)
                    },
                    onNavigateToMarcadores = {
                        navController.navigate(MeuPontoDestinations.MARCADORES)
                    },
                    onNavigateToSobre = {
                        navController.navigate(MeuPontoDestinations.SOBRE)
                    }
                )
            }

            // Tela de gerenciamento de empregos
            composable(MeuPontoDestinations.GERENCIAR_EMPREGOS) {
                GerenciarEmpregosScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEditarEmprego = { empregoId ->
                        navController.navigate(MeuPontoDestinations.editarEmprego(empregoId))
                    },
                    onNavigateToNovoEmprego = {
                        navController.navigate(MeuPontoDestinations.editarEmprego(-1L))
                    }
                )
            }

            // Tela de edição/criação de emprego
            composable(
                route = MeuPontoDestinations.EDITAR_EMPREGO,
                arguments = listOf(
                    navArgument(MeuPontoDestinations.ARG_EMPREGO_ID) {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                // TODO: Implementar EditarEmpregoScreen
                PlaceholderScreen(
                    titulo = "Editar Emprego",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Tela de configuração de jornada
            composable(MeuPontoDestinations.CONFIGURACAO_JORNADA) {
                // TODO: Implementar ConfiguracaoJornadaScreen
                PlaceholderScreen(
                    titulo = "Configuração de Jornada",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Tela de horários por dia da semana
            composable(MeuPontoDestinations.HORARIOS_TRABALHO) {
                // TODO: Implementar HorariosTrabalhoScreen
                PlaceholderScreen(
                    titulo = "Horários por Dia",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Tela de ajustes de banco de horas
            composable(MeuPontoDestinations.AJUSTES_BANCO_HORAS) {
                // TODO: Implementar AjustesBancoHorasScreen
                PlaceholderScreen(
                    titulo = "Ajustes de Saldo",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Tela de marcadores
            composable(MeuPontoDestinations.MARCADORES) {
                // TODO: Implementar MarcadoresScreen
                PlaceholderScreen(
                    titulo = "Marcadores",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Tela sobre o app
            composable(MeuPontoDestinations.SOBRE) {
                SobreScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

/**
 * Tela placeholder para funcionalidades ainda não implementadas.
 *
 * Exibe um título e mensagem indicando que a funcionalidade está em desenvolvimento.
 *
 * @param titulo Título da tela
 * @param onNavigateBack Callback para voltar à tela anterior
 *
 * @author Thiago
 * @since 2.0.0
 */
@Composable
private fun PlaceholderScreen(
    titulo: String,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            MeuPontoTopBar(
                title = titulo,
                showBackButton = true,
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Construction,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Em desenvolvimento",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
