package com.example.ratelimiter.platform.time

import android.os.SystemClock
import com.example.ratelimiter.domain.time.TimeProvider

class AndroidTimeProvider: TimeProvider {
    override fun elapsedRealtimeMillis(): Long = SystemClock.elapsedRealtime()
}