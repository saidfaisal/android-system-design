package com.example.offlinefirstnews.ui.news.contract

sealed interface NewsFeedIntent {
    data class ArticleClicked(
        val articleId: String
    ): NewsFeedIntent
} 