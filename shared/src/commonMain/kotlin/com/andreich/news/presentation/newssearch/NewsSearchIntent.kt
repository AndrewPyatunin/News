package com.andreich.news.presentation.newssearch

import com.andreich.news.domain.model.News
import com.andreich.news.presentation.core.UiIntent

sealed interface NewsSearchIntent : UiIntent {

    class SearchNews(val param: String) : NewsSearchIntent

    class ExpandedChanged(val expanded: Boolean) : NewsSearchIntent

    class QueryChanged(val query: String) : NewsSearchIntent

    class NewsClick(val news: News) : NewsSearchIntent

    class SuggestionClicked(val suggestion: String) : NewsSearchIntent

    object ClearQuery : NewsSearchIntent
}