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
import com.andreich.news.domain.repository.NewsRepository
import com.andreich.news.network.NewsApi
import com.andreich.news.network.toNews
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class NewsRepositoryImpl(
    private val newsApi: NewsApi,
    private val newsDao: NewsDao,
    private val cityLookup: CityLookup
) : NewsRepository {

    private val CACHE_EXPIRED = 2 * 3_600L

    override fun getNews(): Flow<List<News>> {
        return flow {
            val currentTime = Clock.System.now().epochSeconds
            newsDao.getCacheData(NewsRequest.TopNews)?.run {
                if (currentTime - time > CACHE_EXPIRED) {
                    putNewsInDatabase(currentTime)
                }
            } ?: putNewsInDatabase(currentTime)
            emit(newsDao.getNewsList().map { it.toDomain() })
        }

    }

    private suspend fun putNewsInDatabase(currentTime: Long) {
        newsApi.getNews().topNews.map {
            it.news[0].toNews()
        }.apply {
            newsDao.insertCacheTime(CacheEntity(NewsRequest.TopNews, currentTime))
            newsDao.insertNews(map { it.toEntity() })
        }
    }

    private suspend fun putSearchListInDatabase(
        currentTime: Long,
        param: String,
        paramsFilter: ParamsFilter? = null
    ) {
        val searchParams = paramsFilter?.toSearchParamsDto(cityLookup)
        newsApi.searchNews(param, searchParams).news.map {
            it.toNews()
        }.apply {
            newsDao.insertCacheTime(CacheEntity(NewsRequest.Search(param), currentTime))
            newsDao.insertNews(map { it.toEntity() })
        }
    }

    override fun getFavorites(): Flow<List<News>> {
        return newsDao.getFavorites().map { list -> list.map { it.toNews() } }
    }

    override fun searchNews(param: String, paramsFilter: ParamsFilter?): Flow<List<News>> {
        return flow {
            val currentTime = Clock.System.now().epochSeconds
            val requestType = NewsRequest.Search(param)
            newsDao.getCacheData(requestType)?.apply {
                if (currentTime - time > CACHE_EXPIRED) {
                    putNewsInDatabase(currentTime)
                }
            } ?: putSearchListInDatabase(currentTime, param)
            emit(
                newsDao.getSearchedNews(
                    param,
                    paramsFilter?.language,
                    paramsFilter?.country,
                    paramsFilter?.category,
                    paramsFilter?.location
                ).map { it.toDomain() })
        }
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

    override suspend fun addToFavourites(news: News) {
        newsDao.insertFavorite(news.toFavoriteEntity())
    }
}