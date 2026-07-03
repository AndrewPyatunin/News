package com.andreich.news.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [NewsEntity::class, FavoriteNewsEntity::class, CacheEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(NewsConverter::class)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao
}