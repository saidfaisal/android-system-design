package com.example.offlinefirstnews.ui.news.contract

sealed interface NewsFeedEffect {
    data class NavigateToArticle(
        val articleId: String
    ): NewsFeedEffect
}