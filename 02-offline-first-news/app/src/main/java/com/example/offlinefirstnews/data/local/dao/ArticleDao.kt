package com.example.offlinefirstnews.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.offlinefirstnews.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query(
        """
            SELECT * FROM articles 
            ORDER BY publishedAt DESC, id ASC
        """
    )
    fun pagingSource(): PagingSource<Int, ArticleEntity>

    @Query("SELECT * FROM articles WHERE id = :articleId")
    fun observeArticleById(articleId: String): Flow<ArticleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArticles(articles: List<ArticleEntity>)

    @Query(
        """
            DELETE FROM articles
            WHERE id NOT IN (
                SELECT id FROM articles
                ORDER BY publishedAt DESC, id ASC
                LIMIT 20
            )
        """
    )
    suspend fun deleteOrderArticles()
}