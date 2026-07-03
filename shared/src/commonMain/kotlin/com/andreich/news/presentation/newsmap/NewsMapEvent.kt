package com.andreich.news.presentation.newsmap

import com.andreich.news.presentation.core.UiEvent

sealed interface NewsMapEvent : UiEvent {

    class ShowError(val message: String) : NewsMapEvent

    class NavigateToNews(val newsId: Int) : NewsMapEvent

    class NavigateToNewsCityList(val ids: List<Int>) : NewsMapEvent
}