package com.andreich.news.data.datasource

data class SearchParamsDto(
    val country: String,
    val language: String,
    val category: String? = null,
    val location: String? = null
)
