package com.andreich.news.presentation.newssearch

import com.andreich.news.presentation.core.UiEvent

sealed interface NewsSearchEvent : UiEvent {

    class NavigateTo(val newsId: Int) : NewsSearchEvent
}