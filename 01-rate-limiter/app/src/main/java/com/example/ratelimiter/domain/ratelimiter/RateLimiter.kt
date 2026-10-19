package com.example.ratelimiter.domain.ratelimiter

import com.example.ratelimiter.domain.time.TimeProvider
import com.example.ratelimiter.domain.ratelimiter.algorithm.RateLimitAlgorithm
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitDecision
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RateLimiter<S,C>(
    private val algorithm: RateLimitAlgorithm<S, C>,
    private val configuration: C,
    private val timeProvider: TimeProvider
) {
    private val mutex = Mutex()
    private var state = algorithm.initState(configuration)

    suspend fun allow(): RateLimitDecision = mutex.withLock {
        val evaluation = algorithm.evaluate(
            state = state,
            configuration = configuration,
            nowMillis = timeProvider.elapsedRealtimeMillis()
        )
        state = evaluation.newState
        evaluation.decision
    }
}