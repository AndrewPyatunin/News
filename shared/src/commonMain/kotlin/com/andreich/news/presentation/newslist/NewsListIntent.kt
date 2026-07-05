package com.andreich.news.presentation.newslist

import com.andreich.news.domain.model.News
import com.andreich.news.domain.model.UserSettings
import com.andreich.news.presentation.core.UiIntent

sealed interface NewsListIntent : UiIntent {

    object UpdateNews : NewsListIntent

    object ObserveNews : NewsListIntent

    object LoadNextPage : NewsListIntent

    object LoadConfiguration : NewsListIntent

    object ShowMenu : NewsListIntent

    class ConfigureSettings(val userSettings: UserSettings) : NewsListIntent

    class NewsClick(val news: News) : NewsListIntent
}