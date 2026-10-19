package com.example.ratelimiter.viewmodel

import com.example.ratelimiter.application.RateLimiterCoordinator
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitConfiguration
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitDecision
import com.example.ratelimiter.domain.time.TimeProvider
import com.example.ratelimiter.ui.state.RateLimiterIntent
import com.example.ratelimiter.ui.state.RequestUiState
import com.example.ratelimiter.ui.viewmodel.RateLimiterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class RateLimiterViewmodelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when request limit is invalid then show error and preserve configuration`() {
        val initialConfiguration = RateLimitConfiguration(
            limit = 3,
            windowMillis = 10_000L
        )

        val coordinator = RateLimiterCoordinator(
            initialConfiguration = initialConfiguration,
            timeProvider = FakeTimeProvider()
        )

        val viewModel = RateLimiterViewModel(
            coordinator = coordinator,
            initialConfiguration = initialConfiguration
        )

        viewModel.onIntent(
            RateLimiterIntent.SetRequestLimit("0")
        )
        viewModel.onIntent(
            RateLimiterIntent.ApplyConfiguration
        )

        val state = viewModel.uiState.value

        Assert.assertEquals(
            "Request limit must be a positive integer.",
            state.configurationError
        )

        Assert.assertEquals(
            initialConfiguration,
            state.configuration
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when request is simulated then UI state becomes completed`() = runTest {
        val configuration = RateLimitConfiguration(
            limit = 3,
            windowMillis = 10_000L
        )

        val coordinator = RateLimiterCoordinator(
            initialConfiguration = configuration,
            timeProvider = FakeTimeProvider()
        )

        val viewModel = RateLimiterViewModel(
            coordinator = coordinator,
            initialConfiguration = configuration
        )

        // Act
        viewModel.onIntent(RateLimiterIntent.SimulateRequest)

        // The coroutine hasn't run yet.
        Assert.assertEquals(
            RequestUiState.Processing,
            viewModel.uiState.value.requestState
        )

        // Execute pending coroutine work.
        advanceUntilIdle()

        // Assert
        Assert.assertEquals(
            RequestUiState.Completed(RateLimitDecision.Allowed),
            viewModel.uiState.value.requestState
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when reset is triggered then request state becomes idle and configuration is preserved`() = runTest {
        val configuration = RateLimitConfiguration(
            limit = 3,
            windowMillis = 10_000L
        )

        val coordinator = RateLimiterCoordinator(
            initialConfiguration = configuration,
            timeProvider = FakeTimeProvider()
        )

        val viewModel = RateLimiterViewModel(
            coordinator = coordinator,
            initialConfiguration = configuration
        )

        // First, complete a request.
        viewModel.onIntent(RateLimiterIntent.SimulateRequest)
        advanceUntilIdle()

        Assert.assertEquals(
            RequestUiState.Completed(RateLimitDecision.Allowed),
            viewModel.uiState.value.requestState
        )

        // Act: reset the rate limiter.
        viewModel.onIntent(RateLimiterIntent.Reset)
        advanceUntilIdle()

        // Assert: reset clears the result but keeps the configuration.
        Assert.assertEquals(
            RequestUiState.Idle,
            viewModel.uiState.value.requestState
        )

        Assert.assertEquals(
            configuration,
            viewModel.uiState.value.configuration
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when input changes during apply then preserve the newer draft`() = runTest {
        val configuration = RateLimitConfiguration(
            limit = 3,
            windowMillis = 10_000L
        )

        val coordinator = RateLimiterCoordinator(
            initialConfiguration = configuration,
            timeProvider = FakeTimeProvider()
        )

        val viewModel = RateLimiterViewModel(
            coordinator = coordinator,
            initialConfiguration = configuration
        )

        // Arrange: enter a new configuration.
        viewModel.onIntent(RateLimiterIntent.SetRequestLimit("5"))

        // Act: start applying the limit of 5.
        viewModel.onIntent(RateLimiterIntent.ApplyConfiguration)

        // Edit the input before the coroutine completes.
        viewModel.onIntent(RateLimiterIntent.SetRequestLimit("10"))

        // Allow the pending Apply operation to finish.
        advanceUntilIdle()

        // Assert: configuration 5 was applied.
        Assert.assertEquals(
            5,
            viewModel.uiState.value.configuration.limit
        )

        // The newer input must not be overwritten.
        Assert.assertEquals(
            "10",
            viewModel.uiState.value.limitInput
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when multiple configurations are applied quickly then latest configuration wins`() = runTest {
        val initialConfiguration = RateLimitConfiguration(
            limit = 3,
            windowMillis = 10_000L
        )

        val coordinator = RateLimiterCoordinator(
            initialConfiguration = initialConfiguration,
            timeProvider = FakeTimeProvider()
        )

        val viewModel = RateLimiterViewModel(
            coordinator = coordinator,
            initialConfiguration = initialConfiguration
        )

        // First Apply: limit 5.
        viewModel.onIntent(
            RateLimiterIntent.SetRequestLimit("5")
        )
        viewModel.onIntent(
            RateLimiterIntent.ApplyConfiguration
        )

        // Second Apply: limit 7, before the first operation runs.
        viewModel.onIntent(
            RateLimiterIntent.SetRequestLimit("7")
        )
        viewModel.onIntent(
            RateLimiterIntent.ApplyConfiguration
        )

        // Run the queued operations.
        advanceUntilIdle()

        // The latest configuration should win.
        Assert.assertEquals(
            7,
            viewModel.uiState.value.configuration.limit
        )

        Assert.assertEquals(
            "7",
            viewModel.uiState.value.limitInput
        )
    }

    private class FakeTimeProvider: TimeProvider {
        override fun elapsedRealtimeMillis(): Long = 0L
    }
}