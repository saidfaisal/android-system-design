package com.example.ratelimiter.ui.state

import com.example.ratelimiter.domain.ratelimiter.model.RateLimitDecision

sealed interface RequestUiState {
    data object Idle: RequestUiState
    data object Processing: RequestUiState
    data class Completed(
        val decision: RateLimitDecision
    ): RequestUiState
}