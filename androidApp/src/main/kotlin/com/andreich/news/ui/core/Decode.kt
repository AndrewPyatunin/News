package com.andreich.news.ui.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build.VERSION.SDK_INT
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.annotation.InternalCoilApi
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.DecodeUtils
import coil3.decode.Decoder
import coil3.decode.ExifOrientationStrategy
import coil3.decode.ExifOrientationStrategy.Companion.RESPECT_PERFORMANCE
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.request.allowRgb565
import coil3.request.bitmapConfig
import coil3.request.colorSpace
import coil3.request.maxBitmapSize
import coil3.request.premultipliedAlpha
import coil3.size.Precision
import coil3.util.component1
import coil3.util.component2
import coil3.util.toSoftware
import kotlinx.coroutines.runInterruptible

import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import okio.Buffer
import okio.ForwardingSource
import okio.Source
import okio.buffer
import kotlin.math.roundToInt

class ByteArrayFactoryDecoder(
    val source: ImageSource,
    val options: Options,
    val parallelismLock: Semaphore = Semaphore(Int.MAX_VALUE),
    val exifOrientationStrategy: ExifOrientationStrategy = RESPECT_PERFORMANCE,
) : Decoder {

    class Factory(
        val parallelismLock: Semaphore = Semaphore(DEFAULT_MAX_PARALLELISM),
        val exifOrientationStrategy: ExifOrientationStrategy = RESPECT_PERFORMANCE
    ) : Decoder.Factory {

        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            return ByteArrayFactoryDecoder(options = options, parallelismLock = parallelismLock, exifOrientationStrategy = exifOrientationStrategy, source = result.source)
        }
    }

    override suspend fun decode() = parallelismLock.withPermit {
        runInterruptible { BitmapFactory.Options().decode() }
    }

    private fun BitmapFactory.Options.decode(): DecodeResult {
        val safeSource = ExceptionCatchingSource(source.source())
        val safeBufferedSource = safeSource.buffer()
        val bytes = safeBufferedSource.readByteArray()
        // Read the image's dimensions.
        inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size,
            this
        ) ?: error("decode failed")
//        BitmapFactory.decodeStream(safeBufferedSource.peek().inputStream(), null, this)
        safeSource.exception?.let { throw it }
        inJustDecodeBounds = false

        // Get the image's EXIF data.
        val exifData = ExifUtils.getExifData(outMimeType, safeBufferedSource, exifOrientationStrategy)
        safeSource.exception?.let { throw it }

        // Always create immutable bitmaps as they have better performance.
        inMutable = false

        if (SDK_INT >= 26 && options.colorSpace != null) {
            inPreferredColorSpace = options.colorSpace
        }
        inPremultiplied = options.premultipliedAlpha

        configureConfig(exifData)
        configureScale(exifData)
        inJustDecodeBounds = false
        // Decode the bitmap.
        val outBitmap = BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size,
            this
        )
//        val outBitmap: Bitmap? = safeBufferedSource.use {
//            BitmapFactory.decodeByteArray(it.readByteArray(), 0, bytes.size)
//        }
        safeSource.exception?.let { throw it }
        checkNotNull(outBitmap) {
            "BitmapFactory returned a null bitmap. Often this means BitmapFactory could not " +
                    "decode the image data read from the image source (e.g. network, disk, or " +
                    "memory) as it's not encoded as a valid image format."
        }

        // Fix the incorrect density created by overloading inDensity/inTargetDensity.
        outBitmap.density = options.context.resources.displayMetrics.densityDpi

        // Reverse the EXIF transformations to get the original image.
        val bitmap = ExifUtils.reverseTransformations(outBitmap, exifData)

        return DecodeResult(
            image = bitmap.toDrawable(options.context).asImage(),
            isSampled = inSampleSize > 1 || inScaled,
        )
    }

    /** Compute and set [BitmapFactory.Options.inPreferredConfig]. */
    @OptIn(InternalCoilApi::class)
    private fun BitmapFactory.Options.configureConfig(exifData: ExifData) {
        var config = options.bitmapConfig

        // Disable hardware bitmaps if we need to perform EXIF transformations.
        if (exifData.isFlipped || exifData.isRotated) {
            config = config.toSoftware()
        }

        // Decode the image as RGB_565 as an optimization if allowed.
        if (options.allowRgb565 && config == Bitmap.Config.ARGB_8888 && outMimeType == MIME_TYPE_JPEG) {
            config = Bitmap.Config.RGB_565
        }

        // High color depth images must be decoded as either RGBA_F16 or HARDWARE.
        if (SDK_INT >= 26 && outConfig == Bitmap.Config.RGBA_F16 && config != Bitmap.Config.HARDWARE) {
            config = Bitmap.Config.RGBA_F16
        }

        inPreferredConfig = config
    }

    /** Compute and set the scaling properties for [BitmapFactory.Options]. */
    @OptIn(ExperimentalCoilApi::class)
    private fun BitmapFactory.Options.configureScale(exifData: ExifData) {
        // This occurs if there was an error decoding the image's size.
        if (outWidth <= 0 || outHeight <= 0) {
            inSampleSize = 1
            inScaled = false
            return
        }

        // srcWidth and srcHeight are the original dimensions of the image after
        // EXIF transformations (but before sampling).
        val srcWidth = if (exifData.isSwapped) outHeight else outWidth
        val srcHeight = if (exifData.isSwapped) outWidth else outHeight

        val (dstWidth, dstHeight) = DecodeUtils.computeDstSize(
            srcWidth = srcWidth,
            srcHeight = srcHeight,
            targetSize = options.size,
            scale = options.scale,
            maxSize = options.maxBitmapSize,
        )

        // Calculate the image's sample size.
        inSampleSize = DecodeUtils.calculateInSampleSize(
            srcWidth = srcWidth,
            srcHeight = srcHeight,
            dstWidth = dstWidth,
            dstHeight = dstHeight,
            scale = options.scale,
        )

        // Calculate the image's density scaling multiple.
        var scale = DecodeUtils.computeSizeMultiplier(
            srcWidth = srcWidth / inSampleSize.toDouble(),
            srcHeight = srcHeight / inSampleSize.toDouble(),
            dstWidth = dstWidth.toDouble(),
            dstHeight = dstHeight.toDouble(),
            scale = options.scale,
            maxSize = options.maxBitmapSize,
        )

        // Only upscale the image if the options require an exact size.
        if (options.precision == Precision.INEXACT) {
            scale = scale.coerceAtMost(1.0)
        }

        inScaled = scale != 1.0
        if (inScaled) {
            if (scale > 1) {
                // Upscale
                inDensity = (Int.MAX_VALUE / scale).roundToInt()
                inTargetDensity = Int.MAX_VALUE
            } else {
                // Downscale
                inDensity = Int.MAX_VALUE
                inTargetDensity = (Int.MAX_VALUE * scale).roundToInt()
            }
        }
    }

    /** Prevent [BitmapFactory.decodeStream] from swallowing [Exception]s. */
    private class ExceptionCatchingSource(delegate: Source) : ForwardingSource(delegate) {

        var exception: Exception? = null
            private set

        override fun read(sink: Buffer, byteCount: Long): Long {
            try {
                return super.read(sink, byteCount)
            } catch (e: Exception) {
                exception = e
                throw e
            }
        }
    }

    internal companion object {
        const val MIME_TYPE_JPEG = "image/jpeg"
        internal const val DEFAULT_MAX_PARALLELISM = 4
    }
}