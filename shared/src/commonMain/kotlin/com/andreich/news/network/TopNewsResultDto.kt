package com.andreich.news.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopNewsResultDto(
    @SerialName("top_news")
    val topNews: List<TopNewsListDto> = listOf(),
    val language: String? = null,
    val country: String? = null
)