package com.andreich.news.domain.repository

import com.andreich.news.domain.model.News
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    fun getNews(): Flow<List<News>>

    fun getFavorites(): Flow<List<News>>

    fun searchNews(param: String): Flow<List<News>>

    suspend fun addToFavourites(news: News)
}