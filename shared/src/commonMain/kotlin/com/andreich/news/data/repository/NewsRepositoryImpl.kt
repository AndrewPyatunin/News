package com.andreich.news.data.repository

import com.andreich.news.data.mapper.toDomain
import com.andreich.news.data.mapper.toEntity
import com.andreich.news.data.mapper.toFavoriteEntity
import com.andreich.news.data.mapper.toNews
import com.andreich.news.database.CacheEntity
import com.andreich.news.database.NewsDao
import com.andreich.news.domain.model.News
import com.andreich.news.domain.model.NewsRequest
import com.andreich.news.domain.repository.NewsRepository
import com.andreich.news.network.NewsApi
import com.andreich.news.network.toNews
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class NewsRepositoryImpl(
    private val newsApi: NewsApi,
    private val newsDao: NewsDao
) : NewsRepository {

    private val CACHE_EXPIRED = 2 * 3_600L

    override fun getNews(): Flow<List<News>> {
        return flow {
            val currentTime = Clock.System.now().epochSeconds
            newsDao.getCacheData(NewsRequest.TopNews)?.run {
                if (time - currentTime > CACHE_EXPIRED) {
                    emit(buildList(currentTime))
                } else {
                    emit(newsDao.getNews().map { it.toDomain() })
                }
            } ?: emit(buildList(currentTime))
        }

    }

    private suspend fun buildList(currentTime: Long): List<News> {
        return  newsApi.getNews().topNews.map {
            it.news[0].toNews()
        }.apply {
            newsDao.insertCacheTime(CacheEntity(NewsRequest.TopNews, currentTime))
            newsDao.insertNews(map { it.toEntity() })
        }
    }

    override fun getFavorites(): Flow<List<News>> {
        return newsDao.getFavorites().map { list -> list.map { it.toNews() } }
    }

    override fun searchNews(param: String): Flow<List<News>> {
        return flow {
            val currentTime = Clock.System.now().epochSeconds
            val requestType = NewsRequest.Search(param)
            newsDao.getCacheData(requestType)?.apply {
                if (time - currentTime > CACHE_EXPIRED) {
                    emit(buildList(currentTime))
                } else {
                    emit(newsDao.getSearchedNews(param).map { it.toDomain() })

                }
            } ?: emit(buildList(currentTime))

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