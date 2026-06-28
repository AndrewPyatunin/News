package com.andreich.news.presentation.newssearch

import com.andreich.news.domain.model.News
import com.andreich.news.presentation.core.UiEvent

sealed interface NewsSearchEvent : UiEvent {

    class ShowError(val message: String) : NewsSearchEvent

    class NavigateTo(val news: News) : NewsSearchEvent
}