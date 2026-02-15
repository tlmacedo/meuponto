// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/repository/HorarioPadraoRepositoryImpl.kt
package br.com.tlmacedo.meuponto.data.repository

import br.com.tlmacedo.meuponto.domain.model.DiaSemana
import br.com.tlmacedo.meuponto.domain.model.HorarioDiaSemana
import br.com.tlmacedo.meuponto.domain.model.HorarioPadrao
import br.com.tlmacedo.meuponto.domain.repository.HorarioDiaSemanaRepository
import br.com.tlmacedo.meuponto.domain.repository.HorarioPadraoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de horários padrão.
 *
 * Adapta o [HorarioDiaSemanaRepository] existente para a interface
 * [HorarioPadraoRepository], convertendo entre os modelos de domínio.
 *
 * @property horarioDiaSemanaRepository Repositório delegado para operações de banco
 *
 * @author Thiago
 * @since 2.0.0
 */
@Singleton
class HorarioPadraoRepositoryImpl @Inject constructor(
    private val horarioDiaSemanaRepository: HorarioDiaSemanaRepository
) : HorarioPadraoRepository {

    override suspend fun inserir(horarioPadrao: HorarioPadrao): Long {
        return horarioDiaSemanaRepository.inserir(horarioPadrao.toHorarioDiaSemana())
    }

    override suspend fun atualizar(horarioPadrao: HorarioPadrao) {
        horarioDiaSemanaRepository.atualizar(horarioPadrao.toHorarioDiaSemana())
    }

    override suspend fun excluir(horarioPadrao: HorarioPadrao) {
        horarioDiaSemanaRepository.excluir(horarioPadrao.toHorarioDiaSemana())
    }

    override suspend fun buscarPorId(id: Long): HorarioPadrao? {
        return horarioDiaSemanaRepository.buscarPorId(id)?.toHorarioPadrao()
    }

    override suspend fun buscarPorEmprego(empregoId: Long): List<HorarioPadrao> {
        return horarioDiaSemanaRepository.buscarPorEmprego(empregoId)
            .map { it.toHorarioPadrao() }
    }

    override suspend fun buscarPorEmpregoEDiaSemana(empregoId: Long, diaSemana: Int): HorarioPadrao? {
        val dia = intToDiaSemana(diaSemana) ?: return null
        return horarioDiaSemanaRepository.buscarPorEmpregoEDia(empregoId, dia)?.toHorarioPadrao()
    }

    override fun observarPorEmprego(empregoId: Long): Flow<List<HorarioPadrao>> {
        return horarioDiaSemanaRepository.observarPorEmprego(empregoId)
            .map { list -> list.map { it.toHorarioPadrao() } }
    }

    override suspend fun excluirPorEmpregoId(empregoId: Long) {
        horarioDiaSemanaRepository.excluirPorEmprego(empregoId)
    }

    override suspend fun upsert(horarioPadrao: HorarioPadrao): Long {
        val existing = buscarPorEmpregoEDiaSemana(horarioPadrao.empregoId, horarioPadrao.diaSemana)
        return if (existing != null) {
            atualizar(horarioPadrao.copy(id = existing.id))
            existing.id
        } else {
            inserir(horarioPadrao)
        }
    }

    // ========================================================================
    // Funções de Conversão
    // ========================================================================

    /**
     * Converte Int (1-7) para DiaSemana enum.
     * 1=Segunda, 7=Domingo (padrão ISO)
     */
    private fun intToDiaSemana(value: Int): DiaSemana? {
        return when (value) {
            1 -> DiaSemana.SEGUNDA
            2 -> DiaSemana.TERCA
            3 -> DiaSemana.QUARTA
            4 -> DiaSemana.QUINTA
            5 -> DiaSemana.SEXTA
            6 -> DiaSemana.SABADO
            7 -> DiaSemana.DOMINGO
            else -> null
        }
    }

    /**
     * Converte DiaSemana enum para Int (1-7).
     */
    private fun diaSemanaToInt(dia: DiaSemana): Int {
        return when (dia) {
            DiaSemana.SEGUNDA -> 1
            DiaSemana.TERCA -> 2
            DiaSemana.QUARTA -> 3
            DiaSemana.QUINTA -> 4
            DiaSemana.SEXTA -> 5
            DiaSemana.SABADO -> 6
            DiaSemana.DOMINGO -> 7
        }
    }

    /**
     * Converte HorarioPadrao para HorarioDiaSemana.
     */
    private fun HorarioPadrao.toHorarioDiaSemana(): HorarioDiaSemana {
        val dia = intToDiaSemana(this.diaSemana) ?: DiaSemana.SEGUNDA
        return HorarioDiaSemana(
            id = this.id,
            empregoId = this.empregoId,
            diaSemana = dia,
            ativo = this.isDiaUtil,
            cargaHorariaMinutos = this.jornadaMinutos,
            entradaIdeal = this.horaEntrada,
            saidaIntervaloIdeal = this.horaSaidaAlmoco,
            voltaIntervaloIdeal = this.horaRetornoAlmoco,
            saidaIdeal = this.horaSaida,
            criadoEm = this.criadoEm,
            atualizadoEm = this.atualizadoEm
        )
    }

    /**
     * Converte HorarioDiaSemana para HorarioPadrao.
     */
    private fun HorarioDiaSemana.toHorarioPadrao(): HorarioPadrao {
        return HorarioPadrao(
            id = this.id,
            empregoId = this.empregoId,
            diaSemana = diaSemanaToInt(this.diaSemana),
            horaEntrada = this.entradaIdeal,
            horaSaidaAlmoco = this.saidaIntervaloIdeal,
            horaRetornoAlmoco = this.voltaIntervaloIdeal,
            horaSaida = this.saidaIdeal,
            jornadaMinutos = this.cargaHorariaMinutos,
            isDiaUtil = this.ativo,
            criadoEm = this.criadoEm,
            atualizadoEm = this.atualizadoEm
        )
    }
}
