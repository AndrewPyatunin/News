package com.andreich.news.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchNewsDto(
    val id: Int,
    val title: String? = null,
    val text: String? = null,
    val summary: String? = null,
    val url: String? = null,
    val image: String? = null,
    val video: String? = null,
    @SerialName("publish_date")
    val publishDate: String? = null,
    val author: String? = null,
    val authors: List<String> = listOf(),
    val category: String? = null,
    val language: String? = null,
    @SerialName("source_country")
    val sourceCountry: String? = null,
    val sentiment: Double? = null
)

