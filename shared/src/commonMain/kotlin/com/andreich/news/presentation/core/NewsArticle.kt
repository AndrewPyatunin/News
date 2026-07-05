package com.andreich.news.presentation.core

import androidx.compose.runtime.Immutable

@Immutable
data class NewsArticle(
    val id: Int,
    val author: String,
    val title: String,
    val description: String,
    val imageUrl: String,
)