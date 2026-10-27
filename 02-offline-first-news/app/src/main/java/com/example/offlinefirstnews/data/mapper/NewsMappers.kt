package com.example.offlinefirstnews.data.mapper

import com.example.offlinefirstnews.data.local.entity.NewsEntity
import com.example.offlinefirstnews.data.remote.dto.NewsDto
import com.example.offlinefirstnews.domain.model.News
import java.time.Instant

fun NewsDto.toEntity(): NewsEntity = NewsEntity(
    id = uuid,
    title = title,
    sourceName = publisher,
    publishedAt = Instant.parse(publishedAt).toEpochMilli()
)

fun NewsEntity.toDomain(): News = News(
    id = id,
    title = title,
    sourceName = sourceName,
    publishedAt = publishedAt
)