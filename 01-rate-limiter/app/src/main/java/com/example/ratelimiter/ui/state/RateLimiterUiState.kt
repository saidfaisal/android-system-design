package com.example.ratelimiter.ui.state

import com.example.ratelimiter.domain.ratelimiter.model.RateLimitConfiguration

data class RateLimiterUiState(
    val configuration: RateLimitConfiguration,
    val requestState: RequestUiState = RequestUiState.Idle,
    val limitInput: String = configuration.limit.toString(),
    val windowSecondsInput: String = (configuration.windowMillis / 1000L).toString(),
    val configurationError: String? = null
)