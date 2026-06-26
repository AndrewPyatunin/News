package com.andreich.news.network

import kotlinx.serialization.Serializable

@Serializable
data class NewsDto(
    val id: Int,
    val author: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val url: String,
    val content: String,
    val publishedAt: String,
    val category: String
)
