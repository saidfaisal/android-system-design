package com.example.offlinefirstnews.ui.news.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NewsDetailRoute(
    viewModel: NewsDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NewsDetailScreen(
        uiState = uiState
    )
}