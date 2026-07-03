package com.andreich.news.presentation.newsmap

import com.andreich.news.presentation.core.UiIntent

sealed interface NewsMapIntent : UiIntent {

    class StartObserving(val isEnglish: Boolean) : NewsMapIntent

    class ClickItem(val newsIds: List<Int>) : NewsMapIntent
}