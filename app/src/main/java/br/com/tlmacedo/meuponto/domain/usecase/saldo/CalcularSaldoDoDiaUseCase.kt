// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/saldo/CalcularSaldoDoDiaUseCase.kt
package br.com.tlmacedo.meuponto.domain.usecase.saldo

import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoJornada
import br.com.tlmacedo.meuponto.domain.model.RegistroDiario
import br.com.tlmacedo.meuponto.domain.model.SaldoHoras
import br.com.tlmacedo.meuponto.domain.repository.PontoRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Caso de uso para calcular o saldo de horas de um dia específico.
 *
 * Busca os pontos do dia informado e calcula a diferença entre as horas
 * efetivamente trabalhadas e a carga horária esperada, retornando o saldo
 * em formato estruturado.
 *
 * @property repository Repositório de pontos para busca dos registros
 *
 * @author Thiago
 * @since 1.0.0
 */
class CalcularSaldoDoDiaUseCase @Inject constructor(
    private val repository: PontoRepository
) {
    /**
     * Calcula o saldo de horas do dia especificado.
     *
     * @param data Data para calcular o saldo (padrão: hoje)
     * @param configuracao Configuração de jornada a ser utilizada (padrão: configuração padrão)
     * @return [SaldoHoras] calculado, ou null se não houver pontos suficientes para cálculo
     */
    suspend operator fun invoke(
        data: LocalDate = LocalDate.now(),
        configuracao: ConfiguracaoJornada = ConfiguracaoJornada.default()
    ): SaldoHoras? {
        // Busca os pontos do dia
        val pontos = repository.buscarPontosPorData(data)
        
        // Se não há pontos, retorna null
        if (pontos.isEmpty()) {
            return null
        }

        // Cria o registro diário com as configurações
        val registroDiario = RegistroDiario(
            data = data,
            pontos = pontos,
            cargaHorariaDiariaMinutos = configuracao.cargaHorariaDiariaMinutos,
            intervaloMinimoMinutos = configuracao.intervaloMinimoMinutos,
            toleranciaMinutos = configuracao.toleranciaMinutos,
            jornadaMaximaMinutos = configuracao.jornadaMaximaDiariaMinutos
        )

        // Calcula o saldo em minutos
        val saldoMinutos = registroDiario.calcularSaldoMinutos() ?: return null
        
        return SaldoHoras(saldoMinutos)
    }
}
