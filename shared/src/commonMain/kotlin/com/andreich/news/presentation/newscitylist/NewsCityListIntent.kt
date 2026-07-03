package com.andreich.news.presentation.newscitylist

import com.andreich.news.presentation.core.UiIntent

sealed interface NewsCityListIntent : UiIntent {

    class LoadNewsList(val ids: List<Int>) : NewsCityListIntent

    class NewsClick(val newsId: Int) : NewsCityListIntent
}