package com.andreich.news.domain.repository

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository{

    val suggestions: Flow<List<String>>

    suspend fun saveSearch(query: String)

    suspend fun clearHistory()
}