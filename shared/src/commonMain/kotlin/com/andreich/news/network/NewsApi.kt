package com.andreich.news.network

import com.andreich.news.data.datasource.SearchParamsDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path

class NewsApi(
    private val client: HttpClient,
    private val date: String
) {
    companion object {
        const val BASE_URL = "https://api.worldnewsapi.com/"
        const val TOP_NEWS = "top-news"

        const val TEXT = "text"
        const val SEARCH_NEWS = "search-news"
        const val RU = "ru"

        const val US = "us"

        const val EN = "en"
        const val SOURCE_COUNTRY = "source-country"
        const val LANGUAGE = "language"

        const val CATEGORY = "categories"

        const val LOCATION = "location-filter"

        const val DATE = "date"

    }

    suspend fun getNews(language: String = EN, sourceCountry: String = US): TopNewsResultDto {
        return client.get {
            url {
                path(TOP_NEWS)
                parameters.apply {
                    append(SOURCE_COUNTRY, sourceCountry)
                    append(LANGUAGE, language)
                    append(DATE, date)
                }
            }
        }.body()
    }

    suspend fun searchNews(param: String, paramsFilter: SearchParamsDto? = null): SearchResultDto {
        return client.get {
            url {
                path(SEARCH_NEWS)
                parameters.apply {
                    append(TEXT, param)
                    append(DATE, date)
                    paramsFilter?.let {
                        it.country?.let { append(SOURCE_COUNTRY, it) }
                        it.language?.let { append(LANGUAGE, it) }

                        it.category?.let { category ->
                            append(CATEGORY, category)
                        }
                        it.location?.let { location ->
                            append(LOCATION, location)
                        }

                    }
                }
            }
        }.body<SearchResultDto>()
    }
}