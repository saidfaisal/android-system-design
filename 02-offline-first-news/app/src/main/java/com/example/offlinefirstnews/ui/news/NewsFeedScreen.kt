package com.example.offlinefirstnews.ui.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey

@Composable
fun NewsFeedScreen(
    viewModel: NewsFeedViewModel,
    onArticleClick: (String) -> Unit,
    modifier: Modifier
) {
    val articles = viewModel.articles.collectAsLazyPagingItems()
    val refreshState = articles.loadState.refresh

    Column(modifier = Modifier.fillMaxSize()) {
        when (refreshState) {
            is LoadState.Loading if articles.itemCount == 0 -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is LoadState.Error if articles.itemCount == 0 -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Couldn't load news.")

                    Button(
                        onClick = { articles.retry() }
                    ) {
                        Text("Retry")
                    }
                }
            }

            else -> {
                if (refreshState is LoadState.Error) {
                    Text(
                        text = "Couldn't refresh news. Showing saved articles.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        count = articles.itemCount,
                        key = articles.itemKey { it.id }
                    ) { index ->
                        val article = articles[index]
                        if (article != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onArticleClick(article.id)
                                    }
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = article.title,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = article.sourceName.orEmpty(),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    when(articles.loadState.append) {
                        is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        is LoadState.Error -> {
                            item {
                                Button(
                                    onClick = { articles.retry() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Retry loading more")
                                }
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}