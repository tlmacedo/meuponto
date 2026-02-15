// Arquivo: app/src/test/java/br/com/tlmacedo/meuponto/domain/model/EmpregoTest.kt
package br.com.tlmacedo.meuponto.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime

/**
 * Testes unitários para o modelo [Emprego].
 *
 * @author Thiago
 * @since 2.0.0
 */
class EmpregoTest {

    @Test
    fun `emprego padrao deve ter valores default corretos`() {
        val emprego = Emprego(nome = "Teste")

        assertThat(emprego.id).isEqualTo(0)
        assertThat(emprego.nome).isEqualTo("Teste")
        assertThat(emprego.descricao).isNull()
        assertThat(emprego.ativo).isTrue()
        assertThat(emprego.arquivado).isFalse()
        assertThat(emprego.ordem).isEqualTo(0)
    }

    @Test
    fun `isVisivel deve retornar true quando ativo e nao arquivado`() {
        val emprego = Emprego(
            nome = "Teste",
            ativo = true,
            arquivado = false
        )

        assertThat(emprego.isVisivel).isTrue()
    }

    @Test
    fun `isVisivel deve retornar false quando inativo`() {
        val emprego = Emprego(
            nome = "Teste",
            ativo = false,
            arquivado = false
        )

        assertThat(emprego.isVisivel).isFalse()
    }

    @Test
    fun `isVisivel deve retornar false quando arquivado`() {
        val emprego = Emprego(
            nome = "Teste",
            ativo = true,
            arquivado = true
        )

        assertThat(emprego.isVisivel).isFalse()
    }

    @Test
    fun `isVisivel deve retornar false quando inativo e arquivado`() {
        val emprego = Emprego(
            nome = "Teste",
            ativo = false,
            arquivado = true
        )

        assertThat(emprego.isVisivel).isFalse()
    }

    @Test
    fun `podeRegistrarPonto deve retornar true quando ativo e nao arquivado`() {
        val emprego = Emprego(
            nome = "Teste",
            ativo = true,
            arquivado = false
        )

        assertThat(emprego.podeRegistrarPonto).isTrue()
    }

    @Test
    fun `podeRegistrarPonto deve retornar false quando inativo`() {
        val emprego = Emprego(
            nome = "Teste",
            ativo = false,
            arquivado = false
        )

        assertThat(emprego.podeRegistrarPonto).isFalse()
    }

    @Test
    fun `podeRegistrarPonto deve retornar false quando arquivado`() {
        val emprego = Emprego(
            nome = "Teste",
            ativo = true,
            arquivado = true
        )

        assertThat(emprego.podeRegistrarPonto).isFalse()
    }

    @Test
    fun `copy deve manter valores nao alterados`() {
        val original = Emprego(
            id = 1,
            nome = "Original",
            descricao = "Descrição",
            ativo = true,
            arquivado = false,
            ordem = 5
        )

        val copia = original.copy(nome = "Novo Nome")

        assertThat(copia.id).isEqualTo(1)
        assertThat(copia.nome).isEqualTo("Novo Nome")
        assertThat(copia.descricao).isEqualTo("Descrição")
        assertThat(copia.ativo).isTrue()
        assertThat(copia.arquivado).isFalse()
        assertThat(copia.ordem).isEqualTo(5)
    }

    @Test
    fun `dois empregos com mesmo conteudo devem ser iguais`() {
        val dataHora = LocalDateTime.of(2024, 1, 1, 10, 0)
        
        val emprego1 = Emprego(
            id = 1,
            nome = "Teste",
            criadoEm = dataHora,
            atualizadoEm = dataHora
        )
        
        val emprego2 = Emprego(
            id = 1,
            nome = "Teste",
            criadoEm = dataHora,
            atualizadoEm = dataHora
        )

        assertThat(emprego1).isEqualTo(emprego2)
    }

    @Test
    fun `emprego com descricao null e vazia sao diferentes`() {
        val empregoNull = Emprego(nome = "Teste", descricao = null)
        val empregoVazio = Emprego(nome = "Teste", descricao = "")

        assertThat(empregoNull.descricao).isNotEqualTo(empregoVazio.descricao)
    }
}
