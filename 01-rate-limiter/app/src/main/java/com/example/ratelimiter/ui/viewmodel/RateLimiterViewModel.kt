package com.example.ratelimiter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratelimiter.application.RateLimiterCoordinator
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitConfiguration
import com.example.ratelimiter.ui.state.RateLimiterIntent
import com.example.ratelimiter.ui.state.RateLimiterUiState
import com.example.ratelimiter.ui.state.RequestUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class RateLimiterViewModel @Inject constructor(
    private val coordinator: RateLimiterCoordinator,
    initialConfiguration: RateLimitConfiguration
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(
        RateLimiterUiState(
            configuration = initialConfiguration
        )
    )

    val uiState = _uiState.asStateFlow()

    // Internal state

    // Invalidates results from requests started before a newer operation.
    private var configurationVersion = 0L

    // Identifies the latest Apply Configuration or Reset operation.
    private var configurationOperationVersion = 0L

    // Detects whether the user edited the input fields during an operation.
    private var draftVersion = 0L

    // Serializes requests, configuration changes, and resets.
    private val mutex = Mutex()

    // MVI entry point
    fun onIntent(intent: RateLimiterIntent) {
        when (intent) {
            RateLimiterIntent.Reset -> reset()

            RateLimiterIntent.SimulateRequest -> simulateRequest()

            RateLimiterIntent.ApplyConfiguration -> applyConfiguration()

            is RateLimiterIntent.SetRequestLimit -> {
                setRequestLimit(intent.limitInput)
            }

            is RateLimiterIntent.SetRequestWindow -> {
                setRequestWindow(intent.windowSecondsInput)
            }
        }
    }

    // Input handling
    private fun setRequestLimit(limit: String) {
        draftVersion++

        _uiState.update {
            it.copy(
                limitInput = limit,
                configurationError = null
            )
        }
    }

    private fun setRequestWindow(window: String) {
        draftVersion++

        _uiState.update {
            it.copy(
                windowSecondsInput = window,
                configurationError = null
            )
        }
    }

    // Apply configuration
    private fun applyConfiguration() {
        val currentState = _uiState.value
        val draftVersionAtStart = draftVersion

        // Validate request limit.
        val limit = currentState.limitInput.trim().toIntOrNull()

        if (limit == null || limit <= 0) {
            _uiState.update {
                it.copy(
                    configurationError =
                        "Request limit must be a positive integer."
                )
            }
            return
        }

        // Validate window duration.
        val windowInput = currentState.windowSecondsInput.trim()
        val windowSeconds = windowInput.toLongOrNull()

        if (windowSeconds == null) {
            val isNumericInput =
                windowInput.isNotEmpty() &&
                        windowInput.all { it in '0'..'9' }

            _uiState.update {
                it.copy(
                    configurationError = if (isNumericInput) {
                        "Window duration is too large."
                    } else {
                        "Window must be a positive whole number of seconds."
                    }
                )
            }
            return
        }

        if (windowSeconds <= 0) {
            _uiState.update {
                it.copy(
                    configurationError =
                        "Window must be a positive whole number of seconds."
                )
            }
            return
        }

        if (windowSeconds > Long.MAX_VALUE / 1_000L) {
            _uiState.update {
                it.copy(
                    configurationError = "Window duration is too large."
                )
            }
            return
        }

        val newConfiguration = RateLimitConfiguration(
            limit = limit,
            windowMillis = windowSeconds * 1_000L
        )

        // Register this as the latest configuration operation.
        configurationOperationVersion++
        val operationVersionAtStart = configurationOperationVersion

        // Invalidate request results associated with an older operation.
        configurationVersion++

        _uiState.update {
            it.copy(configurationError = null)
        }

        viewModelScope.launch {
            try {
                mutex.withLock {
                    // Skip an obsolete operation that has not started.
                    if (
                        operationVersionAtStart !=
                        configurationOperationVersion
                    ) {
                        return@withLock
                    }

                    // Avoid replacing the limiter if the configuration
                    // is already active.
                    val configurationChanged =
                        _uiState.value.configuration != newConfiguration

                    if (configurationChanged) {
                        coordinator.updateConfiguration(newConfiguration)
                    }

                    _uiState.update { state ->
                        val isLatestOperation =
                            operationVersionAtStart ==
                                    configurationOperationVersion

                        val draftIsUnchanged =
                            draftVersionAtStart == draftVersion

                        state.copy(
                            // Reflect the configuration actually applied.
                            configuration = newConfiguration,

                            // Preserve inputs edited while Apply was running.
                            limitInput = if (
                                isLatestOperation && draftIsUnchanged
                            ) {
                                limit.toString()
                            } else {
                                state.limitInput
                            },

                            windowSecondsInput = if (
                                isLatestOperation && draftIsUnchanged
                            ) {
                                windowSeconds.toString()
                            } else {
                                state.windowSecondsInput
                            },

                            // Only the latest operation controls request UI.
                            requestState = if (isLatestOperation) {
                                RequestUiState.Idle
                            } else {
                                state.requestState
                            },

                            // Don't clear an error belonging to newer input.
                            configurationError = if (
                                isLatestOperation && draftIsUnchanged
                            ) {
                                null
                            } else {
                                state.configurationError
                            }
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                if (
                    operationVersionAtStart ==
                    configurationOperationVersion
                ) {
                    _uiState.update { state ->
                        val draftIsUnchanged =
                            draftVersionAtStart == draftVersion

                        state.copy(
                            configurationError = if (draftIsUnchanged) {
                                "Unable to apply configuration. Please try again."
                            } else {
                                state.configurationError
                            },
                            requestState = RequestUiState.Idle
                        )
                    }
                }
            }
        }
    }

    // Request handling
    private fun simulateRequest() {
        if (_uiState.value.requestState is RequestUiState.Processing) {
            return
        }

        val versionAtStart = configurationVersion

        _uiState.update {
            it.copy(requestState = RequestUiState.Processing)
        }

        viewModelScope.launch {
            try {
                mutex.withLock {
                    // A newer configuration operation may have invalidated
                    // this request while it was waiting for the mutex.
                    if (versionAtStart != configurationVersion) {
                        return@withLock
                    }

                    val decision = coordinator.allow()

                    // Publish the result only if it is still current.
                    if (versionAtStart == configurationVersion) {
                        _uiState.update {
                            it.copy(
                                requestState =
                                    RequestUiState.Completed(decision)
                            )
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                if (versionAtStart == configurationVersion) {
                    _uiState.update {
                        it.copy(requestState = RequestUiState.Idle)
                    }
                }
            }
        }
    }

    // Reset handling
    private fun reset() {
        // Invalidate older request results and pending Apply operations.
        configurationVersion++
        configurationOperationVersion++

        val operationVersionAtStart = configurationOperationVersion

        viewModelScope.launch {
            try {
                mutex.withLock {
                    // Skip an obsolete reset if a newer operation exists.
                    if (
                        operationVersionAtStart !=
                        configurationOperationVersion
                    ) {
                        return@withLock
                    }

                    coordinator.reset()

                    _uiState.update {
                        it.copy(requestState = RequestUiState.Idle)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Keep the current UI state if reset fails.
                // Add a dedicated reset error message later if needed.
            }
        }
    }
}