package com.example.ratelimiter.domain.time

interface TimeProvider {
    fun elapsedRealtimeMillis(): Long
}