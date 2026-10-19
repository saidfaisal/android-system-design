package com.example.ratelimiter.domain.ratelimiter.algorithm

import com.example.ratelimiter.domain.ratelimiter.model.RateLimitEvaluation

interface RateLimitAlgorithm<S, C> {
    fun initState(configuration: C): S
    fun evaluate(
        state: S,
        configuration: C,
        nowMillis: Long
    ): RateLimitEvaluation<S>
}