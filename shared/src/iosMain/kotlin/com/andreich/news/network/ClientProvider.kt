package com.andreich.news.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json

actual class ClientProvider {
    private val BASE_URL = "https://api.worldnewsapi.com/"

    actual fun createHttpClient(apiKey: String): HttpClient {
        return HttpClient(Darwin) {
            defaultRequest {
                url(BASE_URL)
                header("x-api-key", apiKey)
            }
            install(ContentNegotiation) {
                json()
            }
        }
    }
}