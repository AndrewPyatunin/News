package com.andreich.news

import android.app.Application
import com.andreich.news.di.dataModule
import com.andreich.news.di.dataStoreModule
import com.andreich.news.di.databaseModule
import com.andreich.news.di.domainModule
import com.andreich.news.di.networkModule
import com.andreich.news.di.presentationModule
import com.andreich.news.di.uiModule
import org.koin.core.context.startKoin

class NewsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val apiKey = BuildConfig.API_KEY
        startKoin {
            modules(
                databaseModule(applicationContext),
                dataModule(applicationContext),
                dataStoreModule(applicationContext),
                domainModule,
                networkModule(apiKey),
                presentationModule,
                uiModule(applicationContext)
            )
        }
    }
}