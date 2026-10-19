package com.example.ratelimiter.domain.ratelimiter

import com.example.ratelimiter.domain.time.TimeProvider
import com.example.ratelimiter.domain.ratelimiter.algorithm.fixedwindow.FixedWindowAlgorithm
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitConfiguration
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitDecision
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class RateLimiterTest {
    @Test
    fun `when launch 100 concurrent requests only one is allowed`() = runBlocking {
        val limiter = RateLimiter(
            algorithm = FixedWindowAlgorithm(),
            configuration = RateLimitConfiguration(
                limit = 1,
                windowMillis = 10_000
            ),
            timeProvider = object : TimeProvider {
                override fun elapsedRealtimeMillis(): Long = 100_000
            }
        )

        val ready = Channel<Unit>(capacity = 100)
        val startGate = CompletableDeferred<Unit>()

        val requests = (1..100).map {
            async(Dispatchers.Default) {
                ready.send(Unit)
                startGate.await()

                limiter.allow()
            }
        }

        repeat(100) {
            ready.receive()
        }

        startGate.complete(Unit)

        val results = requests.awaitAll()

        val allowedCount = results.count {
            it == RateLimitDecision.Allowed
        }

        assertEquals(1, allowedCount)
    }

}