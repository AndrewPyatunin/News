package com.andreich.news.di

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.text.intl.Locale
import com.andreich.news.data.datasource.CityLookup
import com.andreich.news.data.repository.CityRepositoryImpl
import com.andreich.news.data.repository.NewsRepositoryImpl
import com.andreich.news.data.repository.SettingsRepositoryImpl
import com.andreich.news.domain.model.Country
import com.andreich.news.domain.model.Language
import com.andreich.news.domain.model.UserSettings
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
    single<SettingsRepository>(createdAtStart = true) {
        val isRu = Locale.current.language == Language.RU.name
        SettingsRepositoryImpl(
            get(),
            UserSettings(
                country = if (isRu) Country.RU else Country.US,
                language = if (isRu) Language.RU else Language.EN,
                darkTheme = isDarkTheme(context)
            )
        )
    }
}

private fun isDarkTheme(context: Context): Boolean {
    val config = context.resources.configuration
    return (config.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}