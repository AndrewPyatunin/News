package com.andreich.news.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json

actual class ClientProvider {

    private val BASE_URL = "https://api.worldnewsapi.com/"

    actual fun createHttpClient(apiKey: String): HttpClient {

        return HttpClient(OkHttp) {
            install(Logging) {

                level = LogLevel.BODY
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 10_000
            }
            expectSuccess = true

            install(ContentNegotiation) {
                json()
            }
            defaultRequest {
                url(BASE_URL)
                header("x-api-key", apiKey)
            }
        }
    }
}