// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/ConfiguracaoEmprego.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Modelo de domínio que representa as configurações específicas de um emprego.
 *
 * Contém todas as configurações relacionadas a jornada de trabalho,
 * banco de horas, validações e preferências do emprego.
 *
 * @property id Identificador único da configuração
 * @property empregoId FK para o emprego associado
 * @property jornadaMaximaDiariaMinutos Jornada máxima diária em minutos (default: 600 = 10h)
 * @property intervaloMinimoInterjornadaMinutos Intervalo mínimo entre jornadas em minutos (default: 660 = 11h)
 * @property exigeJustificativaInconsistencia Se true, exige justificativa para registros inconsistentes
 * @property habilitarNsr Se true, habilita o campo NSR no registro de ponto
 * @property tipoNsr Tipo do campo NSR (NUMERICO ou ALFANUMERICO)
 * @property habilitarLocalizacao Se true, habilita captura de localização
 * @property localizacaoAutomatica Se true, captura localização automaticamente
 * @property exibirLocalizacaoDetalhes Se true, exibe localização nos detalhes do ponto
 * @property exibirDuracaoTurno Se true, exibe duração do turno na timeline
 * @property exibirDuracaoIntervalo Se true, exibe duração do intervalo na timeline
 * @property primeiroDiaSemana Primeiro dia da semana para cálculos
 * @property primeiroDiaMes Dia do mês que inicia o período (1-28)
 * @property zerarSaldoSemanal Se true, zera o saldo a cada semana
 * @property zerarSaldoMensal Se true, zera o saldo a cada mês
 * @property ocultarSaldoTotal Se true, oculta o saldo total na interface
 * @property periodoBancoHorasMeses Período do banco de horas em meses (0 = sem banco)
 * @property ultimoFechamentoBanco Data do último fechamento do banco de horas
 * @property diasUteisLembreteFechamento Dias úteis antes do fechamento para notificar
 * @property habilitarSugestaoAjuste Se true, sugere ajuste de horas antes do fechamento
 * @property criadoEm Timestamp de criação
 * @property atualizadoEm Timestamp da última atualização
 *
 * @author Thiago
 * @since 2.0.0
 */
data class ConfiguracaoEmprego(
    val id: Long = 0,
    val empregoId: Long,
    val jornadaMaximaDiariaMinutos: Int = 600,
    val intervaloMinimoInterjornadaMinutos: Int = 660,
    val exigeJustificativaInconsistencia: Boolean = false,
    val habilitarNsr: Boolean = false,
    val tipoNsr: TipoNsr = TipoNsr.NUMERICO,
    val habilitarLocalizacao: Boolean = false,
    val localizacaoAutomatica: Boolean = false,
    val exibirLocalizacaoDetalhes: Boolean = true,
    val exibirDuracaoTurno: Boolean = true,
    val exibirDuracaoIntervalo: Boolean = true,
    val primeiroDiaSemana: DiaSemana = DiaSemana.SEGUNDA,
    val primeiroDiaMes: Int = 1,
    val zerarSaldoSemanal: Boolean = false,
    val zerarSaldoMensal: Boolean = false,
    val ocultarSaldoTotal: Boolean = false,
    val periodoBancoHorasMeses: Int = 0,
    val ultimoFechamentoBanco: LocalDate? = null,
    val diasUteisLembreteFechamento: Int = 3,
    val habilitarSugestaoAjuste: Boolean = false,
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Verifica se o banco de horas está habilitado.
     */
    val temBancoHoras: Boolean
        get() = periodoBancoHorasMeses > 0

    /**
     * Retorna a jornada máxima diária formatada (ex: "10:00").
     */
    val jornadaMaximaDiariaFormatada: String
        get() {
            val horas = jornadaMaximaDiariaMinutos / 60
            val minutos = jornadaMaximaDiariaMinutos % 60
            return String.format("%02d:%02d", horas, minutos)
        }

    /**
     * Retorna o intervalo mínimo interjornada formatado (ex: "11:00").
     */
    val intervaloMinimoInterjornadaFormatado: String
        get() {
            val horas = intervaloMinimoInterjornadaMinutos / 60
            val minutos = intervaloMinimoInterjornadaMinutos % 60
            return String.format("%02d:%02d", horas, minutos)
        }
}
