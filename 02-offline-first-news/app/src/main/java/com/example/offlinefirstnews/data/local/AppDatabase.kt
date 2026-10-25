package com.example.offlinefirstnews.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.offlinefirstnews.data.local.dao.ArticleDao
import com.example.offlinefirstnews.data.local.dao.NewsFeedRemoteKeyDao
import com.example.offlinefirstnews.data.local.entity.ArticleEntity
import com.example.offlinefirstnews.data.local.entity.NewsFeedRemoteKeyEntity

@Database(
    entities = [
        ArticleEntity::class,
        NewsFeedRemoteKeyEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun newsRemoteKeyDao(): NewsFeedRemoteKeyDao
}