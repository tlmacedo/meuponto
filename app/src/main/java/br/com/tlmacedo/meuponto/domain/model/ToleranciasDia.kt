// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/ToleranciasDia.kt
package br.com.tlmacedo.meuponto.domain.model

/**
 * Modelo que representa as tolerâncias efetivas para um dia específico.
 *
 * Combina as tolerâncias globais do emprego com as específicas do dia,
 * aplicando a regra de que valores específicos sobrescrevem os globais.
 *
 * @property entradaMinutos Tolerância de entrada em minutos
 * @property saidaMinutos Tolerância de saída em minutos
 * @property intervaloMaisMinutos Tolerância para mais no intervalo
 * @property usaToleranciaEspecificaEntrada Se true, está usando tolerância específica do dia
 * @property usaToleranciaEspecificaSaida Se true, está usando tolerância específica do dia
 *
 * @author Thiago
 * @since 2.1.0
 * @updated 2.3.1 - Removida tolerância de redução de intervalo
 */
data class ToleranciasDia(
    val entradaMinutos: Int,
    val saidaMinutos: Int,
    val intervaloMaisMinutos: Int,
    val usaToleranciaEspecificaEntrada: Boolean = false,
    val usaToleranciaEspecificaSaida: Boolean = false
) {
    companion object {
        /**
         * Cria ToleranciasDia a partir das configurações globais e do dia específico.
         *
         * @param configuracao Configurações globais do emprego
         * @param horarioDia Configurações específicas do dia (opcional)
         * @return Tolerâncias efetivas para o dia
         */
        fun criar(
            configuracao: ConfiguracaoEmprego,
            horarioDia: HorarioDiaSemana? = null
        ): ToleranciasDia {
            return ToleranciasDia(
                entradaMinutos = horarioDia?.toleranciaEntradaMinutos
                    ?: configuracao.toleranciaEntradaMinutos,
                saidaMinutos = horarioDia?.toleranciaSaidaMinutos
                    ?: configuracao.toleranciaSaidaMinutos,
                intervaloMaisMinutos = horarioDia?.toleranciaIntervaloMaisMinutos
                    ?: configuracao.toleranciaIntervaloMaisMinutos,
                usaToleranciaEspecificaEntrada = horarioDia?.toleranciaEntradaMinutos != null,
                usaToleranciaEspecificaSaida = horarioDia?.toleranciaSaidaMinutos != null
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
            if (usaToleranciaEspecificaEntrada) append("*")
            append(" | Saída: ${saidaMinutos}min")
            if (usaToleranciaEspecificaSaida) append("*")
            if (intervaloMaisMinutos > 0) {
                append(" | Intervalo (+): ${intervaloMaisMinutos}min")
            }
        }
}
