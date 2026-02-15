// Arquivo: app/src/test/java/br/com/tlmacedo/meuponto/domain/usecase/emprego/ObterEmpregoComConfiguracaoUseCaseTest.kt
package br.com.tlmacedo.meuponto.domain.usecase.emprego

import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import br.com.tlmacedo.meuponto.domain.model.DiaSemana
import br.com.tlmacedo.meuponto.domain.model.Emprego
import br.com.tlmacedo.meuponto.domain.repository.ConfiguracaoEmpregoRepository
import br.com.tlmacedo.meuponto.domain.repository.EmpregoRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [ObterEmpregoComConfiguracaoUseCase].
 *
 * @author Thiago
 * @since 2.0.0
 */
class ObterEmpregoComConfiguracaoUseCaseTest {

    private lateinit var empregoRepository: EmpregoRepository
    private lateinit var configuracaoEmpregoRepository: ConfiguracaoEmpregoRepository
    private lateinit var useCase: ObterEmpregoComConfiguracaoUseCase

    private val empregoMock = Emprego(
        id = 1L,
        nome = "Empresa Teste",
        descricao = "Descrição teste",
        ativo = true,
        arquivado = false,
        ordem = 0
    )

    private val configuracaoMock = ConfiguracaoEmprego(
        id = 1L,
        empregoId = 1L,
        jornadaMaximaDiariaMinutos = 600,
        intervaloMinimoInterjornadaMinutos = 660,
        primeiroDiaSemana = DiaSemana.SEGUNDA,
        primeiroDiaMes = 1
    )

    @Before
    fun setup() {
        empregoRepository = mockk()
        configuracaoEmpregoRepository = mockk()
        useCase = ObterEmpregoComConfiguracaoUseCase(
            empregoRepository,
            configuracaoEmpregoRepository
        )
    }

    @Test
    fun `deve retornar sucesso quando emprego e configuracao existem`() = runTest {
        // Given
        coEvery { empregoRepository.buscarPorId(1L) } returns empregoMock
        coEvery { configuracaoEmpregoRepository.buscarPorEmpregoId(1L) } returns configuracaoMock

        // When
        val resultado = useCase(1L)

        // Then
        assertThat(resultado).isInstanceOf(ObterEmpregoComConfiguracaoUseCase.Resultado.Sucesso::class.java)
        val sucesso = resultado as ObterEmpregoComConfiguracaoUseCase.Resultado.Sucesso
        assertThat(sucesso.emprego).isEqualTo(empregoMock)
        assertThat(sucesso.configuracao).isEqualTo(configuracaoMock)

        coVerify(exactly = 1) { empregoRepository.buscarPorId(1L) }
        coVerify(exactly = 1) { configuracaoEmpregoRepository.buscarPorEmpregoId(1L) }
    }

    @Test
    fun `deve retornar sucesso com configuracao padrao quando configuracao nao existe`() = runTest {
        // Given
        coEvery { empregoRepository.buscarPorId(1L) } returns empregoMock
        coEvery { configuracaoEmpregoRepository.buscarPorEmpregoId(1L) } returns null

        // When
        val resultado = useCase(1L)

        // Then
        assertThat(resultado).isInstanceOf(ObterEmpregoComConfiguracaoUseCase.Resultado.Sucesso::class.java)
        val sucesso = resultado as ObterEmpregoComConfiguracaoUseCase.Resultado.Sucesso
        assertThat(sucesso.emprego).isEqualTo(empregoMock)
        assertThat(sucesso.configuracao.empregoId).isEqualTo(1L)
    }

    @Test
    fun `deve retornar NaoEncontrado quando emprego nao existe`() = runTest {
        // Given
        coEvery { empregoRepository.buscarPorId(999L) } returns null

        // When
        val resultado = useCase(999L)

        // Then
        assertThat(resultado).isInstanceOf(ObterEmpregoComConfiguracaoUseCase.Resultado.NaoEncontrado::class.java)
        coVerify(exactly = 1) { empregoRepository.buscarPorId(999L) }
        coVerify(exactly = 0) { configuracaoEmpregoRepository.buscarPorEmpregoId(any()) }
    }

    @Test
    fun `deve retornar Erro quando repositorio lanca excecao`() = runTest {
        // Given
        coEvery { empregoRepository.buscarPorId(1L) } throws RuntimeException("Erro de banco")

        // When
        val resultado = useCase(1L)

        // Then
        assertThat(resultado).isInstanceOf(ObterEmpregoComConfiguracaoUseCase.Resultado.Erro::class.java)
        val erro = resultado as ObterEmpregoComConfiguracaoUseCase.Resultado.Erro
        assertThat(erro.mensagem).contains("Erro ao obter emprego")
    }

    @Test
    fun `deve retornar Erro quando repositorio de configuracao lanca excecao`() = runTest {
        // Given
        coEvery { empregoRepository.buscarPorId(1L) } returns empregoMock
        coEvery { configuracaoEmpregoRepository.buscarPorEmpregoId(1L) } throws RuntimeException("Erro de banco")

        // When
        val resultado = useCase(1L)

        // Then
        assertThat(resultado).isInstanceOf(ObterEmpregoComConfiguracaoUseCase.Resultado.Erro::class.java)
    }
}
