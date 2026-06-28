package com.andreich.news.presentation.newssearch

import com.andreich.news.domain.model.News
import com.andreich.news.presentation.core.UiState

data class NewsSearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val resultList: List<News> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val expanded: Boolean = false
) : UiState
