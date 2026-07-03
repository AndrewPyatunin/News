package com.andreich.news.network

import com.andreich.news.domain.model.News

fun SearchNewsDto.toNews(): News {
    return News(
        id = id,
        author = author?.decodeNumericEntities() ?: "",
        title = title?.decodeNumericEntities() ?: "",
        description = summary?.decodeNumericEntities() ?: "",
        imageUrl = image ?: "",
        url = url ?: "",
        content = text?.decodeNumericEntities() ?: "",
        publishedAt = publishDate ?: "",
        category = category ?: "",
        sourceCountry = sourceCountry ?: "",
        language = language ?: ""
    )
}

fun TopNewsDto.toNews(): News {
    return News(
        id = id,
        author = author?.decodeNumericEntities() ?: "",
        title = title?.decodeNumericEntities() ?: "",
        description = summary?.decodeNumericEntities() ?: "",
        imageUrl = image ?: "",
        url = url ?: "",
        content = text?.decodeNumericEntities() ?: "",
        publishedAt = publishDate ?: "",
        category = category ?: "",
        sourceCountry = sourceCountry ?: "",
        language = language ?: ""
    )
}