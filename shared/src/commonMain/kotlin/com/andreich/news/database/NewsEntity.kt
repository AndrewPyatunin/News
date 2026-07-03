package com.andreich.news.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("news")
data class NewsEntity(
    @PrimaryKey
    val id: Int,
    val requestKey: String,
    val author: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val url: String,
    val content: String,
    val publishedAt: String,
    val category: String,
    val sourceCountry: String,
    val language: String
)
