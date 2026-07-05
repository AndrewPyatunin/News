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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map
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
        return flow {
            emit(value = newsDao.getSearchedNews(
                    param,
                    paramsFilter?.language,
                    paramsFilter?.country,
                    paramsFilter?.category,
                    paramsFilter?.location
                ).map { it.toDomain() }
            )
        }
    }

    override suspend fun updateNews(language: String, country: String): RequestResult {
        return getRequestResult(NewsRequest.TopNews) {
            getTopNewsApiCall(language, country)
        }
    }

    override suspend fun updateSearchedNews(param: String, paramsFilter: ParamsFilter?): RequestResult {
        return getRequestResult(NewsRequest.Search(param)) {
            searchApiCall(param, paramsFilter)
        }
    }

    private suspend fun searchApiCall(param: String, paramsFilter: ParamsFilter?): List<News> {
        val searchParams = paramsFilter?.toSearchParamsDto(cityLookup)
        return newsApi.searchNews(param, searchParams).news.map {
            it.toNews()
        }
    }

    private suspend fun getTopNewsApiCall(language: String, country: String): List<News> {
        return newsApi.getNews(language, country).topNews.map {
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
                newsDao.insertCacheTime(CacheEntity(requestType, currentTime))
                newsDao.insertNews(it.map { it.toEntity() })
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