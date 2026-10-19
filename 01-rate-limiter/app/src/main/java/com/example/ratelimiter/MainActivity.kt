package com.example.ratelimiter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.ratelimiter.domain.ratelimiter.model.RateLimitConfiguration
import com.example.ratelimiter.platform.time.AndroidTimeProvider
import com.example.ratelimiter.ui.screen.RateLimiterRoute
import com.example.ratelimiter.ui.viewmodel.RateLimiterViewModel
import com.example.ratelimiter.ui.viewmodel.RateLimiterViewModelFactory
import com.example.ratelimiter.ui.theme.RateLimiterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val configuration = RateLimitConfiguration(
            limit = 3,
            windowMillis = 10_000
        )

        val factory = RateLimiterViewModelFactory(
            initialConfiguration = configuration,
            timeProvider = AndroidTimeProvider()
        )

        val viewModel = ViewModelProvider(this, factory).get(RateLimiterViewModel::class.java)

        enableEdgeToEdge()
        setContent {
            RateLimiterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RateLimiterRoute(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

