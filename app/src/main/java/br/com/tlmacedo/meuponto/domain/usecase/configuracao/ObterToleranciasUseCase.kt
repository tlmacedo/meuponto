// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/configuracao/ObterToleranciasUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.configuracao

import br.com.tlmacedo.meuponto.domain.model.DiaSemana
import br.com.tlmacedo.meuponto.domain.model.ToleranciasDia
import br.com.tlmacedo.meuponto.domain.repository.ConfiguracaoEmpregoRepository
import br.com.tlmacedo.meuponto.domain.repository.HorarioDiaSemanaRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Caso de uso para obter as tolerâncias efetivas para uma data específica.
 *
 * Aplica a lógica híbrida: usa tolerâncias específicas do dia se configuradas,
 * caso contrário usa as tolerâncias globais do emprego.
 *
 * @author Thiago
 * @since 2.1.0
 */
class ObterToleranciasUseCase @Inject constructor(
    private val configuracaoEmpregoRepository: ConfiguracaoEmpregoRepository,
    private val horarioDiaSemanaRepository: HorarioDiaSemanaRepository
) {
    /**
     * Obtém as tolerâncias efetivas para uma data específica.
     *
     * @param empregoId ID do emprego
     * @param data Data para obter as tolerâncias
     * @return Tolerâncias efetivas para o dia, ou null se emprego não encontrado
     */
    suspend operator fun invoke(empregoId: Long, data: LocalDate): ToleranciasDia? {
        // Busca configuração global do emprego
        val configuracao = configuracaoEmpregoRepository.buscarPorEmpregoId(empregoId)
            ?: return null

        // Determina o dia da semana
        val diaSemana = DiaSemana.fromJavaDayOfWeek(data.dayOfWeek)

        // Busca configuração específica do dia
        val horarioDia = horarioDiaSemanaRepository.buscarPorEmpregoEDia(empregoId, diaSemana)

        // Cria as tolerâncias efetivas combinando global + específico
        return ToleranciasDia.criar(configuracao, horarioDia)
    }

    /**
     * Obtém as tolerâncias efetivas para um dia da semana específico.
     *
     * @param empregoId ID do emprego
     * @param diaSemana Dia da semana
     * @return Tolerâncias efetivas para o dia, ou null se emprego não encontrado
     */
    suspend fun porDiaSemana(empregoId: Long, diaSemana: DiaSemana): ToleranciasDia? {
        val configuracao = configuracaoEmpregoRepository.buscarPorEmpregoId(empregoId)
            ?: return null

        val horarioDia = horarioDiaSemanaRepository.buscarPorEmpregoEDia(empregoId, diaSemana)

        return ToleranciasDia.criar(configuracao, horarioDia)
    }

    /**
     * Obtém as tolerâncias efetivas para hoje.
     *
     * @param empregoId ID do emprego
     * @return Tolerâncias efetivas para hoje
     */
    suspend fun paraHoje(empregoId: Long): ToleranciasDia? {
        return invoke(empregoId, LocalDate.now())
    }
}
