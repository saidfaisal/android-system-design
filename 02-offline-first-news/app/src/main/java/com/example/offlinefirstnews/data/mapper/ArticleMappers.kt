package com.example.offlinefirstnews.data.mapper

import com.example.offlinefirstnews.data.local.entity.ArticleEntity
import com.example.offlinefirstnews.data.remote.dto.ArticleDto
import com.example.offlinefirstnews.domain.model.Article
import java.time.Instant

fun ArticleDto.toEntity(): ArticleEntity = ArticleEntity(
    id = uuid,
    title = title,
    sourceName = publisher,
    publishedAt = Instant.parse(publishedAt).toEpochMilli()
)

fun ArticleEntity.toDomain(): Article = Article(
    id = id,
    title = title,
    sourceName = sourceName,
    publishedAt = publishedAt
)