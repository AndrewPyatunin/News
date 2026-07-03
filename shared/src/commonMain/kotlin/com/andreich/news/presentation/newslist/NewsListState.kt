package com.andreich.news.presentation.newslist

import com.andreich.news.domain.model.News
import com.andreich.news.domain.model.UserSettings
import com.andreich.news.presentation.core.UiState

data class NewsListState(
    val newsList: List<News> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val menuExpanded: Boolean = false,
    val userSettings: UserSettings? = null
): UiState


