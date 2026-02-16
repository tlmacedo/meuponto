package br.com.tlmacedo.meuponto.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek

/**
 * Testes unitários para DiaSemana.
 */
class DiaSemanaTest {

    @Test
    fun `javaDayOfWeek deve retornar o DayOfWeek correto para DOMINGO`() {
        assertEquals(DayOfWeek.SUNDAY, DiaSemana.DOMINGO.javaDayOfWeek)
    }

    @Test
    fun `javaDayOfWeek deve retornar o DayOfWeek correto para SEGUNDA`() {
        assertEquals(DayOfWeek.MONDAY, DiaSemana.SEGUNDA.javaDayOfWeek)
    }

    @Test
    fun `javaDayOfWeek deve retornar o DayOfWeek correto para TERCA`() {
        assertEquals(DayOfWeek.TUESDAY, DiaSemana.TERCA.javaDayOfWeek)
    }

    @Test
    fun `javaDayOfWeek deve retornar o DayOfWeek correto para QUARTA`() {
        assertEquals(DayOfWeek.WEDNESDAY, DiaSemana.QUARTA.javaDayOfWeek)
    }

    @Test
    fun `javaDayOfWeek deve retornar o DayOfWeek correto para QUINTA`() {
        assertEquals(DayOfWeek.THURSDAY, DiaSemana.QUINTA.javaDayOfWeek)
    }

    @Test
    fun `javaDayOfWeek deve retornar o DayOfWeek correto para SEXTA`() {
        assertEquals(DayOfWeek.FRIDAY, DiaSemana.SEXTA.javaDayOfWeek)
    }

    @Test
    fun `javaDayOfWeek deve retornar o DayOfWeek correto para SABADO`() {
        assertEquals(DayOfWeek.SATURDAY, DiaSemana.SABADO.javaDayOfWeek)
    }

    @Test
    fun `fromJavaDayOfWeek deve converter corretamente`() {
        assertEquals(DiaSemana.SEGUNDA, DiaSemana.fromJavaDayOfWeek(DayOfWeek.MONDAY))
        assertEquals(DiaSemana.DOMINGO, DiaSemana.fromJavaDayOfWeek(DayOfWeek.SUNDAY))
        assertEquals(DiaSemana.SABADO, DiaSemana.fromJavaDayOfWeek(DayOfWeek.SATURDAY))
    }

    @Test
    fun `descricao deve retornar nome completo em portugues`() {
        assertEquals("Segunda-feira", DiaSemana.SEGUNDA.descricao)
        assertEquals("Domingo", DiaSemana.DOMINGO.descricao)
        assertEquals("Sábado", DiaSemana.SABADO.descricao)
    }

    @Test
    fun `descricaoCurta deve retornar abreviacao de 3 letras`() {
        assertEquals("Seg", DiaSemana.SEGUNDA.descricaoCurta)
        assertEquals("Dom", DiaSemana.DOMINGO.descricaoCurta)
        assertEquals("Sáb", DiaSemana.SABADO.descricaoCurta)
    }

    @Test
    fun `isDiaUtil deve retornar true para dias uteis`() {
        assertTrue(DiaSemana.SEGUNDA.isDiaUtil)
        assertTrue(DiaSemana.TERCA.isDiaUtil)
        assertTrue(DiaSemana.QUARTA.isDiaUtil)
        assertTrue(DiaSemana.QUINTA.isDiaUtil)
        assertTrue(DiaSemana.SEXTA.isDiaUtil)
    }

    @Test
    fun `isDiaUtil deve retornar false para fim de semana`() {
        assertFalse(DiaSemana.SABADO.isDiaUtil)
        assertFalse(DiaSemana.DOMINGO.isDiaUtil)
    }

    @Test
    fun `isFimDeSemana deve retornar true para sabado e domingo`() {
        assertTrue(DiaSemana.SABADO.isFimDeSemana)
        assertTrue(DiaSemana.DOMINGO.isFimDeSemana)
    }

    @Test
    fun `isFimDeSemana deve retornar false para dias uteis`() {
        assertFalse(DiaSemana.SEGUNDA.isFimDeSemana)
        assertFalse(DiaSemana.SEXTA.isFimDeSemana)
    }

    @Test
    fun `proximoDia deve retornar o dia seguinte`() {
        assertEquals(DiaSemana.TERCA, DiaSemana.SEGUNDA.proximoDia)
        assertEquals(DiaSemana.DOMINGO, DiaSemana.SABADO.proximoDia)
        assertEquals(DiaSemana.SEGUNDA, DiaSemana.DOMINGO.proximoDia)
    }

    @Test
    fun `diaAnterior deve retornar o dia anterior`() {
        assertEquals(DiaSemana.SEGUNDA, DiaSemana.TERCA.diaAnterior)
        assertEquals(DiaSemana.SEXTA, DiaSemana.SABADO.diaAnterior)
        assertEquals(DiaSemana.SABADO, DiaSemana.DOMINGO.diaAnterior)
    }
}
