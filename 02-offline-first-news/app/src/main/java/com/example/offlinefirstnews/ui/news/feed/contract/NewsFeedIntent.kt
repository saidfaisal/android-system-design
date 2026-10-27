package com.example.offlinefirstnews.ui.news.feed.contract

sealed interface NewsFeedIntent {
    data class ArticleClicked(
        val articleId: String
    ): NewsFeedIntent
} 