package com.example.offlinefirstnews.ui.news.contract

data class NewsFeedUiState(
    val connectivityStatus: ConnectivityStatus = ConnectivityStatus.Unknown
) {
    enum class ConnectivityStatus {
        Unknown,
        Online,
        Offline
    }
}