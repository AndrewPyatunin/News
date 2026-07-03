package com.andreich.news.di

import android.content.Context
import com.andreich.news.database.NewsDao
import com.andreich.news.database.NewsDatabase
import com.andreich.news.database.NewsDatabaseFactory
import org.koin.dsl.module

fun databaseModule(context: Context) = module {

    single<NewsDatabaseFactory> {
        NewsDatabaseFactory(context)
    }
    single<NewsDatabase> {
        get<NewsDatabaseFactory>().create()
    }
    single<NewsDao> {
        get<NewsDatabase>().newsDao()
    }
}