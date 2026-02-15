// Arquivo: app/src/test/java/br/com/tlmacedo/meuponto/domain/model/TipoNsrTest.kt
package br.com.tlmacedo.meuponto.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Testes unitários para o enum [TipoNsr].
 *
 * @author Thiago
 * @since 2.0.0
 */
class TipoNsrTest {

    @Test
    fun `deve ter exatamente 2 tipos de NSR`() {
        assertThat(TipoNsr.entries).hasSize(2)
    }

    @Test
    fun `NUMERICO deve existir`() {
        assertThat(TipoNsr.NUMERICO).isNotNull()
    }

    @Test
    fun `ALFANUMERICO deve existir`() {
        assertThat(TipoNsr.ALFANUMERICO).isNotNull()
    }

    @Test
    fun `NUMERICO deve ter ordinal 0`() {
        assertThat(TipoNsr.NUMERICO.ordinal).isEqualTo(0)
    }

    @Test
    fun `ALFANUMERICO deve ter ordinal 1`() {
        assertThat(TipoNsr.ALFANUMERICO.ordinal).isEqualTo(1)
    }

    @Test
    fun `valueOf deve retornar tipo correto para NUMERICO`() {
        assertThat(TipoNsr.valueOf("NUMERICO")).isEqualTo(TipoNsr.NUMERICO)
    }

    @Test
    fun `valueOf deve retornar tipo correto para ALFANUMERICO`() {
        assertThat(TipoNsr.valueOf("ALFANUMERICO")).isEqualTo(TipoNsr.ALFANUMERICO)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf deve lancar excecao para valor invalido`() {
        TipoNsr.valueOf("INVALIDO")
    }
}
