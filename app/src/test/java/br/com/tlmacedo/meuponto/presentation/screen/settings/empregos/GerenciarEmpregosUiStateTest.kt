package br.com.tlmacedo.meuponto.presentation.screen.settings.empregos

import br.com.tlmacedo.meuponto.domain.model.Emprego
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Testes unitários para [GerenciarEmpregosUiState].
 */
class GerenciarEmpregosUiStateTest {

    @Test
    fun `estado inicial deve ter valores padrao corretos`() {
        val state = GerenciarEmpregosUiState()

        assertThat(state.empregos).isEmpty()
        assertThat(state.empregosArquivados).isEmpty()
        assertThat(state.empregoAtivoId).isNull()
        assertThat(state.isLoading).isTrue()
        assertThat(state.mostrarArquivados).isFalse()
        assertThat(state.dialogConfirmacaoExclusao).isNull()
    }

    @Test
    fun `copy deve manter valores nao alterados`() {
        val emprego = Emprego(id = 1L, nome = "Teste")
        val original = GerenciarEmpregosUiState(
            empregos = listOf(emprego),
            isLoading = false
        )

        val copia = original.copy(mostrarArquivados = true)

        assertThat(copia.empregos).hasSize(1)
        assertThat(copia.isLoading).isFalse()
        assertThat(copia.mostrarArquivados).isTrue()
    }

    @Test
    fun `dialogConfirmacaoExclusao deve aceitar emprego`() {
        val emprego = Emprego(id = 1L, nome = "Teste")
        val state = GerenciarEmpregosUiState(dialogConfirmacaoExclusao = emprego)

        assertThat(state.dialogConfirmacaoExclusao).isNotNull()
        assertThat(state.dialogConfirmacaoExclusao?.nome).isEqualTo("Teste")
    }
}
