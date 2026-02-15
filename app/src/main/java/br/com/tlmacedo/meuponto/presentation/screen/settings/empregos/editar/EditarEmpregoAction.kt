// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/empregos/editar/EditarEmpregoAction.kt
package br.com.tlmacedo.meuponto.presentation.screen.settings.empregos.editar

import br.com.tlmacedo.meuponto.domain.model.DiaSemana
import br.com.tlmacedo.meuponto.domain.model.TipoNsr
import java.time.Duration

/**
 * Ações possíveis na tela de edição/criação de emprego.
 *
 * Define todas as interações do usuário que podem modificar
 * o estado do formulário ou disparar operações.
 *
 * @author Thiago
 * @since 2.0.0
 */
sealed interface EditarEmpregoAction {

    // ══════════════════════════════════════════════════════════════════════
    // DADOS BÁSICOS
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Altera o nome do emprego.
     *
     * @property nome Novo nome do emprego
     */
    data class AlterarNome(val nome: String) : EditarEmpregoAction

    // ══════════════════════════════════════════════════════════════════════
    // JORNADA DE TRABALHO
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Altera a carga horária diária.
     *
     * @property duracao Nova carga horária
     */
    data class AlterarCargaHorariaDiaria(val duracao: Duration) : EditarEmpregoAction

    /**
     * Altera a jornada máxima diária em minutos.
     *
     * @property minutos Novos minutos de jornada máxima
     */
    data class AlterarJornadaMaximaDiaria(val minutos: Int) : EditarEmpregoAction

    /**
     * Altera o intervalo mínimo obrigatório em minutos.
     *
     * @property minutos Novos minutos de intervalo mínimo
     */
    data class AlterarIntervaloMinimo(val minutos: Int) : EditarEmpregoAction

    /**
     * Altera o intervalo mínimo entre jornadas em minutos.
     *
     * @property minutos Novos minutos de intervalo interjornada
     */
    data class AlterarIntervaloInterjornada(val minutos: Int) : EditarEmpregoAction

    // ══════════════════════════════════════════════════════════════════════
    // TOLERÂNCIAS
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Altera a tolerância de entrada em minutos.
     *
     * @property minutos Novos minutos de tolerância
     */
    data class AlterarToleranciaEntrada(val minutos: Int) : EditarEmpregoAction

    /**
     * Altera a tolerância de saída em minutos.
     *
     * @property minutos Novos minutos de tolerância
     */
    data class AlterarToleranciaSaida(val minutos: Int) : EditarEmpregoAction

    /**
     * Altera a tolerância de intervalo em minutos.
     *
     * @property minutos Novos minutos de tolerância
     */
    data class AlterarToleranciaIntervalo(val minutos: Int) : EditarEmpregoAction

    // ══════════════════════════════════════════════════════════════════════
    // NSR E LOCALIZAÇÃO
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Alterna habilitação do NSR.
     *
     * @property habilitado Novo estado de habilitação
     */
    data class AlterarHabilitarNsr(val habilitado: Boolean) : EditarEmpregoAction

    /**
     * Altera o tipo do NSR.
     *
     * @property tipo Novo tipo de NSR
     */
    data class AlterarTipoNsr(val tipo: TipoNsr) : EditarEmpregoAction

    /**
     * Alterna habilitação da localização.
     *
     * @property habilitado Novo estado de habilitação
     */
    data class AlterarHabilitarLocalizacao(val habilitado: Boolean) : EditarEmpregoAction

    /**
     * Alterna captura automática de localização.
     *
     * @property automatica Novo estado de captura automática
     */
    data class AlterarLocalizacaoAutomatica(val automatica: Boolean) : EditarEmpregoAction

    // ══════════════════════════════════════════════════════════════════════
    // VALIDAÇÕES
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Alterna exigência de justificativa para inconsistências.
     *
     * @property exigir Novo estado de exigência
     */
    data class AlterarExigeJustificativa(val exigir: Boolean) : EditarEmpregoAction

    // ══════════════════════════════════════════════════════════════════════
    // PERÍODO E BANCO DE HORAS
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Altera o primeiro dia da semana.
     *
     * @property dia Novo primeiro dia da semana
     */
    data class AlterarPrimeiroDiaSemana(val dia: DiaSemana) : EditarEmpregoAction

    /**
     * Altera o primeiro dia do mês para fechamento.
     *
     * @property dia Novo dia do mês (1-28)
     */
    data class AlterarPrimeiroDiaMes(val dia: Int) : EditarEmpregoAction

    /**
     * Altera o período do banco de horas em meses.
     *
     * @property meses Novo período em meses (0 = desabilitado)
     */
    data class AlterarPeriodoBancoHoras(val meses: Int) : EditarEmpregoAction

    /**
     * Alterna zeragem mensal do saldo.
     *
     * @property zerar Novo estado de zeragem
     */
    data class AlterarZerarSaldoMensal(val zerar: Boolean) : EditarEmpregoAction

    // ══════════════════════════════════════════════════════════════════════
    // NAVEGAÇÃO E UI
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Expande/colapsa uma seção do formulário.
     *
     * @property secao Seção a ser expandida/colapsada
     */
    data class ToggleSecao(val secao: SecaoFormulario) : EditarEmpregoAction

    /**
     * Salva o emprego (cria ou atualiza).
     */
    data object Salvar : EditarEmpregoAction

    /**
     * Cancela a edição e volta à tela anterior.
     */
    data object Cancelar : EditarEmpregoAction

    /**
     * Limpa a mensagem de erro.
     */
    data object LimparErro : EditarEmpregoAction
}
