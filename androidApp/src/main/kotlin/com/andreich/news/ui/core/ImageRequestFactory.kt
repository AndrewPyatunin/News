package com.andreich.news.ui.core

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import coil3.BitmapImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.bitmapConfig

class ImageRequestFactory(
    private val context: Context
) {
    fun create(url: String): ImageRequest {

        return ImageRequest.Builder(context)
            .data(url)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .allowHardware(false)
            .listener(
                onStart = {
                },
                onSuccess = { _, result ->
                    ImageFailureCache.markSuccess(url)
                    Log.d("IMG", result.image.toString())
                    Log.d("IMG", result.image::class.java.name)

                    val drawable = result.image
                    Log.d("IMG", drawable::class.java.name)

                    if (drawable is BitmapImage) {
                        Log.d(
                            "IMG",
                            "bitmap=${drawable.bitmap.width}x${drawable.bitmap.height}"
                        )
                    }
                },
                onError = { _, _ ->
                    ImageFailureCache.markFailed(url)
                }
            )
            .build()
    }
}