package com.andreich.news.di

import android.content.Context
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.decode.BitmapFactoryDecoder
import coil3.request.allowHardware
import coil3.request.crossfade
import com.andreich.news.ui.core.getImageClient
import okhttp3.OkHttpClient
import org.koin.dsl.module

@OptIn(ExperimentalCoilApi::class)
fun uiModule(context: Context) = module {

    single<ImageLoader> {
        ImageLoader.Builder(context)
            .components {
                add(BitmapFactoryDecoder.Factory())
            }
            .allowHardware(false)
            .crossfade(true)
            .build()
    }
    single<OkHttpClient> {
        getImageClient
    }
}