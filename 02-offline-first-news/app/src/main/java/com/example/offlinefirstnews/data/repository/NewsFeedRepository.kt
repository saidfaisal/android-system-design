package com.example.offlinefirstnews.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.offlinefirstnews.data.local.dao.ArticleDao
import com.example.offlinefirstnews.data.mapper.toDomain
import com.example.offlinefirstnews.data.remote.NewsFeedRemoteMediator
import com.example.offlinefirstnews.domain.model.Article
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class NewsFeedRepository @Inject constructor(
    private val articleDao: ArticleDao,
    private val remoteMediator: NewsFeedRemoteMediator
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getNews(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 3,
                enablePlaceholders = false
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                articleDao.pagingSource()
            }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                entity.toDomain()
            }
        }
    }
}