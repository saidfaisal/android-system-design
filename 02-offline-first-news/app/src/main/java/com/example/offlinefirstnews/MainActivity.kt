package com.example.offlinefirstnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.offlinefirstnews.ui.navigation.AppNavigation
import com.example.offlinefirstnews.ui.theme.OfflineFirstNewsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OfflineFirstNewsTheme {
                AppNavigation()
            }
        }
    }
}