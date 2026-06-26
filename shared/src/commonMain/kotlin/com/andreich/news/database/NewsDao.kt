package com.andreich.news.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andreich.news.domain.model.NewsRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Query("SELECT * FROM news")
    suspend fun getNews(): List<NewsEntity>

    @Query("SELECT * FROM favorite_news")
    fun getFavorites(): Flow<List<FavoriteNewsEntity>>

    @Query("SELECT * FROM cache_data")
    suspend fun getCacheData(type: NewsRequest): CacheEntity

    @Query("SELECT * FROM news WHERE :param = '' OR title LIKE '%' || :param || '%' " +
            "OR description LIKE '%' || :param || '%'")
    suspend fun getSearchedNews(param: String): List<NewsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(newsEntity: FavoriteNewsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheTime(cacheEntity: CacheEntity)
}