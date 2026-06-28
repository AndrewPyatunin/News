package com.andreich.news.network

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultDto(
    val offset: Int? = null,
    val number: Int? = null,
    val available: Int? = null,
    val news: List<SearchNewsDto> = listOf()
)