package com.andreich.news.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andreich.news.domain.model.NewsRequest

@Entity("cache_data")
data class CacheEntity(
    @PrimaryKey
    val type: NewsRequest,
    val time: Long
)