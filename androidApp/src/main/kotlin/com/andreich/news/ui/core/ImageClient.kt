package com.andreich.news.ui.core
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

val getImageClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .callTimeout(5, TimeUnit.SECONDS).build()
}