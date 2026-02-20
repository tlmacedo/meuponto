// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/MotivoEdicao.kt
package br.com.tlmacedo.meuponto.domain.model

/**
 * Motivos pré-definidos para edição/exclusão de ponto.
 *
 * @author Thiago
 * @since 3.6.0
 */
enum class MotivoEdicao(
    val descricao: String,
    val requerDetalhes: Boolean = false
) {
    NENHUM("Selecione um motivo...", requerDetalhes = true),
    ESQUECI_REGISTRAR("Esqueci de registrar o ponto"),
    ERRO_HORARIO("Erro de digitação/horário incorreto"),
    SISTEMA_INDISPONIVEL("Sistema indisponível no momento"),
    AJUSTE_AUTORIZADO("Ajuste de horário autorizado"),
    TRABALHO_EXTERNO("Trabalho externo/reunião fora"),
    HORARIO_FLEXIVEL("Compensação de horário flexível"),
    FALTA_JUSTIFICADA("Falta justificada"),
    ATESTADO_MEDICO("Atestado médico"),
    AJUSTE_NSR("Ajuste de NSR"),
    OUTRO("Outro motivo (especificar)", requerDetalhes = true);

    companion object {
        /**
         * Retorna motivos selecionáveis (exceto NENHUM).
         */
        fun selecionaveis(): List<MotivoEdicao> = entries.filter { it != NENHUM }
    }
}
