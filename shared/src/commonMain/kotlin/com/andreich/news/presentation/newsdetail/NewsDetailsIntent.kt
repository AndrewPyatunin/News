package com.andreich.news.presentation.newsdetail

import com.andreich.news.presentation.core.UiIntent

sealed interface NewsDetailsIntent : UiIntent {

    class AddToFavorite(val message: String) : NewsDetailsIntent

    class RemoveFromFavorite(val message: String) : NewsDetailsIntent

    class LoadNews(val id: Int) : NewsDetailsIntent

}