package com.andreich.news.presentation.newssearch

import com.andreich.news.domain.model.ParamsFilter
import com.andreich.news.presentation.core.NewsArticle
import com.andreich.news.presentation.core.UiState

data class NewsSearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val resultList: List<NewsArticle> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val expanded: Boolean = false,
    val popUpMenuShowed: Boolean = false,
    val paramsFilter: ParamsFilter? = null
) : UiState
