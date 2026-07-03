package com.andreich.news.di

import android.content.Context
import com.andreich.news.data.datasource.CityLookup
import com.andreich.news.data.repository.CityRepositoryImpl
import com.andreich.news.data.repository.NewsRepositoryImpl
import com.andreich.news.data.repository.SettingsRepositoryImpl
import com.andreich.news.domain.repository.CityRepository
import com.andreich.news.domain.repository.NewsRepository
import com.andreich.news.domain.repository.SettingsRepository
import org.koin.dsl.module

fun dataModule(context: Context) = module {
    single<NewsRepository> {
        NewsRepositoryImpl(get(), get(), get())
    }
    single(createdAtStart = true) {
        CityLookup(context)
    }
    single<CityRepository> {
        CityRepositoryImpl(get(), get())
    }
    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
}