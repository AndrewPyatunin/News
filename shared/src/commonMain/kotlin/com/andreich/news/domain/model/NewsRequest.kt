package com.andreich.news.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface NewsRequest {
    @Serializable
    data class TopNewsRu(val country: String) : NewsRequest

    @Serializable
    data class TopNewsEng(val country: String) : NewsRequest

    @Serializable
    data class SearchRu(val param: String, val country: String) : NewsRequest

    @Serializable
    data class SearchEng(val param: String, val country: String) : NewsRequest
}