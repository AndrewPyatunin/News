package com.andreich.news.presentation.newsdetail

import com.andreich.news.domain.model.News
import com.andreich.news.presentation.core.UiState

data class NewsDetailsState(
    val news: News? = null,
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false,
    val chunks: List<String> = emptyList()
) : UiState