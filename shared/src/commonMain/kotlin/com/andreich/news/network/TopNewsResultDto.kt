package com.andreich.news.network

import kotlinx.serialization.Serializable

@Serializable
data class TopNewsResultDto(
    val topNews: List<TopNewsListDto> = listOf(),
    val language: String? = null,
    val country: String? = null
)