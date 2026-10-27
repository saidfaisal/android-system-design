package com.example.offlinefirstnews.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.offlinefirstnews.data.local.dao.NewsDao
import com.example.offlinefirstnews.data.mapper.toDomain
import com.example.offlinefirstnews.data.remote.NewsFeedRemoteMediator
import com.example.offlinefirstnews.domain.model.News
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class NewsRepository @Inject constructor(
    private val newsDao: NewsDao,
    private val remoteMediator: NewsFeedRemoteMediator
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getNews(): Flow<PagingData<News>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                prefetchDistance = 1,
                enablePlaceholders = false
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                newsDao.pagingSource()
            }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                entity.toDomain()
            }
        }
    }

    fun observeNewsById(id: String): Flow<News?> =
        newsDao.observeNewsById(id)
            .map { entity ->
                entity?.toDomain()
            }
}