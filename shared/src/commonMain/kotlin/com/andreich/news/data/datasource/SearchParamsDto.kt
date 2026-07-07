package com.andreich.news.data.datasource

data class SearchParamsDto(
    val country: String? = null,
    val language: String? = null,
    val category: String? = null,
    val location: String? = null
)
