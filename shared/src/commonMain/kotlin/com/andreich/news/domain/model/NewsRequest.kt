package com.andreich.news.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface NewsRequest {
    @Serializable
    object TopNews : NewsRequest
    @Serializable
    data class Search(val param: String) : NewsRequest
}