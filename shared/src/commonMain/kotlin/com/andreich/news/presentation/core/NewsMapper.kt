package com.andreich.news.presentation.core

import com.andreich.news.domain.model.News

fun News.toNewsArticle(): NewsArticle {
    return NewsArticle(
        id, author, title, description, imageUrl
    )
}