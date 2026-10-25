package com.example.offlinefirstnews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.offlinefirstnews.data.local.entity.NewsFeedRemoteKeyEntity

@Dao
interface NewsFeedRemoteKeyDao {
    @Query("""
        SELECT * FROM news_remote_keys
        WHERE feedKey = :feedKey
    """)
    suspend fun getRemoteKey(
        feedKey: String
    ): NewsFeedRemoteKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRemoteKey(
        remoteKey: NewsFeedRemoteKeyEntity
    )

    @Query("""
        DELETE FROM news_remote_keys
        WHERE feedKey = :feedKey
    """)
    suspend fun deleteRemoteKey(
        feedKey: String
    )
}