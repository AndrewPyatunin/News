package com.andreich.news.presentation.newsdetail

import com.andreich.news.domain.model.News
import com.andreich.news.presentation.core.UiState

data class NewsDetailsState(
    val news: News? = null,
    val isLoading: Boolean = false,
) : UiState