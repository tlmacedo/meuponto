package br.com.tlmacedo.meuponto.presentation.screen.settings.empregos.editar

import br.com.tlmacedo.meuponto.domain.model.DiaSemana
import br.com.tlmacedo.meuponto.domain.model.TipoNsr
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Duration

/**
 * Testes unitários para [EditarEmpregoUiState].
 */
class EditarEmpregoUiStateTest {

    @Test
    fun `estado inicial deve ter valores padrao corretos`() {
        val state = EditarEmpregoUiState()

        assertThat(state.empregoId).isNull()
        assertThat(state.isNovoEmprego).isTrue()
        assertThat(state.isLoading).isFalse()
        assertThat(state.isSaving).isFalse()
        assertThat(state.nome).isEmpty()
        assertThat(state.nomeErro).isNull()
    }

    @Test
    fun `cargaHorariaDiaria padrao deve ser 8 horas`() {
        val state = EditarEmpregoUiState()
        assertThat(state.cargaHorariaDiaria).isEqualTo(Duration.ofHours(8))
    }

    @Test
    fun `tolerancias padrao devem estar corretas`() {
        val state = EditarEmpregoUiState()
        assertThat(state.toleranciaEntradaMinutos).isEqualTo(10)
        assertThat(state.toleranciaSaidaMinutos).isEqualTo(10)
        assertThat(state.toleranciaIntervaloMinutos).isEqualTo(5)
    }

    @Test
    fun `NSR deve estar desabilitado por padrao`() {
        val state = EditarEmpregoUiState()
        assertThat(state.habilitarNsr).isFalse()
        assertThat(state.tipoNsr).isEqualTo(TipoNsr.NUMERICO)
    }

    @Test
    fun `localizacao deve estar desabilitada por padrao`() {
        val state = EditarEmpregoUiState()
        assertThat(state.habilitarLocalizacao).isFalse()
        assertThat(state.localizacaoAutomatica).isFalse()
    }

    @Test
    fun `primeiroDiaSemana padrao deve ser SEGUNDA`() {
        val state = EditarEmpregoUiState()
        assertThat(state.primeiroDiaSemana).isEqualTo(DiaSemana.SEGUNDA)
    }

    @Test
    fun `formularioValido deve retornar false quando nome esta vazio`() {
        val state = EditarEmpregoUiState(nome = "")
        assertThat(state.formularioValido).isFalse()
    }

    @Test
    fun `formularioValido deve retornar false quando nome esta em branco`() {
        val state = EditarEmpregoUiState(nome = "   ")
        assertThat(state.formularioValido).isFalse()
    }

    @Test
    fun `formularioValido deve retornar false quando ha erro no nome`() {
        val state = EditarEmpregoUiState(nome = "Teste", nomeErro = "Erro")
        assertThat(state.formularioValido).isFalse()
    }

    @Test
    fun `formularioValido deve retornar true quando nome valido e sem erros`() {
        val state = EditarEmpregoUiState(nome = "Empresa Teste", nomeErro = null)
        assertThat(state.formularioValido).isTrue()
    }

    @Test
    fun `tituloTela deve ser Novo Emprego quando isNovoEmprego true`() {
        val state = EditarEmpregoUiState(isNovoEmprego = true)
        assertThat(state.tituloTela).isEqualTo("Novo Emprego")
    }

    @Test
    fun `tituloTela deve ser Editar Emprego quando isNovoEmprego false`() {
        val state = EditarEmpregoUiState(isNovoEmprego = false)
        assertThat(state.tituloTela).isEqualTo("Editar Emprego")
    }

    @Test
    fun `temBancoHoras deve retornar false quando periodo eh 0`() {
        val state = EditarEmpregoUiState(periodoBancoHorasMeses = 0)
        assertThat(state.temBancoHoras).isFalse()
    }

    @Test
    fun `temBancoHoras deve retornar true quando periodo maior que 0`() {
        val state = EditarEmpregoUiState(periodoBancoHorasMeses = 6)
        assertThat(state.temBancoHoras).isTrue()
    }

    @Test
    fun `descricaoPeriodoBancoHoras deve retornar Desabilitado quando 0`() {
        val state = EditarEmpregoUiState(periodoBancoHorasMeses = 0)
        assertThat(state.descricaoPeriodoBancoHoras).isEqualTo("Desabilitado")
    }

    @Test
    fun `descricaoPeriodoBancoHoras deve retornar 1 mes quando 1`() {
        val state = EditarEmpregoUiState(periodoBancoHorasMeses = 1)
        assertThat(state.descricaoPeriodoBancoHoras).isEqualTo("1 mês")
    }

    @Test
    fun `descricaoPeriodoBancoHoras deve retornar X meses quando maior que 1`() {
        val state = EditarEmpregoUiState(periodoBancoHorasMeses = 12)
        assertThat(state.descricaoPeriodoBancoHoras).isEqualTo("12 meses")
    }

    @Test
    fun `copy deve manter valores nao alterados`() {
        val original = EditarEmpregoUiState(
            nome = "Empresa Original",
            toleranciaEntradaMinutos = 15
        )
        val copia = original.copy(nome = "Novo Nome")

        assertThat(copia.nome).isEqualTo("Novo Nome")
        assertThat(copia.toleranciaEntradaMinutos).isEqualTo(15)
    }
}
