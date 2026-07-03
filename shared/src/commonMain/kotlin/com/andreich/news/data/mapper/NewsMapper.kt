package com.andreich.news.data.mapper

import com.andreich.news.database.FavoriteNewsEntity
import com.andreich.news.database.NewsEntity
import com.andreich.news.domain.model.News
import com.andreich.news.network.NewsDto

fun NewsEntity.toDomain(): News {
    return News(
        id = id,
        author = author.decodeNumericEntities(),
        title = title.decodeNumericEntities(),
        description = description.decodeNumericEntities(),
        imageUrl = imageUrl,
        url = url,
        content = content.decodeNumericEntities(),
        publishedAt = publishedAt,
        category = category,
        sourceCountry = sourceCountry,
        language = language
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
        sourceCountry = "sourceCountry",
        language = "language"
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
        requestKey = "",
        language = ""
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
        requestKey = "",
        language = language
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
        sourceCountry = sourceCountry,
        language = language
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
        language = language,
        requestKey = ""
    )
}

internal fun String.decodeNumericEntities(): String =
    replace(Regex("&#(\\d+);")) { match ->
        val codePoint = match.groupValues[1].toIntOrNull()
        codePoint?.toChar()?.toString() ?: match.value
    }