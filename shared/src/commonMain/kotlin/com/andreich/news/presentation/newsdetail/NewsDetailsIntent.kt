package com.andreich.news.presentation.newsdetail

import com.andreich.news.presentation.core.UiIntent

sealed interface NewsDetailsIntent : UiIntent {

    object AddToFavorite : NewsDetailsIntent

    class LoadNews(val id: Int) : NewsDetailsIntent

}