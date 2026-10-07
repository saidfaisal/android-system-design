package com.example.ratelimiter.domain.ratelimiter

data class FixedWindowEvaluation(
    val decision: RateLimitDecision,
    val newState: FixedWindowState
)