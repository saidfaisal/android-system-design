package com.example.offlinefirstnews.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.offlinefirstnews.data.repository.NewsFeedRepository
import com.example.offlinefirstnews.domain.model.Article
import com.example.offlinefirstnews.ui.news.contract.NewsFeedEffect
import com.example.offlinefirstnews.ui.news.contract.NewsFeedIntent
import com.example.offlinefirstnews.ui.news.contract.NewsFeedUiState
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class NewsFeedViewModel @Inject constructor(
    private val newsFeedRepository: NewsFeedRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(NewsFeedUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<NewsFeedEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val articles: Flow<PagingData<Article>> = newsFeedRepository
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