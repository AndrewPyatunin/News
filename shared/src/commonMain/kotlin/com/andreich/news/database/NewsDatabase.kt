package com.andreich.news.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NewsEntity::class, FavoriteNewsEntity::class], version = 1, exportSchema = true)
abstract class NewsDatabase: RoomDatabase() {

    abstract fun newsDao(): NewsDao
}