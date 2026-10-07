package com.example.ratelimiter.domain.ratelimiter

data class RateLimitConfiguration(
    val limit: Int,
    val windowMillis: Long
) {
    init {
        require(limit > 0) { "limit must be greater than 0" }
        require(windowMillis > 0) { "windowMillis must be greater than 0" }
    }
}