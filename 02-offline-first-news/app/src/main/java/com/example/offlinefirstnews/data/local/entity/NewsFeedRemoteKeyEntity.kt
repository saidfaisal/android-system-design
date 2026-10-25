package com.example.offlinefirstnews.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("news_remote_keys")
data class NewsFeedRemoteKeyEntity(
    @PrimaryKey
    val feedKey: String,
    val nextCursor: String?,
    val hasMore: Boolean,
    val updatedAt: Long
)