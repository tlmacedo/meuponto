// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/ConfiguracaoEmprego.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Modelo de domínio que representa as configurações específicas de um emprego.
 *
 * @author Thiago
 * @since 2.0.0
 * @updated 2.3.2 - Adicionado zerarBancoAntesPeriodo e suporte a períodos flexíveis.
 */
data class ConfiguracaoEmprego(
    val id: Long = 0,
    val empregoId: Long,

    // JORNADA DE TRABALHO
    val cargaHorariaDiariaMinutos: Int = 492,
    val jornadaMaximaDiariaMinutos: Int = 600,
    val intervaloMinimoInterjornadaMinutos: Int = 660,
    val intervaloMinimoMinutos: Int = 60,

    // TOLERÂNCIAS GLOBAIS
    val toleranciaEntradaMinutos: Int = 10,
    val toleranciaSaidaMinutos: Int = 10,
    val toleranciaIntervaloMaisMinutos: Int = 0,

    // VALIDAÇÕES
    val exigeJustificativaInconsistencia: Boolean = false,

    // NSR
    val habilitarNsr: Boolean = false,
    val tipoNsr: TipoNsr = TipoNsr.NUMERICO,

    // LOCALIZAÇÃO
    val habilitarLocalizacao: Boolean = false,
    val localizacaoAutomatica: Boolean = false,
    val exibirLocalizacaoDetalhes: Boolean = true,

    // EXIBIÇÃO
    val exibirDuracaoTurno: Boolean = true,
    val exibirDuracaoIntervalo: Boolean = true,

    // PERÍODO
    val primeiroDiaSemana: DiaSemana = DiaSemana.SEGUNDA,
    val primeiroDiaMes: Int = 1,

    // SALDO
    val zerarSaldoSemanal: Boolean = false,
    val zerarSaldoMensal: Boolean = false,
    val ocultarSaldoTotal: Boolean = false,

    // BANCO DE HORAS
    /**
     * Valor do período do banco de horas.
     * Mapeamento: 0=Desativado, 1-3=Semanas, 4-15=Meses (valor-3)
     */
    val periodoBancoHorasMeses: Int = 0,
    val ultimoFechamentoBanco: LocalDate? = null,
    val diasUteisLembreteFechamento: Int = 3,
    val habilitarSugestaoAjuste: Boolean = false,
    val zerarBancoAntesPeriodo: Boolean = false,

    // AUDITORIA
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now()
) {
    val temBancoHoras: Boolean
        get() = periodoBancoHorasMeses > 0

    val cargaHorariaDiariaFormatada: String
        get() = formatarMinutosComoHoras(cargaHorariaDiariaMinutos)

    val jornadaMaximaDiariaFormatada: String
        get() = formatarMinutosComoHoras(jornadaMaximaDiariaMinutos)

    val intervaloMinimoInterjornadaFormatada: String
        get() = formatarMinutosComoHoras(intervaloMinimoInterjornadaMinutos)

    val intervaloMinimoFormatado: String
        get() = formatarMinutosComoHoras(intervaloMinimoMinutos)

    val temToleranciasConfiguradas: Boolean
        get() = toleranciaEntradaMinutos > 0 ||
                toleranciaSaidaMinutos > 0 ||
                toleranciaIntervaloMaisMinutos > 0

    val descricaoTolerancias: String
        get() = buildString {
            append("Entrada: ${toleranciaEntradaMinutos}min")
            append(" | Saída: ${toleranciaSaidaMinutos}min")
            if (toleranciaIntervaloMaisMinutos > 0) {
                append(" | Intervalo: +${toleranciaIntervaloMaisMinutos}min")
            }
        }

    private fun formatarMinutosComoHoras(minutos: Int): String {
        val horas = minutos / 60
        val mins = minutos % 60
        return String.format("%02d:%02d", horas, mins)
    }
}
