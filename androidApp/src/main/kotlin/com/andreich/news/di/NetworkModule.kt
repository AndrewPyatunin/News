package com.andreich.news.di

import com.andreich.news.network.ClientProvider
import com.andreich.news.network.NewsApi
import io.ktor.client.HttpClient
import org.koin.dsl.module

fun networkModule(apiKey: String) = module {
    single<HttpClient> {
        ClientProvider().createHttpClient(apiKey)
    }
    single<NewsApi> {
        NewsApi(get())
    }
}