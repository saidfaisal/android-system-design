package com.example.ratelimiter.domain.ratelimiter

import org.junit.Assert
import org.junit.Test

class FixedWindowAlgorithmTest {
    @Test
    fun `when state is NoWindowYet, request is allowed and window is initialized`() {
        val algorithm = FixedWindowAlgorithm()
        val actual = algorithm.evaluate(
            state = FixedWindowState.NoWindowYet,
            configuration = RateLimitConfiguration(
                limit = 3,
                windowMillis = 10_000
            ),
            nowMillis = 100_000
        )
        val expected = FixedWindowEvaluation(
            decision = RateLimitDecision.Allowed,
            newState = FixedWindowState.ActiveWindow(
                count = 1,
                windowStartMillis = 100_000
            )
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `when state is ActiveWindow and capacity is available, request is allowed`() {
        val algorithm = FixedWindowAlgorithm()
        val actual = algorithm.evaluate(
            state = FixedWindowState.ActiveWindow(
                count = 2,
                windowStartMillis = 100_000
            ),
            configuration = RateLimitConfiguration(
                limit = 3,
                windowMillis = 10_000
            ),
            nowMillis = 105_000
        )

        val expected = FixedWindowEvaluation(
            decision = RateLimitDecision.Allowed,
            newState = FixedWindowState.ActiveWindow(
                count = 3,
                windowStartMillis = 100_000
            )
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `when ActiveWindow is expired, create a new window and allow the request`() {
        val algorithm = FixedWindowAlgorithm()
        val actual = algorithm.evaluate(
            state = FixedWindowState.ActiveWindow(
                count = 3,
                windowStartMillis = 100_000
            ),
            configuration = RateLimitConfiguration(
                limit = 3,
                windowMillis = 10_000
            ),
            nowMillis = 110_000
        )

        val expected = FixedWindowEvaluation(
            decision = RateLimitDecision.Allowed,
            newState = FixedWindowState.ActiveWindow(
                count = 1,
                windowStartMillis = 110_000
            )
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `state is ActiveWindow and capacity is exhausted, request is rejected`() {
        val algorithm = FixedWindowAlgorithm()
        val windowStartMillis = 100_000L
        val windowMillis = 10_000L
        val nowMillis = 105_000L
        val state = FixedWindowState.ActiveWindow(
            count = 3,
            windowStartMillis = windowStartMillis
        )
        val actual = algorithm.evaluate(
            state = state,
            configuration = RateLimitConfiguration(
                limit = 3,
                windowMillis = windowMillis
            ),
            nowMillis = nowMillis
        )

        val expected = FixedWindowEvaluation(
            decision = RateLimitDecision.Rejected(
                retryAfterMillis = windowStartMillis + windowMillis - nowMillis
            ),
            newState = state
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `when request arrives just before window expiration, request is rejected`() {
        val algorithm = FixedWindowAlgorithm()
        val windowStartMillis = 100_000L
        val windowMillis = 10_000L
        val nowMillis = 109_999L
        val state = FixedWindowState.ActiveWindow(
            count = 3,
            windowStartMillis = windowStartMillis
        )
        val actual = algorithm.evaluate(
            state = state,
            configuration = RateLimitConfiguration(
                limit = 3,
                windowMillis = windowMillis
            ),
            nowMillis = nowMillis
        )

        val expected = FixedWindowEvaluation(
            decision = RateLimitDecision.Rejected(
                retryAfterMillis = windowStartMillis + windowMillis - nowMillis
            ),
            newState = state
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `when limit is zero, creating configuration throws IllegalArgumentException`() {
        val exception = Assert.assertThrows(IllegalArgumentException::class.java) {
            RateLimitConfiguration(
                limit = 0,
                windowMillis = 10_000
            )
        }

        Assert.assertEquals(
            "limit must be greater than 0",
            exception.message
        )
    }

    @Test
    fun `when windowMillis is zero, creating configuration throws IllegalArgumentException`() {
        val exception = Assert.assertThrows(IllegalArgumentException::class.java) {
            RateLimitConfiguration(
                limit = 3,
                windowMillis = 0
            )
        }

        Assert.assertEquals(
            "windowMillis must be greater than 0",
            exception.message
        )
    }
}