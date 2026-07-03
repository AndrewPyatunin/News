package com.andreich.news.data.datasource

import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class CityDto(
    val name: String,
    val lat: Double,
    val lng: Double,
    val id: Long = Clock.System.now().toEpochMilliseconds()
)