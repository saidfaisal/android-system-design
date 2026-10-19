package com.example.ratelimiter.di

import com.example.ratelimiter.domain.ratelimiter.model.RateLimitConfiguration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RateLimiterModule {
    @Provides
    fun provideInitialConfiguration(): RateLimitConfiguration = RateLimitConfiguration(
        limit = 3,
        windowMillis = 10_000L
    )
}