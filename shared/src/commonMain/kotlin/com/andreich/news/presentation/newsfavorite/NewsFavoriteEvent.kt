package com.andreich.news.presentation.newsfavorite

import com.andreich.news.domain.model.News
import com.andreich.news.presentation.core.UiEvent

sealed interface NewsFavoriteEvent : UiEvent {

    class ShowError(val message: String) : NewsFavoriteEvent

    class NavigateToNewsDetail(val news: News) : NewsFavoriteEvent

    class ShowUndoRemove(val news: News) : NewsFavoriteEvent

    object RemoveSuccess : NewsFavoriteEvent
}