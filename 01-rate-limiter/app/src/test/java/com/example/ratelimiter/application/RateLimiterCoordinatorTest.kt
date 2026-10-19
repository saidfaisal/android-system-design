package com.example.ratelimiter.application

import com.example.ratelimiter.domain.ratelimiter.model.RateLimitConfiguration
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitDecision
import com.example.ratelimiter.domain.time.TimeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private class BlockingSecondCallTimeProvider: TimeProvider {
    private val callCount = AtomicInteger()

    val secondCallStarted = CountDownLatch(1)
    val releaseSecondCall = CountDownLatch(1)

    override fun elapsedRealtimeMillis(): Long {
        if (callCount.incrementAndGet() == 2) {
            secondCallStarted.countDown()

            check(releaseSecondCall.await(10, TimeUnit.SECONDS)) {
                "Timed out waiting for the test to release the clock"
            }
        }

        return 100_000L
    }
}

class RateLimiterCoordinatorTest {
    @Test
    fun `reset after configuration update preserves configuration and clears state`() = runBlocking {
        val timeProvider = object : TimeProvider {
            override fun elapsedRealtimeMillis(): Long = 100_000L
        }

        val coordinator = RateLimiterCoordinator(
            initialConfiguration = RateLimitConfiguration(
                limit = 1,
                windowMillis = 10_000
            ),
            timeProvider = timeProvider
        )

        Assert.assertEquals(
            RateLimitDecision.Allowed,
            coordinator.allow()
        )

        coordinator.updateConfiguration(
            RateLimitConfiguration(
                limit = 2,
                windowMillis = 30_000
            )
        )

        Assert.assertEquals(
            RateLimitDecision.Allowed,
            coordinator.allow()
        )

        Assert.assertEquals(
            RateLimitDecision.Allowed,
            coordinator.allow()
        )

        Assert.assertTrue(
            coordinator.allow() is RateLimitDecision.Rejected
        )

        coordinator.reset()

        Assert.assertEquals(
            RateLimitDecision.Allowed,
            coordinator.allow()
        )

        Assert.assertEquals(
            RateLimitDecision.Allowed,
            coordinator.allow()
        )

        Assert.assertEquals(
            true,
            coordinator.allow() is RateLimitDecision.Rejected
        )
    }

    @Test
    fun `in flight request uses old configuration after update`() = runBlocking {
        val timeProvider = BlockingSecondCallTimeProvider()

        val coordinator = RateLimiterCoordinator(
            initialConfiguration = RateLimitConfiguration(
                limit = 1,
                windowMillis = 10_000
            ),
            timeProvider = timeProvider
        )

        Assert.assertEquals(
            RateLimitDecision.Allowed,
            coordinator.allow()
        )

        val oldRequest = async(Dispatchers.Default) {
            coordinator.allow()
        }

        try {
            Assert.assertTrue(
                timeProvider.secondCallStarted.await(
                    5,
                    TimeUnit.SECONDS
                )
            )

            coordinator.updateConfiguration(
                RateLimitConfiguration(
                    limit = 2,
                    windowMillis = 30_000
                )
            )

            Assert.assertEquals(
                RateLimitDecision.Allowed,
                coordinator.allow()
            )
        } finally {
            timeProvider.releaseSecondCall.countDown()
        }

        Assert.assertTrue(
            oldRequest.await() is RateLimitDecision.Rejected
        )
    }

    @Test
    fun `when configuration changes new limiter starts with fresh state`() = runBlocking {
        val timeProvider = object : TimeProvider {
            override fun elapsedRealtimeMillis(): Long = 100_000L
        }

        val coordinator = RateLimiterCoordinator(
            initialConfiguration = RateLimitConfiguration(
                limit = 1,
                windowMillis = 10_000
            ),
            timeProvider = timeProvider
        )

        Assert.assertEquals(
            RateLimitDecision.Allowed,
            coordinator.allow()
        )

        Assert.assertEquals(
            true,
            coordinator.allow() is RateLimitDecision.Rejected
        )

        coordinator.updateConfiguration(
            RateLimitConfiguration(
                limit = 2,
                windowMillis = 30_000
            )
        )

        Assert.assertEquals(
            RateLimitDecision.Allowed,
            coordinator.allow()
        )

        Assert.assertEquals(
            RateLimitDecision.Allowed,
            coordinator.allow()
        )

        Assert.assertEquals(
            true,
            coordinator.allow() is RateLimitDecision.Rejected
        )
    }
}