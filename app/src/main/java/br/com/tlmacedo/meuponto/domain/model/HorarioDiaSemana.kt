// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/HorarioDiaSemana.kt
package br.com.tlmacedo.meuponto.domain.model

import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Modelo de domínio que representa a configuração de horários para um dia da semana.
 *
 * Permite configurar horários ideais, carga horária e tolerâncias de intervalo
 * para cada dia da semana de forma independente por emprego.
 *
 * @property id Identificador único da configuração
 * @property empregoId FK para o emprego associado
 * @property diaSemana Dia da semana desta configuração
 * @property ativo Se false, o dia é considerado folga
 * @property cargaHorariaMinutos Carga horária esperada em minutos (ex: 492 = 8h12m)
 * @property entradaIdeal Horário ideal de entrada
 * @property saidaIntervaloIdeal Horário ideal de saída para intervalo
 * @property voltaIntervaloIdeal Horário ideal de volta do intervalo
 * @property saidaIdeal Horário ideal de saída final
 * @property intervaloMinimoMinutos Intervalo mínimo obrigatório em minutos
 * @property toleranciaIntervaloMaisMinutos Tolerância para mais no intervalo
 * @property toleranciaIntervaloMenosMinutos Tolerância para menos no intervalo
 * @property criadoEm Timestamp de criação
 * @property atualizadoEm Timestamp da última atualização
 *
 * @author Thiago
 * @since 2.0.0
 */
data class HorarioDiaSemana(
    val id: Long = 0,
    val empregoId: Long,
    val diaSemana: DiaSemana,
    val ativo: Boolean = true,
    val cargaHorariaMinutos: Int = 492,
    val entradaIdeal: LocalTime? = null,
    val saidaIntervaloIdeal: LocalTime? = null,
    val voltaIntervaloIdeal: LocalTime? = null,
    val saidaIdeal: LocalTime? = null,
    val intervaloMinimoMinutos: Int = 60,
    val toleranciaIntervaloMaisMinutos: Int = 0,
    val toleranciaIntervaloMenosMinutos: Int = 0,
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Verifica se é dia de trabalho (ativo).
     */
    val isDiaTrabalho: Boolean
        get() = ativo

    /**
     * Verifica se é dia de folga (inativo).
     */
    val isDiaFolga: Boolean
        get() = !ativo

    /**
     * Retorna a carga horária formatada (ex: "08:12").
     */
    val cargaHorariaFormatada: String
        get() {
            val horas = cargaHorariaMinutos / 60
            val minutos = cargaHorariaMinutos % 60
            return String.format("%02d:%02d", horas, minutos)
        }

    /**
     * Verifica se possui horários ideais configurados.
     */
    val temHorariosIdeais: Boolean
        get() = entradaIdeal != null || saidaIdeal != null

    /**
     * Verifica se possui intervalo configurado.
     */
    val temIntervalo: Boolean
        get() = saidaIntervaloIdeal != null && voltaIntervaloIdeal != null
}
