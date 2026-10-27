package com.example.offlinefirstnews.ui.news.feed.contract

sealed interface NewsFeedEffect {
    data class NavigateToArticle(
        val articleId: String
    ): NewsFeedEffect
}