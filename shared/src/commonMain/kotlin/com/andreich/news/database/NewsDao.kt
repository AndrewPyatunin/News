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

    @Query("SELECT * FROM news WHERE :newsId = id LIMIT 1")
    fun getSingleNews(newsId: Int): Flow<NewsEntity>

    @Query("SELECT * FROM cache_data WHERE :type = type")
    suspend fun getCacheData(type: NewsRequest): CacheEntity?

    @Query("SELECT * FROM news WHERE :param = '' OR title LIKE '%' || :param || '%' " +
            "OR description LIKE '%' || :param || '%'")
    suspend fun getSearchedNews(param: String): List<NewsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(newsEntity: FavoriteNewsEntity)

    @Query("DELETE FROM favorite_news WHERE id = :newsId")
    suspend fun removeFromFavorite(newsId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheTime(cacheEntity: CacheEntity)

    @Query("SELECT DISTINCT title FROM news WHERE title LIKE :query || '%' LIMIT 7")
    suspend fun getSuggestions(query: String): List<String>
}