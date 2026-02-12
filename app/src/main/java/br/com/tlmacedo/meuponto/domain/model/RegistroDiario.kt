// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/RegistroDiario.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.Duration
import java.time.LocalDate

/**
 * Modelo que representa o resumo de um dia de trabalho.
 *
 * Agrupa todos os pontos de um dia e fornece métodos para cálculo
 * de horas trabalhadas, saldo, intervalos e validação de consistência.
 *
 * @property data Data do registro
 * @property pontos Lista de pontos do dia (pode estar vazia)
 * @property cargaHorariaDiariaMinutos Carga horária esperada em minutos (padrão: 480 = 8h)
 * @property intervaloMinimoMinutos Intervalo mínimo obrigatório em minutos (padrão: 60 = 1h)
 * @property toleranciaMinutos Tolerância permitida em minutos (padrão: 10)
 * @property jornadaMaximaMinutos Jornada máxima permitida em minutos (padrão: 600 = 10h)
 *
 * @author Thiago
 * @since 1.0.0
 */
data class RegistroDiario(
    val data: LocalDate,
    val pontos: List<Ponto> = emptyList(),
    val cargaHorariaDiariaMinutos: Int = 480,
    val intervaloMinimoMinutos: Int = 60,
    val toleranciaMinutos: Int = 10,
    val jornadaMaximaMinutos: Int = 600
) {
    /**
     * Retorna os pontos ordenados por data/hora.
     */
    val pontosOrdenados: List<Ponto>
        get() = pontos.sortedBy { it.dataHora }

    /**
     * Quantidade de pontos registrados no dia.
     */
    val quantidadePontos: Int
        get() = pontos.size

    /**
     * Primeiro ponto do dia (primeira entrada).
     */
    val primeiraEntrada: Ponto?
        get() = pontosOrdenados.firstOrNull { it.isEntrada }

    /**
     * Último ponto do dia.
     */
    val ultimoPonto: Ponto?
        get() = pontosOrdenados.lastOrNull()

    /**
     * Última saída registrada no dia.
     */
    val ultimaSaida: Ponto?
        get() = pontosOrdenados.lastOrNull { it.isSaida }

    /**
     * Verifica se o dia possui o cenário ideal de 4 pontos.
     */
    val isCenarioIdeal: Boolean
        get() = quantidadePontos == TipoPonto.PONTOS_IDEAL

    /**
     * Verifica se a quantidade de pontos é par (consistente).
     */
    val isQuantidadePar: Boolean
        get() = quantidadePontos % 2 == 0

    /**
     * Calcula o total de minutos trabalhados no dia.
     *
     * Soma os períodos entre cada par de ENTRADA/SAÍDA consecutivos.
     *
     * @return Total de minutos trabalhados, ou null se não houver pontos suficientes
     */
    fun calcularMinutosTrabalhados(): Int? {
        // Precisa de pelo menos 2 pontos (1 entrada + 1 saída)
        if (quantidadePontos < TipoPonto.MIN_PONTOS) return null

        // Quantidade ímpar indica inconsistência
        if (!isQuantidadePar) return null

        val ordenados = pontosOrdenados
        var totalMinutos = 0L

        // Processa pares de pontos (entrada, saída)
        var i = 0
        while (i < ordenados.size - 1) {
            val entrada = ordenados[i]
            val saida = ordenados[i + 1]

            // Valida que é um par válido (entrada seguida de saída)
            if (!entrada.isEntrada || !saida.isSaida) {
                return null
            }

            val duracao = Duration.between(entrada.dataHora, saida.dataHora)
            totalMinutos += duracao.toMinutes()
            
            i += 2
        }

        return totalMinutos.toInt()
    }

    /**
     * Calcula o total de minutos de intervalo no dia.
     *
     * Soma os períodos entre cada SAÍDA e a próxima ENTRADA.
     *
     * @return Total de minutos de intervalo, ou null se não aplicável
     */
    fun calcularMinutosIntervalo(): Int? {
        // Precisa de pelo menos 4 pontos para ter intervalo
        if (quantidadePontos < TipoPonto.PONTOS_IDEAL) return null

        val ordenados = pontosOrdenados
        var totalIntervalo = 0L

        // Processa intervalos entre saída e próxima entrada
        var i = 1
        while (i < ordenados.size - 1) {
            val saida = ordenados[i]
            val entrada = ordenados[i + 1]

            // Valida que é um intervalo válido (saída seguida de entrada)
            if (!saida.isSaida || !entrada.isEntrada) {
                i += 1
                continue
            }

            val duracao = Duration.between(saida.dataHora, entrada.dataHora)
            totalIntervalo += duracao.toMinutes()
            
            i += 2
        }

        return totalIntervalo.toInt()
    }

    /**
     * Calcula o saldo do dia em minutos.
     *
     * @return Saldo em minutos (positivo = crédito, negativo = débito), ou null se não calculável
     */
    fun calcularSaldoMinutos(): Int? {
        val trabalhado = calcularMinutosTrabalhados() ?: return null
        return trabalhado - cargaHorariaDiariaMinutos
    }

    /**
     * Determina o status de consistência do dia.
     *
     * @return StatusDia indicando a situação dos registros
     */
    fun determinarStatus(): StatusDia {
        // Sem registros
        if (pontos.isEmpty()) {
            return StatusDia.SEM_REGISTRO
        }

        // Excesso de pontos
        if (quantidadePontos > TipoPonto.MAX_PONTOS) {
            return StatusDia.EXCESSO_PONTOS
        }

        // Quantidade ímpar (incompleto)
        if (!isQuantidadePar) {
            // Se só tem entrada, está em andamento
            return if (quantidadePontos == 1 && pontosOrdenados.first().isEntrada) {
                StatusDia.EM_ANDAMENTO
            } else {
                StatusDia.INCOMPLETO
            }
        }

        // Valida sequência (deve alternar entrada/saída)
        val ordenados = pontosOrdenados
        for (i in ordenados.indices) {
            val esperaEntrada = i % 2 == 0
            val ponto = ordenados[i]
            if (ponto.isEntrada != esperaEntrada) {
                return StatusDia.SEQUENCIA_INVALIDA
            }
        }

        // Calcula minutos trabalhados
        val minutosTrabalhados = calcularMinutosTrabalhados()
        if (minutosTrabalhados == null) {
            return StatusDia.INCOMPLETO
        }

        // Verifica jornada máxima
        if (minutosTrabalhados > jornadaMaximaMinutos) {
            return StatusDia.JORNADA_EXCEDIDA
        }

        // Verifica intervalo mínimo (se tem 4+ pontos)
        if (quantidadePontos >= TipoPonto.PONTOS_IDEAL) {
            val minutosIntervalo = calcularMinutosIntervalo() ?: 0
            val intervaloMinimoComTolerancia = intervaloMinimoMinutos - toleranciaMinutos
            if (minutosIntervalo < intervaloMinimoComTolerancia) {
                return StatusDia.INTERVALO_INSUFICIENTE
            }
        }

        // Dia válido
        return if (quantidadePontos == 2) {
            StatusDia.COMPLETO_SEM_INTERVALO
        } else {
            StatusDia.COMPLETO
        }
    }

    /**
     * Retorna o próximo tipo de ponto esperado.
     */
    val proximoPontoEsperado: TipoPonto
        get() = TipoPonto.getProximoTipo(ultimoPonto?.tipo)

    /**
     * Verifica se ainda é possível registrar mais pontos no dia.
     */
    val podeRegistrarMaisPontos: Boolean
        get() = quantidadePontos < TipoPonto.MAX_PONTOS
}
