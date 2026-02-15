// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/empregos/editar/EditarEmpregoUiState.kt
package br.com.tlmacedo.meuponto.presentation.screen.settings.empregos.editar

import br.com.tlmacedo.meuponto.domain.model.DiaSemana
import br.com.tlmacedo.meuponto.domain.model.TipoNsr
import java.time.Duration
import java.time.LocalTime

/**
 * Estado da tela de edição/criação de emprego.
 *
 * Contém todos os dados do formulário e estados de UI necessários
 * para a criação ou edição de um emprego.
 *
 * @property empregoId ID do emprego sendo editado (null para novo)
 * @property isNovoEmprego Indica se está criando um novo emprego
 * @property nome Nome do emprego
 * @property nomeErro Mensagem de erro do campo nome
 * @property cargaHorariaDiaria Carga horária diária esperada
 * @property toleranciaEntradaMinutos Tolerância para entrada em minutos
 * @property toleranciaSaidaMinutos Tolerância para saída em minutos
 * @property toleranciaIntervaloMinutos Tolerância de intervalo em minutos
 * @property intervaloMinimoMinutos Intervalo mínimo obrigatório em minutos
 * @property jornadaMaximaDiariaMinutos Jornada máxima diária em minutos
 * @property intervaloInterjornadaMinutos Intervalo mínimo entre jornadas
 * @property habilitarNsr Habilitar campo NSR
 * @property tipoNsr Tipo do NSR (numérico ou alfanumérico)
 * @property habilitarLocalizacao Habilitar captura de localização
 * @property localizacaoAutomatica Capturar localização automaticamente
 * @property exigeJustificativaInconsistencia Exigir justificativa para inconsistências
 * @property primeiroDiaSemana Primeiro dia da semana para cálculos
 * @property primeiroDiaMes Primeiro dia do mês para fechamento
 * @property periodoBancoHorasMeses Período do banco de horas em meses
 * @property zerarSaldoMensal Zerar saldo mensalmente
 * @property isLoading Indica se está carregando dados
 * @property isSaving Indica se está salvando
 * @property erro Mensagem de erro geral
 * @property secaoExpandida Seção atualmente expandida no formulário
 *
 * @author Thiago
 * @since 2.0.0
 */
data class EditarEmpregoUiState(
    // Identificação
    val empregoId: Long? = null,
    val isNovoEmprego: Boolean = true,
    
    // Dados básicos
    val nome: String = "",
    val nomeErro: String? = null,
    
    // Jornada de trabalho
    val cargaHorariaDiaria: Duration = Duration.ofHours(8),
    val toleranciaEntradaMinutos: Int = 10,
    val toleranciaSaidaMinutos: Int = 10,
    val toleranciaIntervaloMinutos: Int = 5,
    val intervaloMinimoMinutos: Int = 60,
    val jornadaMaximaDiariaMinutos: Int = 600,
    val intervaloInterjornadaMinutos: Int = 660,
    
    // NSR e Localização
    val habilitarNsr: Boolean = false,
    val tipoNsr: TipoNsr = TipoNsr.NUMERICO,
    val habilitarLocalizacao: Boolean = false,
    val localizacaoAutomatica: Boolean = false,
    
    // Validações
    val exigeJustificativaInconsistencia: Boolean = false,
    
    // Período e Banco de Horas
    val primeiroDiaSemana: DiaSemana = DiaSemana.SEGUNDA,
    val primeiroDiaMes: Int = 1,
    val periodoBancoHorasMeses: Int = 0,
    val zerarSaldoMensal: Boolean = false,
    
    // Estados de UI
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val erro: String? = null,
    val secaoExpandida: SecaoFormulario = SecaoFormulario.DADOS_BASICOS
) {
    /**
     * Título da tela baseado no modo (criar/editar).
     */
    val tituloTela: String
        get() = if (isNovoEmprego) "Novo Emprego" else "Editar Emprego"

    /**
     * Texto do botão de ação principal.
     */
    val textoBotaoSalvar: String
        get() = if (isNovoEmprego) "Criar Emprego" else "Salvar Alterações"

    /**
     * Verifica se o formulário é válido para envio.
     */
    val formularioValido: Boolean
        get() = nome.isNotBlank() && nomeErro == null

    /**
     * Carga horária diária formatada (HH:mm).
     */
    val cargaHorariaDiariaFormatada: String
        get() {
            val horas = cargaHorariaDiaria.toHours()
            val minutos = cargaHorariaDiaria.toMinutes() % 60
            return String.format("%02d:%02d", horas, minutos)
        }

    /**
     * Jornada máxima diária formatada (HH:mm).
     */
    val jornadaMaximaDiariaFormatada: String
        get() {
            val horas = jornadaMaximaDiariaMinutos / 60
            val minutos = jornadaMaximaDiariaMinutos % 60
            return String.format("%02d:%02d", horas, minutos)
        }

    /**
     * Intervalo interjornada formatado (HH:mm).
     */
    val intervaloInterjornadaFormatado: String
        get() {
            val horas = intervaloInterjornadaMinutos / 60
            val minutos = intervaloInterjornadaMinutos % 60
            return String.format("%02d:%02d", horas, minutos)
        }

    /**
     * Verifica se o banco de horas está habilitado.
     */
    val temBancoHoras: Boolean
        get() = periodoBancoHorasMeses > 0

    /**
     * Descrição do período do banco de horas.
     */
    val descricaoPeriodoBancoHoras: String
        get() = when (periodoBancoHorasMeses) {
            0 -> "Desabilitado"
            1 -> "1 mês"
            else -> "$periodoBancoHorasMeses meses"
        }
}

/**
 * Seções do formulário de emprego.
 *
 * Define as diferentes seções que podem ser expandidas/colapsadas
 * no formulário de criação/edição de emprego.
 */
enum class SecaoFormulario {
    /** Seção de dados básicos (nome) */
    DADOS_BASICOS,
    
    /** Seção de configuração de jornada */
    JORNADA,
    
    /** Seção de tolerâncias */
    TOLERANCIAS,
    
    /** Seção de NSR e localização */
    NSR_LOCALIZACAO,
    
    /** Seção de banco de horas */
    BANCO_HORAS,
    
    /** Seção de configurações avançadas */
    AVANCADO
}
