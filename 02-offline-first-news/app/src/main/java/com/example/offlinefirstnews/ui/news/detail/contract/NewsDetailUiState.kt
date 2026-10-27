package com.example.offlinefirstnews.ui.news.detail.contract

import com.example.offlinefirstnews.domain.model.News

sealed interface NewsDetailUiState {
    data object Loading: NewsDetailUiState
    data class Success(
        val news: News
    ): NewsDetailUiState
    data object NotFound: NewsDetailUiState
    data object InvalidArgument: NewsDetailUiState
    data class Error(
        val message: String
    ): NewsDetailUiState
}