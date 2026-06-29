package com.andreich.news.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.andreich.news.data.repository.SearchHistoryRepositoryImpl
import com.andreich.news.domain.repository.SearchHistoryRepository
import com.andreich.news.ext.dataStore
import org.koin.dsl.module

fun dataStoreModule(context: Context) = module {
    single<DataStore<Preferences>> {
        context.dataStore
    }
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }
}