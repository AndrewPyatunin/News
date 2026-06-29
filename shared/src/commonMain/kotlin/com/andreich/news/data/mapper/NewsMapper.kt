package com.andreich.news.data.mapper

import com.andreich.news.database.FavoriteNewsEntity
import com.andreich.news.database.NewsEntity
import com.andreich.news.domain.model.News
import com.andreich.news.network.NewsDto

fun NewsEntity.toDomain(): News {
    return News(
        id = id,
        author = author,
        title = title,
        description = description,
        imageUrl = imageUrl,
        url = url,
        content = content,
        publishedAt = publishedAt,
        category = category,
        sourceCountry = sourceCountry
    )
}

fun NewsDto.toDomain(): News {
    return News(
        id = id,
        author = author,
        title = title,
        description = description,
        imageUrl = imageUrl,
        url = url,
        content = content,
        publishedAt = publishedAt,
        category = category,
        sourceCountry = "" //sourceCountry
    )
}

fun NewsDto.toEntity(): NewsEntity {
    return NewsEntity(
        id = id,
        author = author,
        title = title,
        description = description,
        imageUrl = imageUrl,
        url = url,
        content = content,
        publishedAt = publishedAt,
        category = category,
        sourceCountry = "",//sourceCountry
        requestKey = ""
    )
}

fun News.toEntity(): NewsEntity {
    return NewsEntity(
        id = id,
        author = author,
        title = title,
        description = description,
        imageUrl = imageUrl,
        url = url,
        content = content,
        publishedAt = publishedAt,
        category = category,
        sourceCountry = sourceCountry,
        requestKey = ""
    )
}

fun FavoriteNewsEntity.toNews(): News {
    return News(
        id = id,
        author = author,
        title = title,
        description = description,
        imageUrl = imageUrl,
        url = url,
        content = content,
        publishedAt = publishedAt,
        category = category,
        sourceCountry = sourceCountry
    )
}

fun News.toFavoriteEntity(): FavoriteNewsEntity {
    return FavoriteNewsEntity(
        id = id,
        author = author,
        title = title,
        description = description,
        imageUrl = imageUrl,
        url = url,
        content = content,
        publishedAt = publishedAt,
        category = category,
        sourceCountry = sourceCountry,
        requestKey = ""
    )
}