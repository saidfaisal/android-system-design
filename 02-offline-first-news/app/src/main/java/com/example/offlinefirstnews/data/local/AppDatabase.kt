package com.example.offlinefirstnews.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.offlinefirstnews.data.local.dao.NewsDao
import com.example.offlinefirstnews.data.local.dao.NewsFeedRemoteKeyDao
import com.example.offlinefirstnews.data.local.entity.NewsEntity
import com.example.offlinefirstnews.data.local.entity.NewsFeedRemoteKeyEntity

@Database(
    entities = [
        NewsEntity::class,
        NewsFeedRemoteKeyEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): NewsDao
    abstract fun newsRemoteKeyDao(): NewsFeedRemoteKeyDao
}