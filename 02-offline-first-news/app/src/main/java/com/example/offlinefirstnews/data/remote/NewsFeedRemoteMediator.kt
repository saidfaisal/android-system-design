package com.example.offlinefirstnews.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.offlinefirstnews.data.local.AppDatabase
import com.example.offlinefirstnews.data.local.dao.ArticleDao
import com.example.offlinefirstnews.data.local.dao.NewsFeedRemoteKeyDao
import com.example.offlinefirstnews.data.local.entity.ArticleEntity
import com.example.offlinefirstnews.data.local.entity.NewsFeedRemoteKeyEntity
import com.example.offlinefirstnews.data.mapper.toEntity
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@ViewModelScoped
class NewsFeedRemoteMediator @Inject constructor(
    private val api: NewsApiService,
    private val database: AppDatabase,
    private val articleDao: ArticleDao,
    private val remoteKeyDao: NewsFeedRemoteKeyDao
): RemoteMediator<Int, ArticleEntity>() {
    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        val cursor = when(loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> {
                return MediatorResult.Success(
                    endOfPaginationReached = true
                )
            }

            LoadType.APPEND -> {
                val remoteKey = remoteKeyDao.getRemoteKey(FEED_KEY)

                if (
                    remoteKey == null ||
                    !remoteKey.hasMore ||
                    remoteKey.nextCursor == null
                ) {
                    return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }

                remoteKey.nextCursor
            }
        }

        return try {
            val page = api.getNews(
                orderBy = ORDER_BY,
                cursor = cursor
            )

            database.withTransaction {
                articleDao.upsertArticles(
                    page.data.map { it.toEntity() }
                )

                remoteKeyDao.upsertRemoteKey(
                    NewsFeedRemoteKeyEntity(
                        feedKey = FEED_KEY,
                        nextCursor = page.meta.nextCursor,
                        hasMore = page.meta.hasMore,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }

            MediatorResult.Success(
                endOfPaginationReached =
                    page.data.isEmpty()
                            || !page.meta.hasMore
                            || page.meta.nextCursor == null
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private companion object {
        private const val FEED_KEY = "recent"
        private const val ORDER_BY = "recent"
    }
}