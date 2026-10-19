package com.example.ratelimiter.di

import com.example.ratelimiter.domain.time.TimeProvider
import com.example.ratelimiter.platform.time.AndroidTimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeModule {
    @Binds
    abstract fun bindTimeProvider(
        implementation: AndroidTimeProvider
    ): TimeProvider
}