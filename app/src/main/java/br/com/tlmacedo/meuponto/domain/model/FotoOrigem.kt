// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/domain/model/FotoOrigem.kt
package br.com.tlmacedo.meuponto.domain.model

/**
 * Origem da foto de comprovante.
 *
 * @author Thiago
 * @since 10.0.0
 */
enum class FotoOrigem {
    /**
     * Foto capturada diretamente pela câmera do dispositivo.
     */
    CAMERA,

    /**
     * Foto selecionada da galeria do dispositivo.
     */
    GALERIA
}
