package com.example.ratelimiter.platform.time

import android.os.SystemClock
import com.example.ratelimiter.domain.time.TimeProvider
import javax.inject.Inject

class AndroidTimeProvider @Inject constructor(): TimeProvider {
    override fun elapsedRealtimeMillis(): Long = SystemClock.elapsedRealtime()
}