package com.andreich.news.presentation.newslist

import com.andreich.news.domain.model.News
import com.andreich.news.presentation.core.UiIntent

sealed interface NewsListIntent : UiIntent {

    object LoadNextPage : NewsListIntent

    class NewsClick(val news: News) : NewsListIntent
}