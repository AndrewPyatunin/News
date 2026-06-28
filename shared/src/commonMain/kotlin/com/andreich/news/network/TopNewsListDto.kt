package com.andreich.news.network

import kotlinx.serialization.Serializable

@Serializable
data class TopNewsListDto(
    val news: List<TopNewsDto> = listOf()
)
