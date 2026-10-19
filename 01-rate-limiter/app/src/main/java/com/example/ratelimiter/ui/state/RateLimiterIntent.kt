package com.example.ratelimiter.ui.state

sealed interface RateLimiterIntent {
    data object SimulateRequest: RateLimiterIntent
    data object Reset: RateLimiterIntent
    data object ApplyConfiguration: RateLimiterIntent
    data class SetRequestLimit(
        val limitInput: String
    ): RateLimiterIntent
    data class SetRequestWindow(
        val windowSecondsInput: String
    ): RateLimiterIntent
}