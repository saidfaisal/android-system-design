package com.example.ratelimiter.application

import com.example.ratelimiter.domain.ratelimiter.RateLimiter
import com.example.ratelimiter.domain.ratelimiter.algorithm.fixedwindow.FixedWindowAlgorithm
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitConfiguration
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitDecision
import com.example.ratelimiter.domain.time.TimeProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RateLimiterCoordinator(
    initialConfiguration: RateLimitConfiguration,
    private val timeProvider: TimeProvider
) {
    private val mutex: Mutex = Mutex()

    private var currentConfiguration = initialConfiguration
    private var activeLimiter = createLimiter(initialConfiguration)

    suspend fun allow(): RateLimitDecision {
        val limitForTheRequest = mutex.withLock {
            activeLimiter
        }

        return limitForTheRequest.allow()
    }

    suspend fun updateConfiguration(
        configuration: RateLimitConfiguration
    ) {
        mutex.withLock {
            currentConfiguration = configuration
            activeLimiter = createLimiter(currentConfiguration)
        }
    }

    suspend fun reset() {
        mutex.withLock {
            activeLimiter = createLimiter(currentConfiguration)
        }
    }

    private fun createLimiter(
        configuration: RateLimitConfiguration
    ) = RateLimiter(
        algorithm = FixedWindowAlgorithm(),
        configuration = configuration,
        timeProvider = timeProvider
    )
}