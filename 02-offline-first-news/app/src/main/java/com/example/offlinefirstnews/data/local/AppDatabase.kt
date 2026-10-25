package com.example.offlinefirstnews.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.offlinefirstnews.data.local.dao.ArticleDao
import com.example.offlinefirstnews.data.local.entity.ArticleEntity

@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}