package com.example.offlinefirstnews.data.remote

import com.example.offlinefirstnews.data.remote.dto.ArticlePageDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("news")
    suspend fun getNews(
        @Query("order_by")
        orderBy: String = "recent",
        @Query("cursor")
        cursor: String? = null
    ): ArticlePageDto
}