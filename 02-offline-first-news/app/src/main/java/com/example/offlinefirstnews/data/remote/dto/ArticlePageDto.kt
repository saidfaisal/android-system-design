package com.example.offlinefirstnews.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticlePageDto(
    val data: List<ArticleDto>,
    val meta: ArticlePageMetaDto
) {
    @Serializable
    data class ArticlePageMetaDto(
        @SerialName("page_size")
        val pageSize: Int,
        val returned: Int,
        @SerialName("has_more")
        val hasMore: Boolean,
        @SerialName("next_cursor")
        val nextCursor: String?
    )
}