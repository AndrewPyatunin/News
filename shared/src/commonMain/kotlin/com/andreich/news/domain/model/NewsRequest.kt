package com.andreich.news.domain.model

sealed interface NewsRequest {
    object TopNews : NewsRequest

    data class Search(val param: String) : NewsRequest
}