package com.example.offlinefirstnews.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.offlinefirstnews.data.local.entity.NewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query(
        """
            SELECT * FROM news 
            ORDER BY publishedAt DESC, id ASC
        """
    )
    fun pagingSource(): PagingSource<Int, NewsEntity>

    @Query("SELECT * FROM news WHERE id = :id")
    fun observeNewsById(id: String): Flow<NewsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNews(news: List<NewsEntity>)

    @Query(
        """
            DELETE FROM news
            WHERE id NOT IN (
                SELECT id FROM news
                ORDER BY publishedAt DESC, id ASC
                LIMIT 20
            )
        """
    )
    suspend fun deleteOrderNews()
}