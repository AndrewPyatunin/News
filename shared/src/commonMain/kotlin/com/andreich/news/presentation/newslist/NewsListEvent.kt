package com.andreich.news.presentation.newslist

import com.andreich.news.domain.model.News
import com.andreich.news.presentation.core.UiEvent

sealed interface NewsListEvent : UiEvent{

    class ShowError(val message: String): NewsListEvent

    class NavigateTo(val news: News): NewsListEvent
}
