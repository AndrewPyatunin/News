package com.andreich.news.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path

class NewsApi(
    private val client: HttpClient
) {
    companion object {
        const val BASE_URL = "https://api.worldnewsapi.com/"
        const val TOP_NEWS = "top-news"

        const val TEXT = "text"
        const val SEARCH_NEWS = "search-news"
        const val RU = "ru"
        const val SOURCE_COUNTRY = "source-country"
        const val LANGUAGE = "language"

    }

    suspend fun getNews(): TopNewsResultDto {
        return client.get {
            url {
                path(TOP_NEWS)
                parameters.apply {
                    append(SOURCE_COUNTRY, RU)
                    append(LANGUAGE, RU)
                }
            }
        }.body()
    }

    suspend fun searchNews(param: String): SearchResultDto {
        return client.get {
            url {
                path(SEARCH_NEWS)
                parameters.apply {
                    append(TEXT, param)
                }
            }
        }.body()
    }
}