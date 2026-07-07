package com.andreich.news.di

import com.andreich.news.network.ClientProvider
import com.andreich.news.network.NewsApi
import io.ktor.client.HttpClient
import org.koin.dsl.module
import java.time.LocalDate
import kotlin.time.Clock

fun networkModule(apiKey: String) = module {
    single<HttpClient> {
        ClientProvider().createHttpClient(apiKey)
    }

    single<NewsApi> {
        NewsApi(
            get(),
            LocalDate.ofEpochDay(Clock.System.now().epochSeconds / 86400 - 15).toString()
        )
    }
}