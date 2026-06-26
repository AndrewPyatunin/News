package com.andreich.news.database

import androidx.room.Room

actual class NewsDatabaseFactory {

    actual fun create(): NewsDatabase {
        val dbFile = "news.db"

        return Room.databaseBuilder<NewsDatabase>(
            name = dbFile
        ).build()
    }
}