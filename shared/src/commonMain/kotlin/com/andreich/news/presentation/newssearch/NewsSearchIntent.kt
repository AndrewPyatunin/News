package com.andreich.news.presentation.newssearch

import com.andreich.news.domain.model.News
import com.andreich.news.domain.model.ParamsFilter
import com.andreich.news.presentation.core.UiIntent

sealed interface NewsSearchIntent : UiIntent {

    object FilterMenuClick : NewsSearchIntent

    class SaveFilterParams(val paramsFilter: ParamsFilter) : NewsSearchIntent

    class SearchNews(val param: String) : NewsSearchIntent

    class ExpandedChanged(val expanded: Boolean) : NewsSearchIntent

    class QueryChanged(val query: String) : NewsSearchIntent

    class NewsClick(val news: News) : NewsSearchIntent

    class SuggestionClicked(val suggestion: String) : NewsSearchIntent

    object ClearQuery : NewsSearchIntent
}