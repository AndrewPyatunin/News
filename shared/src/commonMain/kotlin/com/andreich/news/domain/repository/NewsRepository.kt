package com.andreich.news.domain.repository

import com.andreich.news.domain.model.News
import com.andreich.news.domain.model.ParamsFilter
import com.andreich.news.domain.model.RequestResult
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    fun getNews(language: String?, country: String?, limit: Int): Flow<List<News>>

    fun getFavorites(): Flow<List<News>>

    fun searchNews(param: String, paramsFilter: ParamsFilter? = null): Flow<List<News>>

    suspend fun updateNews(language: String, country: String): RequestResult

    suspend fun updateSearchedNews(param: String, paramsFilter: ParamsFilter?): RequestResult

    fun getNewsListByIds(ids: List<Int>): Flow<List<News>>

    fun getSingleNews(id: Int): Flow<News>

    suspend fun removeFromFavourites(newsId: Int)

    suspend fun addToFavourites(news: News)
}