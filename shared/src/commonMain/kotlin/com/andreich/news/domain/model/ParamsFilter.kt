package com.andreich.news.domain.model

data class ParamsFilter(
    val country: String,
    val language: String,
    val category: String? = null,
    val location: String? = null
)