package com.andreich.news.presentation.newsfavorite

import com.andreich.news.presentation.core.UiEvent

sealed interface NewsFavoriteEvent : UiEvent {

    class NavigateToNewsDetail(val newsId: Int) : NewsFavoriteEvent

    class ShowUndoRemove(val newsId: Int) : NewsFavoriteEvent

    object RemoveSuccess : NewsFavoriteEvent
}