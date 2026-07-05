package com.andreich.news.presentation.newscitylist

import com.andreich.news.presentation.core.NewsArticle
import com.andreich.news.presentation.core.UiState

data class NewsCityListState(
    val newsList: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false
) : UiState
