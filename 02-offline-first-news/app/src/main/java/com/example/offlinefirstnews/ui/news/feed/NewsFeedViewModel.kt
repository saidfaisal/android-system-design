package com.example.offlinefirstnews.ui.news.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.offlinefirstnews.data.repository.NewsRepository
import com.example.offlinefirstnews.domain.model.News
import com.example.offlinefirstnews.ui.news.feed.contract.NewsFeedEffect
import com.example.offlinefirstnews.ui.news.feed.contract.NewsFeedIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsFeedViewModel @Inject constructor(
    newsRepository: NewsRepository
): ViewModel() {
    private val _effects = Channel<NewsFeedEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val news: Flow<PagingData<News>> = newsRepository
        .getNews()
        .cachedIn(viewModelScope)

    fun onIntent(intent: NewsFeedIntent) {
        when(intent) {
            is NewsFeedIntent.ArticleClicked -> {
                viewModelScope.launch {
                    _effects.send(
                        NewsFeedEffect.NavigateToArticle(
                            articleId = intent.articleId
                        )
                    )
                }
            }
        }
    }
}