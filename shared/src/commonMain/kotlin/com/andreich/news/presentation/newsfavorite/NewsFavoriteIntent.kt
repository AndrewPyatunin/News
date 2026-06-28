package com.andreich.news.presentation.newsfavorite

import com.andreich.news.domain.model.News
import com.andreich.news.presentation.core.UiIntent

sealed interface NewsFavoriteIntent : UiIntent {

    data object LoadFavourites : NewsFavoriteIntent

    data class RemoveNews(val news: News) : NewsFavoriteIntent

    data class ClickNews(val news: News) : NewsFavoriteIntent

    data class UndoRemove(val news: News) : NewsFavoriteIntent
}