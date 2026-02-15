// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/emprego/ObterEmpregoComConfiguracaoUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.emprego

import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import br.com.tlmacedo.meuponto.domain.model.Emprego
import br.com.tlmacedo.meuponto.domain.repository.ConfiguracaoEmpregoRepository
import br.com.tlmacedo.meuponto.domain.repository.EmpregoRepository
import javax.inject.Inject

/**
 * Caso de uso para obter um emprego junto com sua configuração.
 *
 * Retorna os dados completos do emprego incluindo todas as configurações
 * de jornada, tolerâncias, NSR e banco de horas.
 *
 * @author Thiago
 * @since 2.0.0
 */
class ObterEmpregoComConfiguracaoUseCase @Inject constructor(
    private val empregoRepository: EmpregoRepository,
    private val configuracaoEmpregoRepository: ConfiguracaoEmpregoRepository
) {
    /**
     * Resultado da operação.
     */
    sealed class Resultado {
        /**
         * Emprego e configuração obtidos com sucesso.
         *
         * @property emprego Dados do emprego
         * @property configuracao Configuração do emprego
         */
        data class Sucesso(
            val emprego: Emprego,
            val configuracao: ConfiguracaoEmprego
        ) : Resultado()

        /**
         * Emprego não encontrado.
         */
        data object NaoEncontrado : Resultado()

        /**
         * Erro ao obter emprego.
         *
         * @property mensagem Mensagem de erro
         */
        data class Erro(val mensagem: String) : Resultado()
    }

    /**
     * Obtém o emprego e sua configuração pelo ID.
     *
     * @param empregoId ID do emprego a ser obtido
     * @return [Resultado] da operação
     */
    suspend operator fun invoke(empregoId: Long): Resultado {
        return try {
            val emprego = empregoRepository.buscarPorId(empregoId)
                ?: return Resultado.NaoEncontrado

            val configuracao = configuracaoEmpregoRepository.buscarPorEmpregoId(empregoId)
                ?: ConfiguracaoEmprego(empregoId = empregoId) // Retorna config padrão se não existir

            Resultado.Sucesso(
                emprego = emprego,
                configuracao = configuracao
            )
        } catch (e: Exception) {
            Resultado.Erro("Erro ao obter emprego: ${e.message}")
        }
    }
}
