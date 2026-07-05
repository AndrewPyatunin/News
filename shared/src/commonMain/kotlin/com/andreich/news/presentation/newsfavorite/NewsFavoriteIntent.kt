package com.andreich.news.presentation.newsfavorite

import com.andreich.news.presentation.core.UiIntent

sealed interface NewsFavoriteIntent : UiIntent {

    data object LoadFavourites : NewsFavoriteIntent

    data class RemoveNews(val newsId: Int) : NewsFavoriteIntent

    data class ClickNews(val newsId: Int) : NewsFavoriteIntent

    data class UndoRemove(val newsId: Int) : NewsFavoriteIntent
}