package com.example.ratelimiter.domain.ratelimiter.algorithm.fixedwindow

import com.example.ratelimiter.domain.ratelimiter.algorithm.RateLimitAlgorithm
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitConfiguration
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitDecision
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitEvaluation

class FixedWindowAlgorithm: RateLimitAlgorithm<FixedWindowState, RateLimitConfiguration> {
    override fun initState(configuration: RateLimitConfiguration): FixedWindowState = FixedWindowState.NoWindowYet
    override fun evaluate(
        state: FixedWindowState,
        configuration: RateLimitConfiguration,
        nowMillis: Long
    ): RateLimitEvaluation<FixedWindowState> {
        return when (state) {
            is FixedWindowState.NoWindowYet -> {
                RateLimitEvaluation(
                    decision = RateLimitDecision.Allowed,
                    newState = FixedWindowState.ActiveWindow(
                        count = 1,
                        windowStartMillis = nowMillis
                    )
                )
            }
            is FixedWindowState.ActiveWindow -> {
                if (nowMillis - state.windowStartMillis >= configuration.windowMillis) {
                    RateLimitEvaluation(
                        decision = RateLimitDecision.Allowed,
                        newState = FixedWindowState.ActiveWindow(
                            count = 1,
                            windowStartMillis = nowMillis
                        )
                    )
                } else if(state.count < configuration.limit) {
                    RateLimitEvaluation(
                        decision = RateLimitDecision.Allowed,
                        newState = FixedWindowState.ActiveWindow(
                            count = state.count + 1,
                            windowStartMillis = state.windowStartMillis
                        )
                    )
                } else {
                    RateLimitEvaluation(
                        decision = RateLimitDecision.Rejected(
                            retryAfterMillis = state.windowStartMillis + configuration.windowMillis - nowMillis
                        ),
                        newState = state
                    )
                }
            }
        }
    }
}