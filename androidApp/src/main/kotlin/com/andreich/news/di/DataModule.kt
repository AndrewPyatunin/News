package com.andreich.news.di

import com.andreich.news.data.repository.NewsRepositoryImpl
import com.andreich.news.domain.repository.NewsRepository
import org.koin.dsl.module

val dataModule = module {
    single<NewsRepository> {
        NewsRepositoryImpl(get(), get())
    }
}