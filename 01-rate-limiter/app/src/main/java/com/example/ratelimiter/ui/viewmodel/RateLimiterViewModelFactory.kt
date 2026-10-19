package com.example.ratelimiter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ratelimiter.application.RateLimiterCoordinator
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitConfiguration
import com.example.ratelimiter.domain.time.TimeProvider

class RateLimiterViewModelFactory(
    private val initialConfiguration: RateLimitConfiguration,
    private val timeProvider: TimeProvider
): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RateLimiterViewModel::class.java)) {
            val coordinator = RateLimiterCoordinator(
                initialConfiguration = initialConfiguration,
                timeProvider = timeProvider
            )

            @Suppress("UNCHECKED_CAST")
            return RateLimiterViewModel(
                coordinator = coordinator,
                initialConfiguration = initialConfiguration
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown Viewmodel class: ${modelClass.name}"
        )
    }
}