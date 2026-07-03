package com.andreich.news.presentation.newscitylist

import com.andreich.news.presentation.core.UiEvent

sealed interface NewsCityListEvent : UiEvent {

    class ShowError(val message: String) : NewsCityListEvent

    class NavigateToDetails(val newsId: Int) : NewsCityListEvent
}