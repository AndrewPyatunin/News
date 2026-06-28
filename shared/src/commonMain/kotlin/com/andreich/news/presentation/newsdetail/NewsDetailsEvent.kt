package com.andreich.news.presentation.newsdetail

import com.andreich.news.presentation.core.UiEvent

sealed interface NewsDetailsEvent : UiEvent {

    class AddToFavoriteSuccess(val message: String) : NewsDetailsEvent

    class ShowError(val message: String) : NewsDetailsEvent
}