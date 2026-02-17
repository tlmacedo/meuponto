// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/ToleranciasDia.kt
package br.com.tlmacedo.meuponto.domain.model

/**
 * Modelo que representa as tolerâncias efetivas para um dia específico.
 *
 * As tolerâncias de entrada/saída são configuradas por dia da semana
 * em HorarioDiaSemana. A tolerância de intervalo pode vir do global
 * ou do dia específico.
 *
 * @property entradaMinutos Tolerância de entrada em minutos
 * @property saidaMinutos Tolerância de saída em minutos
 * @property intervaloMaisMinutos Tolerância para mais no intervalo
 *
 * @author Thiago
 * @since 2.1.0
 * @updated 2.5.0 - Tolerâncias de entrada/saída agora vêm apenas de HorarioDiaSemana
 */
data class ToleranciasDia(
    val entradaMinutos: Int,
    val saidaMinutos: Int,
    val intervaloMaisMinutos: Int
) {
    companion object {
        /**
         * Cria ToleranciasDia a partir das configurações do dia específico.
         *
         * @param configuracao Configurações globais do emprego (para tolerância de intervalo)
         * @param horarioDia Configurações específicas do dia
         * @return Tolerâncias efetivas para o dia
         */
        fun criar(
            configuracao: ConfiguracaoEmprego,
            horarioDia: HorarioDiaSemana?
        ): ToleranciasDia {
            return ToleranciasDia(
                entradaMinutos = horarioDia?.toleranciaEntradaMinutos ?: PADRAO.entradaMinutos,
                saidaMinutos = horarioDia?.toleranciaSaidaMinutos ?: PADRAO.saidaMinutos,
                intervaloMaisMinutos = horarioDia?.toleranciaIntervaloMaisMinutos
                    ?: configuracao.toleranciaIntervaloMaisMinutos
            )
        }

        /**
         * Tolerâncias padrão quando não há configuração.
         */
        val PADRAO = ToleranciasDia(
            entradaMinutos = 10,
            saidaMinutos = 10,
            intervaloMaisMinutos = 0
        )
    }

    /**
     * Verifica se há alguma tolerância configurada.
     */
    val temTolerancia: Boolean
        get() = entradaMinutos > 0 || saidaMinutos > 0 || intervaloMaisMinutos > 0

    /**
     * Descrição resumida das tolerâncias.
     */
    val descricao: String
        get() = buildString {
            append("Entrada: ${entradaMinutos}min")
            append(" | Saída: ${saidaMinutos}min")
            if (intervaloMaisMinutos > 0) {
                append(" | Intervalo (+): ${intervaloMaisMinutos}min")
            }
        }
}
