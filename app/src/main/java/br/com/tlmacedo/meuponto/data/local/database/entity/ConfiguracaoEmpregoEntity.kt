// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/entity/ConfiguracaoEmpregoEntity.kt
package br.com.tlmacedo.meuponto.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.tlmacedo.meuponto.domain.model.ConfiguracaoEmprego
import br.com.tlmacedo.meuponto.domain.model.FotoFormato
import br.com.tlmacedo.meuponto.domain.model.TipoNsr
import java.time.LocalDateTime

/**
 * Entidade Room para configurações de emprego.
 *
 * Contém configurações de exibição, comportamento e captura de dados
 * específicas de cada emprego.
 *
 * @author Thiago
 * @since 2.0.0
 * @updated 8.0.0 - Simplificado: campos de jornada/banco migrados para VersaoJornada
 * @updated 9.0.0 - Adicionado fotoObrigatoria
 * @updated 10.0.0 - Adicionados campos completos de configuração de foto
 */
@Entity(
    tableName = "configuracoes_emprego",
    foreignKeys = [
        ForeignKey(
            entity = EmpregoEntity::class,
            parentColumns = ["id"],
            childColumns = ["empregoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["empregoId"], unique = true)]
)
data class ConfiguracaoEmpregoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val empregoId: Long,

    // ════════════════════════════════════════════════════════════════════════
    // NSR (Número Sequencial de Registro)
    // ════════════════════════════════════════════════════════════════════════

    val habilitarNsr: Boolean = false,
    val tipoNsr: TipoNsr = TipoNsr.NUMERICO,

    // ════════════════════════════════════════════════════════════════════════
    // LOCALIZAÇÃO
    // ════════════════════════════════════════════════════════════════════════

    val habilitarLocalizacao: Boolean = false,
    val localizacaoAutomatica: Boolean = false,
    val exibirLocalizacaoDetalhes: Boolean = true,

    // ════════════════════════════════════════════════════════════════════════
    // FOTO DE COMPROVANTE
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Habilita a funcionalidade de foto de comprovante.
     * Quando true, o usuário será solicitado a capturar/selecionar uma foto
     * após o registro do ponto.
     */
    val fotoHabilitada: Boolean = false,

    /**
     * Torna a foto obrigatória para concluir o registro do ponto.
     * Só tem efeito se fotoHabilitada = true.
     */
    val fotoObrigatoria: Boolean = false,

    /**
     * Formato de salvamento da foto.
     * JPEG: Menor tamanho, boa qualidade (recomendado)
     * PNG: Maior qualidade, sem perdas, maior tamanho
     */
    val fotoFormato: FotoFormato = FotoFormato.JPEG,

    /**
     * Qualidade de compressão da foto (1-100).
     * Apenas para formato JPEG.
     * Recomendado: 85 (bom equilíbrio entre qualidade e tamanho)
     */
    val fotoQualidade: Int = 85,

    /**
     * Resolução máxima da foto em pixels (largura).
     * A altura é calculada proporcionalmente.
     * Ex: 1920 = Full HD, 1280 = HD, 0 = sem limite
     */
    val fotoResolucaoMaxima: Int = 1920,

    /**
     * Tamanho máximo do arquivo em KB.
     * Se a foto exceder, será recomprimida.
     * 0 = sem limite
     */
    val fotoTamanhoMaximoKb: Int = 1024,

    /**
     * Corrigir automaticamente a orientação da foto baseado em EXIF.
     */
    val fotoCorrecaoOrientacao: Boolean = true,

    /**
     * Permitir apenas captura via câmera.
     * Quando true, desabilita a opção de selecionar da galeria.
     */
    val fotoApenasCamera: Boolean = false,

    /**
     * Incluir localização GPS nos metadados EXIF da foto.
     * Requer habilitarLocalizacao = true.
     */
    val fotoIncluirLocalizacaoExif: Boolean = true,

    /**
     * Habilitar backup automático das fotos na nuvem.
     */
    val fotoBackupNuvemHabilitado: Boolean = false,

    /**
     * Sincronizar fotos apenas quando conectado a Wi-Fi.
     */
    val fotoBackupApenasWifi: Boolean = true,

    // ════════════════════════════════════════════════════════════════════════
    // EXIBIÇÃO
    // ════════════════════════════════════════════════════════════════════════

    val exibirDuracaoTurno: Boolean = true,
    val exibirDuracaoIntervalo: Boolean = true,

    // ════════════════════════════════════════════════════════════════════════
    // AUDITORIA
    // ════════════════════════════════════════════════════════════════════════

    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now()
)

/**
 * Converte Entity para Domain Model.
 */
fun ConfiguracaoEmpregoEntity.toDomain(): ConfiguracaoEmprego =
    ConfiguracaoEmprego(
        id = id,
        empregoId = empregoId,
        habilitarNsr = habilitarNsr,
        tipoNsr = tipoNsr,
        habilitarLocalizacao = habilitarLocalizacao,
        localizacaoAutomatica = localizacaoAutomatica,
        exibirLocalizacaoDetalhes = exibirLocalizacaoDetalhes,
        fotoHabilitada = fotoHabilitada,
        fotoObrigatoria = fotoObrigatoria,
        fotoFormato = fotoFormato,
        fotoQualidade = fotoQualidade,
        fotoResolucaoMaxima = fotoResolucaoMaxima,
        fotoTamanhoMaximoKb = fotoTamanhoMaximoKb,
        fotoCorrecaoOrientacao = fotoCorrecaoOrientacao,
        fotoApenasCamera = fotoApenasCamera,
        fotoIncluirLocalizacaoExif = fotoIncluirLocalizacaoExif,
        fotoBackupNuvemHabilitado = fotoBackupNuvemHabilitado,
        fotoBackupApenasWifi = fotoBackupApenasWifi,
        exibirDuracaoTurno = exibirDuracaoTurno,
        exibirDuracaoIntervalo = exibirDuracaoIntervalo,
        criadoEm = criadoEm,
        atualizadoEm = atualizadoEm
    )

/**
 * Converte Domain Model para Entity.
 */
fun ConfiguracaoEmprego.toEntity(): ConfiguracaoEmpregoEntity =
    ConfiguracaoEmpregoEntity(
        id = id,
        empregoId = empregoId,
        habilitarNsr = habilitarNsr,
        tipoNsr = tipoNsr,
        habilitarLocalizacao = habilitarLocalizacao,
        localizacaoAutomatica = localizacaoAutomatica,
        exibirLocalizacaoDetalhes = exibirLocalizacaoDetalhes,
        fotoHabilitada = fotoHabilitada,
        fotoObrigatoria = fotoObrigatoria,
        fotoFormato = fotoFormato,
        fotoQualidade = fotoQualidade,
        fotoResolucaoMaxima = fotoResolucaoMaxima,
        fotoTamanhoMaximoKb = fotoTamanhoMaximoKb,
        fotoCorrecaoOrientacao = fotoCorrecaoOrientacao,
        fotoApenasCamera = fotoApenasCamera,
        fotoIncluirLocalizacaoExif = fotoIncluirLocalizacaoExif,
        fotoBackupNuvemHabilitado = fotoBackupNuvemHabilitado,
        fotoBackupApenasWifi = fotoBackupApenasWifi,
        exibirDuracaoTurno = exibirDuracaoTurno,
        exibirDuracaoIntervalo = exibirDuracaoIntervalo,
        criadoEm = criadoEm,
        atualizadoEm = atualizadoEm
    )
