package com.example.ratelimiter.domain.ratelimiter

sealed interface FixedWindowState {
    data object NoWindowYet: FixedWindowState
    data class ActiveWindow(
        val count: Int,
        val windowStartMillis: Long
    ): FixedWindowState
}