package com.example.offlinefirstnews.ui.news.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.offlinefirstnews.ui.news.detail.contract.NewsDetailUiState

@Composable
fun NewsDetailScreen(
    uiState: NewsDetailUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            NewsDetailUiState.Loading -> {
                CircularProgressIndicator()
            }
            is NewsDetailUiState.Success -> {
                Text(text = uiState.news.title)
            }
            NewsDetailUiState.NotFound -> {
                Text(text = "News not found")
            }
            NewsDetailUiState.InvalidArgument -> {
                Text(text = "Invalid news ID")
            }
            is NewsDetailUiState.Error -> {
                Text(text = uiState.message)
            }
        }
    }
}