package com.example.ratelimiter.domain.ratelimiter

sealed interface RateLimitDecision {
    data object Allowed: RateLimitDecision
    data class Rejected(
        val retryAfterMillis: Long?
    ): RateLimitDecision
}