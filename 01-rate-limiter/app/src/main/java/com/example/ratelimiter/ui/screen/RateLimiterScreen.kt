package com.example.ratelimiter.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitDecision
import com.example.ratelimiter.ui.state.RateLimiterIntent
import com.example.ratelimiter.ui.state.RateLimiterUiState
import com.example.ratelimiter.ui.state.RequestUiState

@Composable
fun RateLimiterScreen(
    uiState: RateLimiterUiState,
    onIntent: (RateLimiterIntent) -> Unit,
    modifier: Modifier = Modifier
)
{
    Column(
        modifier = modifier.padding(
            start = 16.dp,
            end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                label = { Text("Request limit") },
                placeholder = { Text("limit") },
                value = uiState.limitInput,
                onValueChange = { input ->
                    onIntent(
                        RateLimiterIntent.SetRequestLimit(
                            limitInput = input.filter { it in '0'..'9' }
                        )
                    )
                }
            )
            TextField(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                label = { Text("Window (seconds)") },
                value = uiState.windowSecondsInput,
                onValueChange = { input ->
                    onIntent(
                        RateLimiterIntent.SetRequestWindow(
                            windowSecondsInput = input.filter { it in '0'..'9' }
                        )
                    )
                }
            )
        }

        uiState.configurationError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onIntent(
                    RateLimiterIntent.ApplyConfiguration
                )
            }
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Apply Configuration"
            )
        }

        Column {
            Text("Current configuration")
            Text(
                text = "${uiState.configuration.limit} requests / ${uiState.configuration.windowMillis / 1_000} seconds",
            )
            val resultText = when(val requestState = uiState.requestState) {
                RequestUiState.Idle -> "Ready to simulate a request"
                RequestUiState.Processing -> {
                    "Processing request..."
                }
                is RequestUiState.Completed -> when (val decision = requestState.decision) {
                    RateLimitDecision.Allowed -> "Request allowed"
                    is RateLimitDecision.Rejected ->
                        decision.retryAfterMillis?.let { retryAfter ->
                            "Request rejected. Retry after $retryAfter ms"
                        } ?: "Request rejected"

                }
            }

            Text(
                text = resultText
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            val isProcessing = uiState.requestState is RequestUiState.Processing
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                onClick = {
                    onIntent(RateLimiterIntent.SimulateRequest)
                },
                enabled = !isProcessing
            ) {
                Text("Simulate Request")
            }
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                onClick = {
                    onIntent(RateLimiterIntent.Reset)
                }
            ) {
                Text("Reset")
            }
        }
    }
}