package com.andreich.news.database

import android.content.Context
import androidx.room.Room

actual class NewsDatabaseFactory(
    private val context: Context
) {

    actual fun create(): NewsDatabase {
        return Room.databaseBuilder(
            context = context,
            name = "news.db",
            klass = NewsDatabase::class.java
        ).build()
    }
}