package com.andreich.news.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsDto(
    val id: Int,
    val author: String,
    val title: String,
    val description: String,
    @SerialName("image_url")
    val imageUrl: String,
    val url: String,
    val content: String,
    @SerialName("published_at")
    val publishedAt: String,
    val category: String
)
