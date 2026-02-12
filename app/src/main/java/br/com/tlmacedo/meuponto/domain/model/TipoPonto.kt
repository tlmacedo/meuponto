// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/TipoPonto.kt
package br.com.tlmacedo.meuponto.domain.model

/**
 * Enum que representa os tipos de batida de ponto.
 *
 * O sistema trabalha com alternância simples entre ENTRADA e SAÍDA,
 * permitindo flexibilidade para diferentes cenários de jornada:
 * - Mínimo: 2 pontos (1 entrada + 1 saída)
 * - Ideal: 4 pontos (entrada, saída almoço, retorno almoço, saída)
 * - Máximo: 10 pontos (múltiplos intervalos)
 *
 * @property descricao Descrição em português para exibição na interface
 * @property isEntrada Indica se é um ponto de entrada (início de período)
 *
 * @author Thiago
 * @since 1.0.0
 */
enum class TipoPonto(
    val descricao: String,
    val isEntrada: Boolean
) {
    /** Início de período de trabalho */
    ENTRADA("Entrada", true),

    /** Fim de período de trabalho */
    SAIDA("Saída", false);

    companion object {
        /**
         * Retorna o próximo tipo de ponto esperado baseado no último registrado.
         *
         * A lógica é simples: alterna entre ENTRADA e SAÍDA.
         * - Se não há ponto anterior: ENTRADA
         * - Se último foi ENTRADA: SAÍDA
         * - Se último foi SAÍDA: ENTRADA
         *
         * @param ultimoTipo Último tipo de ponto registrado (null se nenhum)
         * @return Próximo tipo esperado na sequência
         */
        fun getProximoTipo(ultimoTipo: TipoPonto?): TipoPonto {
            return when (ultimoTipo) {
                null -> ENTRADA
                ENTRADA -> SAIDA
                SAIDA -> ENTRADA
            }
        }

        /**
         * Valida se a quantidade de pontos está dentro do permitido.
         *
         * @param quantidade Quantidade de pontos registrados
         * @return true se está dentro do limite (2 a 10)
         */
        fun isQuantidadeValida(quantidade: Int): Boolean {
            return quantidade in MIN_PONTOS..MAX_PONTOS
        }

        /**
         * Verifica se a quantidade de pontos representa o cenário ideal.
         *
         * @param quantidade Quantidade de pontos registrados
         * @return true se é o cenário ideal (4 pontos)
         */
        fun isQuantidadeIdeal(quantidade: Int): Boolean {
            return quantidade == PONTOS_IDEAL
        }

        /** Quantidade mínima de pontos por dia */
        const val MIN_PONTOS = 2

        /** Quantidade ideal de pontos por dia (com intervalo de almoço) */
        const val PONTOS_IDEAL = 4

        /** Quantidade máxima de pontos por dia */
        const val MAX_PONTOS = 10
    }
}
