package com.andreich.news.presentation.newsmap

import com.andreich.news.domain.model.CityItem
import com.andreich.news.presentation.core.UiState

data class NewsMapState(
    val clusterItems: List<CityItem> = emptyList(),
    val isLoading: Boolean = false
) : UiState
