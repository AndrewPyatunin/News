package com.andreich.news.domain.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository{

    val suggestions: Flow<List<String>>

    suspend fun saveSearch(query: String)

    suspend fun clearHistory()
}