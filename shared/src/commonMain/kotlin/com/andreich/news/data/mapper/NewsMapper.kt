package com.andreich.news.data.mapper

import com.andreich.news.database.FavoriteNewsEntity
import com.andreich.news.database.NewsEntity
import com.andreich.news.domain.model.News

fun NewsEntity.toDomain(): News {
    return News(
        id = id,
        author = author,
        title = title,
        description = description,
        imageUrl = imageUrl,
        url = url,
        content = getNewsContent(content, description),
        publishedAt = publishedAt,
        category = category,
        sourceCountry = sourceCountry,
        language = language
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
        content = getNewsContent(content, description),
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

fun getNewsContent(content: String?, summary: String?): String {
    return when(content?.filterBrokenContent()) {
        true -> summary
        false -> content
        else -> ""
    } ?: ""
}
private fun String.filterBrokenContent(): Boolean {
    return this.startsWith("У вас большие запросы!")
}