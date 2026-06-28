package com.andreich.news.presentation.newsfavorite

import com.andreich.news.domain.model.News
import com.andreich.news.presentation.core.UiState

data class NewsFavoriteState(
    val news: List<News> = emptyList(),
    val isLoading: Boolean = false
) : UiState