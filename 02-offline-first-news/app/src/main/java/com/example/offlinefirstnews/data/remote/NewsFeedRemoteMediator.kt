package com.example.offlinefirstnews.data.remote

import android.os.SystemClock
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.offlinefirstnews.data.local.AppDatabase
import com.example.offlinefirstnews.data.local.dao.NewsDao
import com.example.offlinefirstnews.data.local.dao.NewsFeedRemoteKeyDao
import com.example.offlinefirstnews.data.local.entity.NewsEntity
import com.example.offlinefirstnews.data.local.entity.NewsFeedRemoteKeyEntity
import com.example.offlinefirstnews.data.mapper.toEntity
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@ViewModelScoped
class NewsFeedRemoteMediator @Inject constructor(
    private val api: NewsApiService,
    private val database: AppDatabase,
    private val newsDao: NewsDao,
    private val remoteKeyDao: NewsFeedRemoteKeyDao
) : RemoteMediator<Int, NewsEntity>() {

    override suspend fun initialize(): InitializeAction =
        InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsEntity>
    ): MediatorResult {

        val cursor = when (loadType) {
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

        val requestStartedAt = SystemClock.elapsedRealtime()

        Log.d(
            TAG,
            "Request START: type=$loadType, " +
                    "cursorPresent=${cursor != null}, " +
                    "time=${requestStartedAt}ms"
        )

        return try {
            val page = api.getNews(
                orderBy = ORDER_BY,
                cursor = cursor
            )

            Log.d(
                TAG,
                "Request RESPONSE: type=$loadType, " +
                        "elapsed=${SystemClock.elapsedRealtime() - requestStartedAt}ms, " +
                        "returned=${page.data.size}, " +
                        "hasMore=${page.meta.hasMore}, " +
                        "nextCursorPresent=${page.meta.nextCursor != null}"
            )

            database.withTransaction {
                newsDao.upsertNews(
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
                    page.data.isEmpty() ||
                            !page.meta.hasMore ||
                            page.meta.nextCursor == null
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            val errorBody = e.response()
                ?.errorBody()
                ?.string()

            Log.e(
                TAG,
                "Request FAILED: type=$loadType, " +
                        "http=${e.code()}, " +
                        "elapsed=${SystemClock.elapsedRealtime() - requestStartedAt}ms, " +
                        "body=$errorBody",
                e
            )

            MediatorResult.Error(e)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Failed to load $loadType",
                e
            )

            MediatorResult.Error(e)
        }
    }

    private companion object {
        const val TAG = "NewsFeedRemoteMediator"
        const val FEED_KEY = "recent"
        const val ORDER_BY = "recent"
    }
}