package com.example.ratelimiter.domain.ratelimiter.model

data class RateLimitEvaluation<S>(
    val decision: RateLimitDecision,
    val newState: S
)