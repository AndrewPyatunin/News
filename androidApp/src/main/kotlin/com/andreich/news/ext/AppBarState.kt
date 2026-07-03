package com.andreich.news.ext

data class AppBarState(
    val title: String = "News",
    val showFilter: Boolean = false,
    val onFilterClick: (() -> Unit)? = null
)