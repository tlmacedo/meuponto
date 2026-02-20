// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/theme/Color.kt
package br.com.tlmacedo.meuponto.presentation.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// Cores Primárias - Azul Moderno
// ============================================================================
val Primary = Color(0xFF2563EB)
val PrimaryVariant = Color(0xFF1D4ED8)
val PrimaryLight = Color(0xFF60A5FA)
val OnPrimary = Color.White

// ============================================================================
// Cores Secundárias - Slate/Cinza
// ============================================================================
val Secondary = Color(0xFF64748B)
val SecondaryVariant = Color(0xFF475569)
val OnSecondary = Color.White

// ============================================================================
// Cores de Superfície
// ============================================================================
val Surface = Color(0xFFFAFAFA)
val SurfaceVariant = Color(0xFFF1F5F9)
val OnSurface = Color(0xFF1E293B)
val OnSurfaceVariant = Color(0xFF64748B)

// ============================================================================
// Cores de Background
// ============================================================================
val Background = Color(0xFFF8FAFC)
val OnBackground = Color(0xFF0F172A)

// ============================================================================
// Cores Semânticas - Status
// ============================================================================
val Success = Color(0xFF10B981)
val SuccessLight = Color(0xFFD1FAE5)
val OnSuccess = Color.White

val Warning = Color(0xFFF59E0B)
val WarningLight = Color(0xFFFEF3C7)
val OnWarning = Color.White

val Error = Color(0xFFEF4444)
val ErrorLight = Color(0xFFFEE2E2)
val OnError = Color.White

val Info = Color(0xFF3B82F6)
val InfoLight = Color(0xFFDBEAFE)
val OnInfo = Color.White

// ============================================================================
// Cores de Ponto
// ============================================================================
val EntradaColor = Color(0xFF10B981)      // Verde para entrada
val EntradaBg = Color(0xFFD1FAE5)         // Fundo verde claro
val SaidaColor = Color(0xFFEF4444)        // Vermelho para saída
val SaidaBg = Color(0xFFFEE2E2)           // Fundo vermelho claro

// ============================================================================
// Cores do Tema Escuro - MELHORADAS PARA MAIOR CONTRASTE
// ============================================================================
val DarkPrimary = Color(0xFF60A5FA)
val DarkPrimaryVariant = Color(0xFF3B82F6)
val DarkSurface = Color(0xFF1E293B)
val DarkSurfaceVariant = Color(0xFF334155)
val DarkBackground = Color(0xFF0F172A)
val DarkOnSurface = Color(0xFFF1F5F9)
val DarkOnSurfaceVariant = Color(0xFFCBD5E1)  // Aumentado contraste (era 94A3B8)
val DarkOnBackground = Color(0xFFF8FAFC)

// Cores de Ponto - Tema Escuro (mais vibrantes)
val DarkEntradaColor = Color(0xFF34D399)   // Verde mais vibrante
val DarkEntradaBg = Color(0xFF064E3B)      // Fundo verde escuro
val DarkSaidaColor = Color(0xFFF87171)     // Vermelho mais vibrante
val DarkSaidaBg = Color(0xFF7F1D1D)        // Fundo vermelho escuro

// Cores de Status - Tema Escuro (mais vibrantes)
val DarkSuccess = Color(0xFF34D399)
val DarkSuccessLight = Color(0xFF065F46)
val DarkWarning = Color(0xFFFBBF24)
val DarkWarningLight = Color(0xFF78350F)
val DarkError = Color(0xFFF87171)
val DarkErrorLight = Color(0xFF7F1D1D)
val DarkInfo = Color(0xFF60A5FA)
val DarkInfoLight = Color(0xFF1E3A5F)

// ============================================================================
// Cores de Texto - Alto Contraste
// ============================================================================
val TextPrimary = Color(0xFFF1F5F9)        // Texto principal claro
val TextSecondary = Color(0xFFCBD5E1)      // Texto secundário com bom contraste
val TextTertiary = Color(0xFF94A3B8)       // Texto terciário
val TextMuted = Color(0xFF64748B)          // Texto apagado (usar com moderação)
