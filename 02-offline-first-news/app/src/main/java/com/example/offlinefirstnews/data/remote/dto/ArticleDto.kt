package com.example.offlinefirstnews.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDto(
    val uuid: String,
    val title: String,
    @SerialName("published_at")
    val publishedAt: String,
    val publisher: String
)