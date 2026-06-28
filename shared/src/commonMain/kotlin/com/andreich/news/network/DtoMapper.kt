package com.andreich.news.network

import com.andreich.news.domain.model.News

fun SearchNewsDto.toNews(): News {
    return News(
        id = id,
        author = author ?: "",
        title = title ?: "",
        imageUrl = image ?: "",
        url = url ?: "",
        content = text ?: "",
        publishedAt = publishDate ?: "",
        category = category ?: "",
        description = summary ?: "",
        sourceCountry = sourceCountry ?: ""
    )
}

fun TopNewsDto.toNews(): News {
    return News(
        id = id,
        author = author ?: "",
        title = title ?: "",
        imageUrl = image ?: "",
        url = url ?: "",
        content = text ?: "",
        publishedAt = publishDate ?: "",
        category = category ?: "",
        sourceCountry = sourceCountry ?: "",
        description = summary ?: ""
    )
}