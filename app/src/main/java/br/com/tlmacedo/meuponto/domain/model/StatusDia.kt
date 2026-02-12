// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/StatusDia.kt
package br.com.tlmacedo.meuponto.domain.model

/**
 * Enum que representa o status de consistência de um dia de trabalho.
 *
 * Utilizado para identificar se os registros de ponto de um dia
 * estão completos, incompletos ou com alguma inconsistência.
 *
 * @property descricao Descrição em português para exibição na interface
 * @property isConsistente Indica se o status representa um dia válido
 *
 * @author Thiago
 * @since 1.0.0
 */
enum class StatusDia(
    val descricao: String,
    val isConsistente: Boolean
) {
    /** Dia com 4 pontos na sequência correta e dentro da tolerância */
    COMPLETO("Completo", true),

    /** Dia com apenas entrada registrada, aguardando demais pontos */
    EM_ANDAMENTO("Em andamento", true),

    /** Dia com 2 pontos válidos (entrada + saída, sem intervalo) */
    COMPLETO_SEM_INTERVALO("Completo sem intervalo", true),

    /** Dia com quantidade ímpar de pontos (falta entrada ou saída) */
    INCOMPLETO("Incompleto", false),

    /** Dia com pontos fora da sequência esperada */
    SEQUENCIA_INVALIDA("Sequência inválida", false),

    /** Dia com mais de 10 pontos registrados */
    EXCESSO_PONTOS("Excesso de pontos", false),

    /** Dia com intervalo menor que o mínimo permitido */
    INTERVALO_INSUFICIENTE("Intervalo insuficiente", false),

    /** Dia com jornada excedendo o limite máximo */
    JORNADA_EXCEDIDA("Jornada excedida", false),

    /** Dia sem nenhum registro de ponto */
    SEM_REGISTRO("Sem registro", false);

    companion object {
        /**
         * Retorna todos os status que indicam inconsistência.
         *
         * @return Lista de status inconsistentes
         */
        fun getStatusInconsistentes(): List<StatusDia> {
            return entries.filter { !it.isConsistente }
        }
    }
}
