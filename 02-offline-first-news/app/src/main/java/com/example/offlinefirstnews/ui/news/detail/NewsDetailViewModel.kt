package com.example.offlinefirstnews.ui.news.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.offlinefirstnews.data.repository.NewsRepository
import com.example.offlinefirstnews.ui.news.detail.contract.NewsDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val newsRepository: NewsRepository
): ViewModel() {
    private val _uiState =
        MutableStateFlow<NewsDetailUiState>(
        NewsDetailUiState.Loading
        )
    val uiState: StateFlow<NewsDetailUiState> =
        _uiState.asStateFlow()

    init {
        val newsId = savedStateHandle
            .get<String>("newsId")
            ?.takeIf { it.isNotBlank() }

        if(newsId == null) {
            _uiState.value = NewsDetailUiState.InvalidArgument
        } else {
            observeNews(newsId)
        }
    }

    private fun observeNews(newsId: String) {
        viewModelScope.launch {
            newsRepository.observeNewsById(newsId)
                .map { news ->
                    if (news == null) {
                        NewsDetailUiState.NotFound
                    } else {
                        NewsDetailUiState.Success(news)
                    }
                }
                .catch { cause ->
                    if (cause is CancellationException) {
                        throw cause
                    }

                    emit(NewsDetailUiState.Error(
                        cause.message ?: "Failed to load news"
                    ))
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }
}