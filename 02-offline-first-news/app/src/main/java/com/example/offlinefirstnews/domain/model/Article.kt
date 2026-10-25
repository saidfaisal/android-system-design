package com.example.offlinefirstnews.domain.model

data class Article(
    val id: String,
    val title: String,
    val sourceName: String?,
    val publishedAt: Long
)