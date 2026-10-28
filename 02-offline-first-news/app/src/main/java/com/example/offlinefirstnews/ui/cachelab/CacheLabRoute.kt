package com.example.offlinefirstnews.ui.cachelab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun CacheLabRoute(
    modifier: Modifier = Modifier
) {
    var selectedSection by rememberSaveable {
        mutableStateOf(CacheLabSection.RETENTION)
    }

    CacheLabScreen(
        selectedSection = selectedSection,
        onSectionSelected = { section ->
            selectedSection = section
        },
        modifier = modifier
    )
}