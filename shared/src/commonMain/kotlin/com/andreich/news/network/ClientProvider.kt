package com.andreich.news.network

import io.ktor.client.HttpClient

expect class ClientProvider {

    fun createHttpClient(apiKey: String): HttpClient
}