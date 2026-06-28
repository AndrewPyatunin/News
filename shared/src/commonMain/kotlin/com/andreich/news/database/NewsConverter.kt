package com.andreich.news.database

import androidx.room.TypeConverter
import com.andreich.news.domain.model.NewsRequest
import kotlinx.serialization.json.Json

class NewsConverter {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @TypeConverter
    fun fromNewsRequestToString(value: NewsRequest?): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun fromStringToNewsRequest(value: String?): NewsRequest? {
        if (value == null) return null
        return json.decodeFromString(value)
    }
}