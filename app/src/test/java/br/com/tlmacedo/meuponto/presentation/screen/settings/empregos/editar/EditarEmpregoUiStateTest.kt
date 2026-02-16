package br.com.tlmacedo.meuponto.presentation.screen.settings.empregos.editar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration

/**
 * Testes unitários para EditarEmpregoUiState.
 */
class EditarEmpregoUiStateTest {

    @Test
    fun `estado inicial deve ter valores padrao corretos`() {
        val state = EditarEmpregoUiState()
        
        assertTrue(state.isNovoEmprego)
        assertEquals("", state.nome)
        assertEquals(Duration.ofMinutes(492), state.cargaHorariaDiaria)
        assertEquals(600, state.jornadaMaximaDiariaMinutos)
        assertEquals(60, state.intervaloMinimoMinutos)
        assertFalse(state.habilitarNsr)
        assertFalse(state.habilitarLocalizacao)
    }

    @Test
    fun `tituloTela deve retornar texto correto para novo emprego`() {
        val state = EditarEmpregoUiState(isNovoEmprego = true)
        assertEquals("Novo Emprego", state.tituloTela)
    }

    @Test
    fun `tituloTela deve retornar texto correto para edicao`() {
        val state = EditarEmpregoUiState(isNovoEmprego = false)
        assertEquals("Editar Emprego", state.tituloTela)
    }

    @Test
    fun `textoBotaoSalvar deve retornar texto correto para novo emprego`() {
        val state = EditarEmpregoUiState(isNovoEmprego = true)
        assertEquals("Criar Emprego", state.textoBotaoSalvar)
    }

    @Test
    fun `textoBotaoSalvar deve retornar texto correto para edicao`() {
        val state = EditarEmpregoUiState(isNovoEmprego = false)
        assertEquals("Salvar Alterações", state.textoBotaoSalvar)
    }

    @Test
    fun `formularioValido deve retornar false quando nome vazio`() {
        val state = EditarEmpregoUiState(nome = "")
        assertFalse(state.formularioValido)
    }

    @Test
    fun `formularioValido deve retornar false quando nome em branco`() {
        val state = EditarEmpregoUiState(nome = "   ")
        assertFalse(state.formularioValido)
    }

    @Test
    fun `formularioValido deve retornar true quando nome preenchido`() {
        val state = EditarEmpregoUiState(nome = "Meu Emprego")
        assertTrue(state.formularioValido)
    }

    @Test
    fun `formularioValido deve retornar false quando tem erro no nome`() {
        val state = EditarEmpregoUiState(nome = "Teste", nomeErro = "Nome muito curto")
        assertFalse(state.formularioValido)
    }

    @Test
    fun `cargaHorariaDiariaFormatada deve formatar corretamente`() {
        val state = EditarEmpregoUiState(cargaHorariaDiaria = Duration.ofMinutes(492))
        assertEquals("08:12", state.cargaHorariaDiariaFormatada)
    }

    @Test
    fun `cargaHorariaDiariaFormatada deve formatar 8 horas corretamente`() {
        val state = EditarEmpregoUiState(cargaHorariaDiaria = Duration.ofHours(8))
        assertEquals("08:00", state.cargaHorariaDiariaFormatada)
    }

    @Test
    fun `temBancoHoras deve retornar true quando periodo maior que zero`() {
        val state = EditarEmpregoUiState(periodoBancoHorasMeses = 6)
        assertTrue(state.temBancoHoras)
    }

    @Test
    fun `temBancoHoras deve retornar false quando periodo zero`() {
        val state = EditarEmpregoUiState(periodoBancoHorasMeses = 0)
        assertFalse(state.temBancoHoras)
    }
}
