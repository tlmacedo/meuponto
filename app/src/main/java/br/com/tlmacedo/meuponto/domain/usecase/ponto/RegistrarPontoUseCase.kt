// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/ponto/RegistrarPontoUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.ponto

import br.com.tlmacedo.meuponto.domain.model.Ponto
import br.com.tlmacedo.meuponto.domain.model.TipoPonto
import br.com.tlmacedo.meuponto.domain.repository.PontoRepository
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Caso de uso para registrar uma nova batida de ponto.
 *
 * Responsável por validar e inserir um novo registro de ponto no sistema,
 * determinando automaticamente o tipo de batida (ENTRADA ou SAÍDA) quando
 * não especificado, baseado no último ponto registrado no dia.
 *
 * Validações realizadas:
 * - Não permite registro de ponto no futuro
 * - Verifica limite máximo de pontos por dia (10)
 * - Alterna automaticamente entre ENTRADA e SAÍDA
 *
 * @property repository Repositório de pontos para operações de persistência
 *
 * @author Thiago
 * @since 1.0.0
 */
class RegistrarPontoUseCase @Inject constructor(
    private val repository: PontoRepository
) {
    /**
     * Registra uma nova batida de ponto.
     *
     * @param dataHora Data e hora da batida (padrão: momento atual)
     * @param tipo Tipo da batida (padrão: determinado automaticamente)
     * @param observacao Observação opcional sobre o registro
     * @return [Result] contendo o [Ponto] criado em caso de sucesso, ou erro
     */
    suspend operator fun invoke(
        dataHora: LocalDateTime = LocalDateTime.now(),
        tipo: TipoPonto? = null,
        observacao: String? = null
    ): Result<Ponto> {
        return try {
            // Validação: não permite ponto no futuro
            if (dataHora.isAfter(LocalDateTime.now())) {
                return Result.failure(
                    IllegalArgumentException("Não é permitido registrar ponto no futuro")
                )
            }

            // Verifica quantidade de pontos do dia
            val pontosHoje = repository.buscarPontosPorData(dataHora.toLocalDate())
            if (pontosHoje.size >= TipoPonto.MAX_PONTOS) {
                return Result.failure(
                    IllegalStateException("Limite máximo de ${TipoPonto.MAX_PONTOS} pontos por dia atingido")
                )
            }

            // Determina o tipo automaticamente se não especificado
            val tipoFinal = tipo ?: determinarTipoAutomatico(dataHora)

            // Cria o ponto
            val ponto = Ponto(
                dataHora = dataHora,
                tipo = tipoFinal,
                observacao = observacao
            )

            // Persiste no banco
            val id = repository.inserir(ponto)
            val pontoSalvo = ponto.copy(id = id)
            
            Timber.d("Ponto registrado com sucesso: $pontoSalvo")
            Result.success(pontoSalvo)

        } catch (e: Exception) {
            Timber.e(e, "Erro ao registrar ponto")
            Result.failure(e)
        }
    }

    /**
     * Determina automaticamente o tipo de ponto baseado no último registro do dia.
     *
     * @param dataHora Data e hora do novo ponto
     * @return [TipoPonto] esperado (alterna entre ENTRADA e SAÍDA)
     */
    private suspend fun determinarTipoAutomatico(dataHora: LocalDateTime): TipoPonto {
        val ultimoPonto = repository.buscarUltimoPontoDoDia(dataHora.toLocalDate())
        return TipoPonto.getProximoTipo(ultimoPonto?.tipo)
    }
}
