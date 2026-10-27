package com.example.offlinefirstnews.di

import android.content.Context
import androidx.room.Room
import com.example.offlinefirstnews.data.local.AppDatabase
import com.example.offlinefirstnews.data.local.dao.NewsDao
import com.example.offlinefirstnews.data.local.dao.NewsFeedRemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "offline_news.db"
    ).build()

    @Provides
    fun provideArticleDao(
        database: AppDatabase
    ): NewsDao = database.articleDao()

    @Provides
    fun provideNewsRemoteKeysDao(
        database: AppDatabase
    ): NewsFeedRemoteKeyDao = database.newsRemoteKeyDao()
}