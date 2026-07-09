package com.andreich.news.presentation.newslist

import com.andreich.news.presentation.core.UiEvent

sealed interface NewsListEvent : UiEvent{

    object SettingsUpdated : NewsListEvent

    class ShowError(val message: String): NewsListEvent

    class NavigateTo(val id: Int): NewsListEvent
}
