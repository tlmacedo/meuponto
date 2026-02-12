// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/ConfiguracaoJornada.kt
package br.com.tlmacedo.meuponto.domain.model

/**
 * Modelo que representa as configurações de jornada de trabalho.
 *
 * Armazena todas as preferências relacionadas à jornada do usuário,
 * permitindo personalização dos parâmetros de cálculo de horas.
 *
 * @property cargaHorariaDiariaMinutos Carga horária diária em minutos (padrão: 480 = 8h)
 * @property cargaHorariaSemanalMinutos Carga horária semanal em minutos (padrão: 2640 = 44h)
 * @property intervaloMinimoMinutos Intervalo mínimo obrigatório em minutos (padrão: 60 = 1h)
 * @property toleranciaMinutos Tolerância para batida de ponto em minutos (padrão: 10)
 * @property jornadaMaximaDiariaMinutos Jornada máxima diária em minutos (padrão: 600 = 10h)
 * @property horaEntradaPadrao Hora padrão de entrada (padrão: 8)
 * @property minutoEntradaPadrao Minuto padrão de entrada (padrão: 0)
 * @property horaSaidaPadrao Hora padrão de saída (padrão: 17)
 * @property minutoSaidaPadrao Minuto padrão de saída (padrão: 0)
 *
 * @author Thiago
 * @since 1.0.0
 */
data class ConfiguracaoJornada(
    val cargaHorariaDiariaMinutos: Int = DEFAULT_CARGA_HORARIA_DIARIA,
    val cargaHorariaSemanalMinutos: Int = DEFAULT_CARGA_HORARIA_SEMANAL,
    val intervaloMinimoMinutos: Int = DEFAULT_INTERVALO_MINIMO,
    val toleranciaMinutos: Int = DEFAULT_TOLERANCIA,
    val jornadaMaximaDiariaMinutos: Int = DEFAULT_JORNADA_MAXIMA,
    val horaEntradaPadrao: Int = DEFAULT_HORA_ENTRADA,
    val minutoEntradaPadrao: Int = DEFAULT_MINUTO_ENTRADA,
    val horaSaidaPadrao: Int = DEFAULT_HORA_SAIDA,
    val minutoSaidaPadrao: Int = DEFAULT_MINUTO_SAIDA
) {
    /**
     * Retorna a carga horária diária formatada (HH:mm).
     */
    val cargaHorariaDiariaFormatada: String
        get() {
            val horas = cargaHorariaDiariaMinutos / 60
            val minutos = cargaHorariaDiariaMinutos % 60
            return String.format("%02d:%02d", horas, minutos)
        }

    /**
     * Retorna a carga horária semanal formatada (HH:mm).
     */
    val cargaHorariaSemanalFormatada: String
        get() {
            val horas = cargaHorariaSemanalMinutos / 60
            val minutos = cargaHorariaSemanalMinutos % 60
            return String.format("%02d:%02d", horas, minutos)
        }

    /**
     * Retorna o horário de entrada padrão formatado (HH:mm).
     */
    val horarioEntradaPadraoFormatado: String
        get() = String.format("%02d:%02d", horaEntradaPadrao, minutoEntradaPadrao)

    /**
     * Retorna o horário de saída padrão formatado (HH:mm).
     */
    val horarioSaidaPadraoFormatado: String
        get() = String.format("%02d:%02d", horaSaidaPadrao, minutoSaidaPadrao)

    companion object {
        /** Carga horária diária padrão em minutos (8 horas) */
        const val DEFAULT_CARGA_HORARIA_DIARIA = 480

        /** Carga horária semanal padrão em minutos (44 horas) */
        const val DEFAULT_CARGA_HORARIA_SEMANAL = 2640

        /** Intervalo mínimo padrão em minutos (1 hora) */
        const val DEFAULT_INTERVALO_MINIMO = 60

        /** Tolerância padrão em minutos */
        const val DEFAULT_TOLERANCIA = 10

        /** Jornada máxima diária padrão em minutos (10 horas) */
        const val DEFAULT_JORNADA_MAXIMA = 600

        /** Hora padrão de entrada */
        const val DEFAULT_HORA_ENTRADA = 8

        /** Minuto padrão de entrada */
        const val DEFAULT_MINUTO_ENTRADA = 0

        /** Hora padrão de saída */
        const val DEFAULT_HORA_SAIDA = 17

        /** Minuto padrão de saída */
        const val DEFAULT_MINUTO_SAIDA = 0

        /**
         * Retorna a configuração padrão.
         *
         * @return ConfiguracaoJornada com valores padrão
         */
        fun default(): ConfiguracaoJornada = ConfiguracaoJornada()
    }
}
