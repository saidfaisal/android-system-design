package com.example.ratelimiter.domain.ratelimiter

class FixedWindowAlgorithm {
    fun evaluate(
        state: FixedWindowState,
        configuration: RateLimitConfiguration,
        nowMillis: Long
    ): FixedWindowEvaluation {
        return when (state) {
            is FixedWindowState.NoWindowYet -> {
                FixedWindowEvaluation(
                    decision = RateLimitDecision.Allowed,
                    newState = FixedWindowState.ActiveWindow(
                        count = 1,
                        windowStartMillis = nowMillis
                    )
                )
            }
            is FixedWindowState.ActiveWindow -> {
                if (nowMillis - state.windowStartMillis >= configuration.windowMillis) {
                    FixedWindowEvaluation(
                        decision = RateLimitDecision.Allowed,
                        newState = FixedWindowState.ActiveWindow(
                            count = 1,
                            windowStartMillis = nowMillis
                        )
                    )
                } else if(state.count < configuration.limit) {
                    FixedWindowEvaluation(
                        decision = RateLimitDecision.Allowed,
                        newState = FixedWindowState.ActiveWindow(
                            count = state.count + 1,
                            windowStartMillis = state.windowStartMillis
                        )
                    )
                } else {
                    FixedWindowEvaluation(
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