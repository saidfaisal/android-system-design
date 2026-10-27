package com.example.offlinefirstnews.ui.news.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.offlinefirstnews.ui.news.feed.contract.NewsFeedEffect
import com.example.offlinefirstnews.ui.news.feed.contract.NewsFeedIntent

@Composable
fun NewsFeedRoute(
    viewModel: NewsFeedViewModel,
    onArticleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val news = viewModel.news.collectAsLazyPagingItems()
    val currentOnArticleClick = rememberUpdatedState(onArticleClick)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when(effect) {
                is NewsFeedEffect.NavigateToArticle -> {
                    currentOnArticleClick.value(effect.articleId)
                }
            }
        }
    }

    NewsFeedScreen(
        news = news,
        onArticleClick = { articleId ->
            viewModel.onIntent(
                NewsFeedIntent.ArticleClicked(articleId)
            )
        },
        onRetry = {
            news.retry()
        },
        modifier = modifier
    )
}