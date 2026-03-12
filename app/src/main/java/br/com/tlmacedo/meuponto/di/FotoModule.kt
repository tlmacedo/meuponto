// Arquivo: app/src/main/java/br/com/tlmacedo/meuponto/di/FotoModule.kt
package br.com.tlmacedo.meuponto.di

import android.content.Context
import br.com.tlmacedo.meuponto.util.ComprovanteImageStorage
import br.com.tlmacedo.meuponto.util.foto.ExifDataWriter
import br.com.tlmacedo.meuponto.util.foto.FotoStorageManager
import br.com.tlmacedo.meuponto.util.foto.ImageCompressor
import br.com.tlmacedo.meuponto.util.foto.ImageHashCalculator
import br.com.tlmacedo.meuponto.util.foto.ImageOrientationCorrector
import br.com.tlmacedo.meuponto.util.foto.ImageProcessor
import br.com.tlmacedo.meuponto.util.foto.ImageResizer
import br.com.tlmacedo.meuponto.util.foto.PhotoCaptureManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para injeção de dependências do sistema de foto.
 *
 * @author Thiago
 * @since 10.0.0
 */
@Module
@InstallIn(SingletonComponent::class)
object FotoModule {

    @Provides
    @Singleton
    fun provideImageCompressor(): ImageCompressor = ImageCompressor()

    @Provides
    @Singleton
    fun provideImageOrientationCorrector(
        @ApplicationContext context: Context
    ): ImageOrientationCorrector = ImageOrientationCorrector(context)

    @Provides
    @Singleton
    fun provideImageResizer(
        @ApplicationContext context: Context
    ): ImageResizer = ImageResizer(context)

    @Provides
    @Singleton
    fun provideImageHashCalculator(
        @ApplicationContext context: Context
    ): ImageHashCalculator = ImageHashCalculator(context)

    @Provides
    @Singleton
    fun provideExifDataWriter(): ExifDataWriter = ExifDataWriter()

    @Provides
    @Singleton
    fun provideImageProcessor(
        @ApplicationContext context: Context,
        resizer: ImageResizer,
        compressor: ImageCompressor,
        orientationCorrector: ImageOrientationCorrector,
        hashCalculator: ImageHashCalculator,
        exifWriter: ExifDataWriter
    ): ImageProcessor = ImageProcessor(
        context = context,
        resizer = resizer,
        compressor = compressor,
        orientationCorrector = orientationCorrector,
        hashCalculator = hashCalculator,
        exifWriter = exifWriter
    )

    @Provides
    @Singleton
    fun provideFotoStorageManager(
        @ApplicationContext context: Context,
        imageStorage: ComprovanteImageStorage,
        imageProcessor: ImageProcessor,
        hashCalculator: ImageHashCalculator,
        exifWriter: ExifDataWriter
    ): FotoStorageManager = FotoStorageManager(
        context = context,
        imageStorage = imageStorage,
        imageProcessor = imageProcessor,
        hashCalculator = hashCalculator,
        exifWriter = exifWriter
    )

    @Provides
    @Singleton
    fun providePhotoCaptureManager(
        @ApplicationContext context: Context,
        storageManager: FotoStorageManager
    ): PhotoCaptureManager = PhotoCaptureManager(
        context = context,
        storageManager = storageManager
    )
}
