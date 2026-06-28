package com.andreich.news.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.andreich.news.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SearchHistoryRepositoryImpl(
    val dataStore: DataStore<Preferences>
) : SearchHistoryRepository {

    companion object {
        private const val SEPARATOR = "\u001F"
        private val key = stringPreferencesKey("suggestions")
    }

    override val suggestions: Flow<List<String>>
        get() = dataStore.data.map {
            it[key]?.split(SEPARATOR)?.filter { it.isNotBlank() } ?: emptyList()
        }

    override suspend fun saveSearch(query: String) {
        val current = suggestions.first()
        val updated = buildList {
            add(query)
            addAll(current.filter { it != query })
        }.take(10)
        dataStore.edit {
            it[key] = updated.joinToString(SEPARATOR)
        }
    }

    override suspend fun clearHistory() {
    }
}