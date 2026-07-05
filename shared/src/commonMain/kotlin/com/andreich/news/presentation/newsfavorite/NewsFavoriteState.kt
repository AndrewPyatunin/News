package com.andreich.news.presentation.newsfavorite

import com.andreich.news.presentation.core.NewsArticle
import com.andreich.news.presentation.core.UiState

data class NewsFavoriteState(
    val news: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false
) : UiState