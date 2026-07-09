package com.andreich.news.data.repository

import com.andreich.news.data.datasource.CityLookup
import com.andreich.news.data.mapper.toDomain
import com.andreich.news.data.mapper.toEntity
import com.andreich.news.data.mapper.toFavoriteEntity
import com.andreich.news.data.mapper.toNews
import com.andreich.news.data.mapper.toSearchParamsDto
import com.andreich.news.database.CacheEntity
import com.andreich.news.database.NewsDao
import com.andreich.news.domain.model.News
import com.andreich.news.domain.model.NewsRequest
import com.andreich.news.domain.model.ParamsFilter
import com.andreich.news.domain.model.RequestResult
import com.andreich.news.domain.repository.NewsRepository
import com.andreich.news.network.NewsApi
import com.andreich.news.network.safeApiCall
import com.andreich.news.network.toNews
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class NewsRepositoryImpl(
    private val newsApi: NewsApi,
    private val newsDao: NewsDao,
    private val cityLookup: CityLookup
) : NewsRepository {

    companion object {
        private const val CACHE_EXPIRED = 2 * 3_600L
    }


    override fun getNews(language: String?, country: String?, limit: Int): Flow<List<News>> {
        return newsDao.getNewsFlow(language, country, limit).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getFavorites(): Flow<List<News>> {
        return newsDao.getFavorites().map { list -> list.map { it.toNews() } }
    }

    override fun searchNews(
        param: String,
        paramsFilter: ParamsFilter?
    ): Flow<List<News>> {
        return newsDao.getSearchedNews(
            param,
            paramsFilter?.language,
            paramsFilter?.country,
            paramsFilter?.category,
            paramsFilter?.location
        ).map { list ->
            list.map {
                it.toDomain()
            }
        }
    }

    override suspend fun updateNews(language: String, country: String): RequestResult {
        return getRequestResult(
            if (language.lowercase() == "en") NewsRequest.TopNewsEng(country) else if(language.lowercase() == "ru") NewsRequest.TopNewsRu(
                country
            ) else NewsRequest.TopNewsRu(country)
        ) {
            getTopNewsApiCall(language, country)
        }
    }

    override suspend fun updateSearchedNews(
        param: String,
        paramsFilter: ParamsFilter?
    ): RequestResult {
        return getRequestResult(
            paramsFilter?.let { (country, language, category, location) ->
                country?.let { if (country.lowercase() == "en") NewsRequest.SearchEng(
                    param,
                    country
                ) else if (country.lowercase() == "ru") NewsRequest.SearchRu(param, country) else NewsRequest.Search(param) } ?: NewsRequest.Search(param)

            } ?: NewsRequest.Search(param))
        {
            searchApiCall(param, paramsFilter)
        }
    }

private suspend fun searchApiCall(param: String, paramsFilter: ParamsFilter?): List<News> {
    val searchParams = paramsFilter?.toSearchParamsDto(cityLookup)
    return newsApi.searchNews(param, searchParams).news.distinctBy { Triple(it.title, it.author, it.summary) }.map {
        it.toNews()
    }
}

private suspend fun getTopNewsApiCall(language: String, country: String): List<News> {
    return newsApi.getNews(language = language, country).topNews.distinctBy { it.news.firstOrNull()?.let { news -> Triple(it.news[0].title, it.news[0].author, it.news[0].summary) } }.map{
        it.news[0].toNews()
    }
}

suspend fun getRequestResult(
    requestType: NewsRequest,
    apiCall: suspend () -> List<News>
): RequestResult {
    val currentTime = Clock.System.now().epochSeconds
    if (!isCacheExpired(requestType)) return RequestResult.Success
    return safeApiCall(
        apiCall = apiCall,
        onSuccess = {
            newsDao.insertNews(it.map { it.toEntity() }).run {
                newsDao.insertCacheTime(CacheEntity(requestType, currentTime))
            }

        }
    )
}

private suspend fun isCacheExpired(requestType: NewsRequest): Boolean {
    val cache = newsDao.getCacheData(requestType) ?: return true
    return Clock.System.now().epochSeconds - cache.time > CACHE_EXPIRED
}

override fun getNewsListByIds(ids: List<Int>): Flow<List<News>> {
    return newsDao.getListNewsByIds(ids).map {
        it.map { entity -> entity.toDomain() }
    }
}

override fun getSingleNews(id: Int): Flow<News> {
    return newsDao.getSingleNews(id).map {
        it.toDomain()
    }
}

override suspend fun removeFromFavourites(newsId: Int) {
    newsDao.removeFromFavorite(newsId)
}

override suspend fun addToFavourites(newsId: Int) {
    val news = getSingleNews(newsId).first()
    newsDao.insertFavorite(news.toFavoriteEntity())
}

override suspend fun getNewsSuggestions(query: String): List<String> {
    return newsDao.getSuggestions(query)
}
}