// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/empregos/editar/EditarEmpregoUiState.kt
package br.com.tlmacedo.meuponto.presentation.screen.settings.empregos.editar

import br.com.tlmacedo.meuponto.domain.model.DiaSemana
import br.com.tlmacedo.meuponto.domain.model.TipoNsr
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Estado da tela de edição/criação de emprego.
 *
 * @author Thiago
 * @since 2.0.0
 * @updated 2.3.3 - Adicionado suporte a data de início e último fechamento.
 */
data class EditarEmpregoUiState(
    val empregoId: Long? = null,
    val isNovoEmprego: Boolean = true,
    val nome: String = "",
    val nomeErro: String? = null,
    val dataInicioTrabalho: LocalDate? = null,

    // JORNADA DE TRABALHO
    val cargaHorariaDiaria: Duration = Duration.ofMinutes(492),
    val jornadaMaximaDiariaMinutos: Int = 600,
    val intervaloMinimoMinutos: Int = 60,
    val intervaloInterjornadaMinutos: Int = 660,

    // TOLERÂNCIAS GLOBAIS
    val toleranciaEntradaMinutos: Int = 10,
    val toleranciaSaidaMinutos: Int = 10,
    val toleranciaIntervaloMaisMinutos: Int = 0,

    // NSR E LOCALIZAÇÃO
    val habilitarNsr: Boolean = false,
    val tipoNsr: TipoNsr = TipoNsr.NUMERICO,
    val habilitarLocalizacao: Boolean = false,
    val localizacaoAutomatica: Boolean = false,

    // VALIDAÇÕES
    val exigeJustificativaInconsistencia: Boolean = false,

    // PERÍODO E BANCO DE HORAS
    val primeiroDiaSemana: DiaSemana = DiaSemana.SEGUNDA,
    val primeiroDiaMes: Int = 1,
    /** 0=Desativado, 1-3=Semanas, 4-15=Meses (valor-3) */
    val periodoBancoHorasValor: Int = 0,
    val zerarSaldoMensal: Boolean = false,
    val zerarBancoAntesPeriodo: Boolean = false,
    val ultimoFechamentoBanco: LocalDate? = null,

    // ESTADOS DE UI
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val erro: String? = null,
    val secaoExpandida: SecaoFormulario = SecaoFormulario.DADOS_BASICOS,
    
    // Pickers
    val showInicioTrabalhoPicker: Boolean = false,
    val showUltimoFechamentoPicker: Boolean = false
) {
    val tituloTela: String = if (isNovoEmprego) "Novo Emprego" else "Editar Emprego"
    val textoBotaoSalvar: String = if (isNovoEmprego) "Criar Emprego" else "Salvar Alterações"
    val formularioValido: Boolean = nome.isNotBlank() && nomeErro == null
    val temBancoHoras: Boolean = periodoBancoHorasValor > 0

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val dataInicioTrabalhoFormatada: String
        get() = dataInicioTrabalho?.format(dateFormatter) ?: "Não informada"

    val ultimoFechamentoBancoFormatado: String
        get() = ultimoFechamentoBanco?.format(dateFormatter) ?: "Selecionar data"

    /**
     * Retorna o texto formatado para o período selecionado.
     */
    val descricaoPeriodoBancoHoras: String
        get() = when (periodoBancoHorasValor) {
            0 -> "Desabilitado"
            1 -> "1 semana"
            2 -> "2 semanas"
            3 -> "3 semanas"
            in 4..15 -> "${periodoBancoHorasValor - 3} mês(es)"
            else -> "Personalizado"
        }

    /**
     * Retorna o texto dinâmico para a opção de zerar saldo.
     */
    val labelZerarSaldoDinamico: String
        get() = when (periodoBancoHorasValor) {
            0 -> "Zerar saldo"
            1 -> "Zerar saldo a cada semana"
            2 -> "Zerar saldo a cada 2 semanas"
            3 -> "Zerar saldo a cada 3 semanas"
            in 4..15 -> {
                val meses = periodoBancoHorasValor - 3
                if (meses == 1) "Zerar saldo mensalmente"
                else "Zerar saldo a cada $meses meses"
            }
            else -> "Zerar saldo periodicamente"
        }

    val descricaoTolerancias: String
        get() = buildString {
            append("Entrada: ${toleranciaEntradaMinutos}min | Saída: ${toleranciaSaidaMinutos}min")
            if (toleranciaIntervaloMaisMinutos > 0) {
                append(" | Intervalo: +${toleranciaIntervaloMaisMinutos}min")
            }
        }
}

enum class SecaoFormulario {
    DADOS_BASICOS, JORNADA, TOLERANCIAS, NSR_LOCALIZACAO, BANCO_HORAS, AVANCADO
}
