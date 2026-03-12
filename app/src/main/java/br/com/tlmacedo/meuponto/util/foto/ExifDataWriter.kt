// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/util/foto/ExifDataWriter.kt
package br.com.tlmacedo.meuponto.util.foto

import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Gravador de metadados EXIF em imagens JPEG.
 *
 * @author Thiago
 * @since 10.0.0
 */
@Singleton
class ExifDataWriter @Inject constructor() {

    companion object {
        const val SOFTWARE_TAG = "MeuPonto App"
        private val EXIF_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")
    }

    /**
     * Escreve metadados completos em uma imagem.
     */
    fun writeMetadata(file: File, metadata: FotoExifMetadata): Boolean {
        return try {
            val exif = ExifInterface(file)

            // Data e hora
            metadata.dateTime?.let { dateTime ->
                val formattedDate = dateTime.format(EXIF_DATE_FORMAT)
                exif.setAttribute(ExifInterface.TAG_DATETIME, formattedDate)
                exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, formattedDate)
                exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, formattedDate)
            }

            // Localização GPS
            if (metadata.latitude != null && metadata.longitude != null) {
                writeGpsData(exif, metadata.latitude, metadata.longitude, metadata.altitude)
            }

            // Comentário do usuário
            metadata.userComment?.let { exif.setAttribute(ExifInterface.TAG_USER_COMMENT, it) }

            // Descrição
            metadata.description?.let { exif.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, it) }

            // Software
            exif.setAttribute(ExifInterface.TAG_SOFTWARE, SOFTWARE_TAG)

            exif.saveAttributes()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Escreve apenas dados de localização GPS.
     */
    fun writeGpsLocation(
        file: File,
        latitude: Double,
        longitude: Double,
        altitude: Double? = null
    ): Boolean {
        return try {
            val exif = ExifInterface(file)
            writeGpsData(exif, latitude, longitude, altitude)
            exif.saveAttributes()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun writeGpsData(
        exif: ExifInterface,
        latitude: Double,
        longitude: Double,
        altitude: Double?
    ) {
        // Latitude
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, if (latitude >= 0) "N" else "S")
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, decimalToDMS(abs(latitude)))

        // Longitude
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, if (longitude >= 0) "E" else "W")
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, decimalToDMS(abs(longitude)))

        // Altitude
        altitude?.let { alt ->
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, if (alt >= 0) "0" else "1")
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, "${abs(alt).toLong()}/1")
        }
    }

    /**
     * Escreve apenas data/hora.
     */
    fun writeDateTime(file: File, dateTime: LocalDateTime): Boolean {
        return try {
            val exif = ExifInterface(file)
            val formattedDate = dateTime.format(EXIF_DATE_FORMAT)
            exif.setAttribute(ExifInterface.TAG_DATETIME, formattedDate)
            exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, formattedDate)
            exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, formattedDate)
            exif.saveAttributes()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Escreve comentário personalizado.
     */
    fun writeUserComment(file: File, comment: String): Boolean {
        return try {
            val exif = ExifInterface(file)
            exif.setAttribute(ExifInterface.TAG_USER_COMMENT, comment)
            exif.saveAttributes()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Lê metadados EXIF de uma imagem.
     */
    fun readMetadata(file: File): FotoExifMetadata? {
        return try {
            val exif = ExifInterface(file)

            val dateTimeStr = exif.getAttribute(ExifInterface.TAG_DATETIME)
            val dateTime = dateTimeStr?.let {
                try { LocalDateTime.parse(it, EXIF_DATE_FORMAT) } catch (e: Exception) { null }
            }

            val latLong = FloatArray(2)
            val hasGps = exif.getLatLong(latLong)

            FotoExifMetadata(
                dateTime = dateTime,
                latitude = if (hasGps) latLong[0].toDouble() else null,
                longitude = if (hasGps) latLong[1].toDouble() else null,
                altitude = exif.getAltitude(Double.NaN).takeIf { !it.isNaN() },
                userComment = exif.getAttribute(ExifInterface.TAG_USER_COMMENT),
                description = exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION),
                software = exif.getAttribute(ExifInterface.TAG_SOFTWARE)
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Converte coordenada decimal para formato DMS.
     */
    private fun decimalToDMS(decimal: Double): String {
        val degrees = decimal.toInt()
        val minutesDecimal = (decimal - degrees) * 60
        val minutes = minutesDecimal.toInt()
        val seconds = ((minutesDecimal - minutes) * 60 * 1000).toInt()
        return "$degrees/1,$minutes/1,$seconds/1000"
    }
}

/**
 * Metadados EXIF para foto de comprovante.
 */
data class FotoExifMetadata(
    val dateTime: LocalDateTime? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitude: Double? = null,
    val userComment: String? = null,
    val description: String? = null,
    val software: String? = null
) {
    val hasGpsData: Boolean get() = latitude != null && longitude != null
    val hasDateTime: Boolean get() = dateTime != null
}
