package com.example.offlinefirstnews.ui.cachelab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CacheLabScreen(
    selectedSection: CacheLabSection,
    onSectionSelected: (CacheLabSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val sections = CacheLabSection.entries
    val selectedTabIndex = sections.indexOf(selectedSection)

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex
        ) {
            sections.forEach { section ->
                Tab(
                    selected = section == selectedSection,
                    onClick = {
                        onSectionSelected(section)
                    },
                    text = {
                        Text(text = section.title)
                    }
                )
            }
        }

        when(selectedSection) {
            CacheLabSection.RETENTION -> {
                Text(
                    text = "Retention strategies",
                    modifier = Modifier.padding(16.dp)
                )
            }
            CacheLabSection.FRESHNESS -> {
                Text(
                    text = "Freshness strategies",
                    modifier = Modifier.padding(16.dp)
                )
            }
            CacheLabSection.OFFLINE_SYNC -> {
                Text(
                    text = "Offline Sync strategies",
                    modifier = Modifier.padding(16.dp)
                )
            }
            CacheLabSection.OBSERVABILITY -> {
                Text(
                    text = "Observability strategies",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}