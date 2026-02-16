// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/configuracao/ObterToleranciasEfetivasUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.configuracao

import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import br.com.tlmacedo.meuponto.domain.model.DiaSemana
import br.com.tlmacedo.meuponto.domain.model.HorarioDiaSemana
import br.com.tlmacedo.meuponto.domain.repository.ConfiguracaoEmpregoRepository
import br.com.tlmacedo.meuponto.domain.repository.HorarioDiaSemanaRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Caso de uso para obter as tolerâncias efetivas para um dia específico.
 *
 * Implementa a lógica híbrida:
 * - Primeiro verifica se há tolerância específica para o dia da semana
 * - Se não houver (null), usa o valor global de ConfiguracaoEmprego
 *
 * @property configuracaoEmpregoRepository Repositório de configurações de emprego
 * @property horarioDiaSemanaRepository Repositório de horários por dia da semana
 *
 * @author Thiago
 * @since 2.1.0
 * @updated 2.3.1 - Removida tolerância de redução de intervalo
 */
class ObterToleranciasEfetivasUseCase @Inject constructor(
    private val configuracaoEmpregoRepository: ConfiguracaoEmpregoRepository,
    private val horarioDiaSemanaRepository: HorarioDiaSemanaRepository
) {

    /**
     * Resultado contendo as tolerâncias efetivas calculadas.
     *
     * @property entradaMinutos Tolerância de entrada em minutos
     * @property saidaMinutos Tolerância de saída em minutos
     * @property intervaloMaisMinutos Tolerância para intervalo maior que o esperado
     * @property usandoValoresEspecificos Indica se está usando valores específicos do dia
     * @property fonte Descrição da fonte dos valores (para debug/UI)
     */
    data class ToleranciasEfetivas(
        val entradaMinutos: Int,
        val saidaMinutos: Int,
        val intervaloMaisMinutos: Int,
        val usandoValoresEspecificos: Boolean,
        val fonte: String
    ) {
        companion object {
            /**
             * Cria tolerâncias com valores padrão (quando não há configuração).
             */
            fun padrao() = ToleranciasEfetivas(
                entradaMinutos = 10,
                saidaMinutos = 10,
                intervaloMaisMinutos = 0,
                usandoValoresEspecificos = false,
                fonte = "Valores padrão do sistema"
            )
        }

        /**
         * Descrição resumida das tolerâncias.
         */
        val descricao: String
            get() = "Entrada: ${entradaMinutos}min | Saída: ${saidaMinutos}min"
    }

    /**
     * Obtém as tolerâncias efetivas para um emprego em uma data específica.
     *
     * @param empregoId ID do emprego
     * @param data Data para determinar o dia da semana
     * @return ToleranciasEfetivas com os valores calculados
     */
    suspend operator fun invoke(empregoId: Long, data: LocalDate): ToleranciasEfetivas {
        // Converte a data para o dia da semana
        val diaSemana = DiaSemana.fromJavaDayOfWeek(data.dayOfWeek)
        return invoke(empregoId, diaSemana)
    }

    /**
     * Obtém as tolerâncias efetivas para um emprego em um dia da semana específico.
     *
     * @param empregoId ID do emprego
     * @param diaSemana Dia da semana
     * @return ToleranciasEfetivas com os valores calculados
     */
    suspend operator fun invoke(empregoId: Long, diaSemana: DiaSemana): ToleranciasEfetivas {
        // Busca configuração global do emprego
        val configGlobal = configuracaoEmpregoRepository.buscarPorEmpregoId(empregoId)
            ?: return ToleranciasEfetivas.padrao()

        // Busca configuração específica do dia
        val configDia = horarioDiaSemanaRepository.buscarPorEmpregoEDia(empregoId, diaSemana)

        return calcularToleranciasEfetivas(configGlobal, configDia, diaSemana)
    }

    /**
     * Obtém as tolerâncias efetivas usando objetos já carregados.
     * Útil quando já temos os dados em memória (evita queries adicionais).
     *
     * @param configGlobal Configuração global do emprego
     * @param configDia Configuração específica do dia (pode ser null)
     * @param diaSemana Dia da semana
     * @return ToleranciasEfetivas com os valores calculados
     */
    fun calcularToleranciasEfetivas(
        configGlobal: ConfiguracaoEmprego,
        configDia: HorarioDiaSemana?,
        diaSemana: DiaSemana
    ): ToleranciasEfetivas {
        // Se não há configuração específica do dia, usa tudo do global
        if (configDia == null) {
            return ToleranciasEfetivas(
                entradaMinutos = configGlobal.toleranciaEntradaMinutos,
                saidaMinutos = configGlobal.toleranciaSaidaMinutos,
                intervaloMaisMinutos = configGlobal.toleranciaIntervaloMaisMinutos,
                usandoValoresEspecificos = false,
                fonte = "Configuração global do emprego"
            )
        }

        // Aplica lógica híbrida: específico do dia se configurado, senão global
        val entradaEfetiva = configDia.toleranciaEntradaMinutos
            ?: configGlobal.toleranciaEntradaMinutos

        val saidaEfetiva = configDia.toleranciaSaidaMinutos
            ?: configGlobal.toleranciaSaidaMinutos

        // Tolerâncias de intervalo vêm do dia se configurado, senão do global
        val intervaloMaisEfetivo = configDia.toleranciaIntervaloMaisMinutos

        val usandoEspecificos = configDia.temToleranciasCustomizadas

        // Determina a fonte dos valores para exibição
        val fonte = when {
            configDia.temToleranciaEntradaCustomizada && configDia.temToleranciaSaidaCustomizada ->
                "Configuração específica de ${diaSemana.descricao}"
            configDia.temToleranciaEntradaCustomizada ->
                "Entrada: ${diaSemana.descricaoCurta} | Saída: Global"
            configDia.temToleranciaSaidaCustomizada ->
                "Entrada: Global | Saída: ${diaSemana.descricaoCurta}"
            else ->
                "Configuração global do emprego"
        }

        return ToleranciasEfetivas(
            entradaMinutos = entradaEfetiva,
            saidaMinutos = saidaEfetiva,
            intervaloMaisMinutos = intervaloMaisEfetivo,
            usandoValoresEspecificos = usandoEspecificos,
            fonte = fonte
        )
    }
}
