package com.andreich.news.ext

import kotlinx.coroutines.Job

data class NewsFabState(
    val visible: Boolean,
    val onClick: () -> Job
)