package com.andreich.news.presentation.newscitylist

import com.andreich.news.domain.model.News
import com.andreich.news.presentation.core.UiState

data class NewsCityListState(
    val newsList: List<News> = emptyList(),
    val isLoading: Boolean = false
) : UiState
