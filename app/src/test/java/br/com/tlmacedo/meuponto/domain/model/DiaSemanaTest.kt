// Arquivo: app/src/test/java/br/com/tlmacedo/meuponto/domain/model/DiaSemanaTest.kt
package br.com.tlmacedo.meuponto.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.DayOfWeek

/**
 * Testes unitários para o enum [DiaSemana].
 *
 * @author Thiago
 * @since 2.0.0
 */
class DiaSemanaTest {

    @Test
    fun `deve ter 7 dias da semana`() {
        assertThat(DiaSemana.entries).hasSize(7)
    }

    @Test
    fun `SEGUNDA deve mapear para MONDAY`() {
        assertThat(DiaSemana.SEGUNDA.dayOfWeek).isEqualTo(DayOfWeek.MONDAY)
    }

    @Test
    fun `TERCA deve mapear para TUESDAY`() {
        assertThat(DiaSemana.TERCA.dayOfWeek).isEqualTo(DayOfWeek.TUESDAY)
    }

    @Test
    fun `QUARTA deve mapear para WEDNESDAY`() {
        assertThat(DiaSemana.QUARTA.dayOfWeek).isEqualTo(DayOfWeek.WEDNESDAY)
    }

    @Test
    fun `QUINTA deve mapear para THURSDAY`() {
        assertThat(DiaSemana.QUINTA.dayOfWeek).isEqualTo(DayOfWeek.THURSDAY)
    }

    @Test
    fun `SEXTA deve mapear para FRIDAY`() {
        assertThat(DiaSemana.SEXTA.dayOfWeek).isEqualTo(DayOfWeek.FRIDAY)
    }

    @Test
    fun `SABADO deve mapear para SATURDAY`() {
        assertThat(DiaSemana.SABADO.dayOfWeek).isEqualTo(DayOfWeek.SATURDAY)
    }

    @Test
    fun `DOMINGO deve mapear para SUNDAY`() {
        assertThat(DiaSemana.DOMINGO.dayOfWeek).isEqualTo(DayOfWeek.SUNDAY)
    }

    @Test
    fun `fromDayOfWeek deve converter MONDAY para SEGUNDA`() {
        val resultado = DiaSemana.fromDayOfWeek(DayOfWeek.MONDAY)
        assertThat(resultado).isEqualTo(DiaSemana.SEGUNDA)
    }

    @Test
    fun `fromDayOfWeek deve converter todos os dias corretamente`() {
        DayOfWeek.entries.forEach { dayOfWeek ->
            val diaSemana = DiaSemana.fromDayOfWeek(dayOfWeek)
            assertThat(diaSemana.dayOfWeek).isEqualTo(dayOfWeek)
        }
    }

    @Test
    fun `descricao deve retornar nomeCompleto`() {
        DiaSemana.entries.forEach { dia ->
            assertThat(dia.descricao).isEqualTo(dia.nomeCompleto)
        }
    }

    @Test
    fun `nomeResumido deve ter 3 caracteres`() {
        DiaSemana.entries.forEach { dia ->
            assertThat(dia.nomeResumido.length).isEqualTo(3)
        }
    }

    @Test
    fun `nomeCompleto deve conter a palavra feira para dias uteis`() {
        val diasUteis = listOf(
            DiaSemana.SEGUNDA,
            DiaSemana.TERCA,
            DiaSemana.QUARTA,
            DiaSemana.QUINTA,
            DiaSemana.SEXTA
        )
        diasUteis.forEach { dia ->
            assertThat(dia.nomeCompleto).contains("feira")
        }
    }

    @Test
    fun `SABADO e DOMINGO nao devem conter feira no nome`() {
        assertThat(DiaSemana.SABADO.nomeCompleto).doesNotContain("feira")
        assertThat(DiaSemana.DOMINGO.nomeCompleto).doesNotContain("feira")
    }
}
