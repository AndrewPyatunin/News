package com.andreich.news.domain.model

data class News(
    val id: Int,
    val author: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val url: String,
    val content: String,
    val publishedAt: String,
    val category: String,
    val sourceCountry: String
)
