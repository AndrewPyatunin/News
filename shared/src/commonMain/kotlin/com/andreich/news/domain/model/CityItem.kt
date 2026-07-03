package com.andreich.news.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CityItem(
    val id: Long,
    val name: String,
    val lat: Double,
    val lng: Double,
    val news: List<News> = emptyList()
)
