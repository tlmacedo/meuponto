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
 * As tolerâncias de entrada/saída são configuradas por dia da semana
 * em HorarioDiaSemana. A tolerância de intervalo pode vir do global
 * (ConfiguracaoEmprego) ou do dia específico.
 *
 * @author Thiago
 * @since 2.1.0
 * @updated 2.5.0 - Tolerâncias de entrada/saída agora vêm apenas de HorarioDiaSemana
 */
class ObterToleranciasEfetivasUseCase @Inject constructor(
    private val configuracaoEmpregoRepository: ConfiguracaoEmpregoRepository,
    private val horarioDiaSemanaRepository: HorarioDiaSemanaRepository
) {

    /**
     * Resultado contendo as tolerâncias efetivas calculadas.
     */
    data class ToleranciasEfetivas(
        val entradaMinutos: Int,
        val saidaMinutos: Int,
        val intervaloMaisMinutos: Int,
        val fonte: String
    ) {
        companion object {
            fun padrao() = ToleranciasEfetivas(
                entradaMinutos = 10,
                saidaMinutos = 10,
                intervaloMaisMinutos = 0,
                fonte = "Valores padrão do sistema"
            )
        }

        val descricao: String
            get() = "Entrada: ${entradaMinutos}min | Saída: ${saidaMinutos}min"
    }

    /**
     * Obtém as tolerâncias efetivas para um emprego em uma data específica.
     */
    suspend operator fun invoke(empregoId: Long, data: LocalDate): ToleranciasEfetivas {
        val diaSemana = DiaSemana.fromJavaDayOfWeek(data.dayOfWeek)
        return invoke(empregoId, diaSemana)
    }

    /**
     * Obtém as tolerâncias efetivas para um emprego em um dia da semana específico.
     */
    suspend operator fun invoke(empregoId: Long, diaSemana: DiaSemana): ToleranciasEfetivas {
        val configGlobal = configuracaoEmpregoRepository.buscarPorEmpregoId(empregoId)
        val configDia = horarioDiaSemanaRepository.buscarPorEmpregoEDia(empregoId, diaSemana)

        return calcularToleranciasEfetivas(configGlobal, configDia, diaSemana)
    }

    /**
     * Calcula tolerâncias usando objetos já carregados.
     */
    fun calcularToleranciasEfetivas(
        configGlobal: ConfiguracaoEmprego?,
        configDia: HorarioDiaSemana?,
        diaSemana: DiaSemana
    ): ToleranciasEfetivas {
        // Tolerâncias de entrada/saída vêm do dia, ou padrão se não configurado
        val entradaEfetiva = configDia?.toleranciaEntradaMinutos ?: 10
        val saidaEfetiva = configDia?.toleranciaSaidaMinutos ?: 10

        // Tolerância de intervalo: dia específico > global > padrão
        val intervaloMaisEfetivo = configDia?.toleranciaIntervaloMaisMinutos
            ?: configGlobal?.toleranciaIntervaloMaisMinutos
            ?: 0

        val fonte = when {
            configDia != null -> "Configuração de ${diaSemana.descricao}"
            else -> "Valores padrão"
        }

        return ToleranciasEfetivas(
            entradaMinutos = entradaEfetiva,
            saidaMinutos = saidaEfetiva,
            intervaloMaisMinutos = intervaloMaisEfetivo,
            fonte = fonte
        )
    }
}
