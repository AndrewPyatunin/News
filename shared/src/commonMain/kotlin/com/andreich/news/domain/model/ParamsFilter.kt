package com.andreich.news.domain.model

data class ParamsFilter(
    val country: String? = null,
    val language: String? = null,
    val category: String? = null,
    val location: String? = null
)