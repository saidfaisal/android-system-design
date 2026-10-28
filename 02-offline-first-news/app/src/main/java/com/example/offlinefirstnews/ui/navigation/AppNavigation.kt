package com.example.offlinefirstnews.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.offlinefirstnews.ui.cachelab.CacheLabRoute
import com.example.offlinefirstnews.ui.news.detail.NewsDetailRoute
import com.example.offlinefirstnews.ui.news.detail.NewsDetailViewModel
import com.example.offlinefirstnews.ui.news.feed.NewsFeedRoute
import com.example.offlinefirstnews.ui.news.feed.NewsFeedViewModel

private object AppRoutes {
    const val NEWS_FEED = "news_feed"
    const val CACHE_LAB = "cache_lab"
    const val NEWS_DETAIL = "news_detail/{newsId}"
}

private data class TopLevelDestination(
    val route: String,
    val label: String
)

private val topLevelDestinations = listOf(
    TopLevelDestination(
        route = AppRoutes.NEWS_FEED,
        label = "News Feed"
    ),
    TopLevelDestination(
        route = AppRoutes.CACHE_LAB,
        label = "Cache Lab"
    )
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = topLevelDestinations.any {
        it.route == currentRoute
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(
                                        navController.graph.findStartDestination().id
                                    ) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {},
                            label = {
                                Text(destination.label)
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.NEWS_FEED,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppRoutes.NEWS_FEED) {
                val viewModel: NewsFeedViewModel = hiltViewModel()

                NewsFeedRoute(
                    viewModel = viewModel,
                    onArticleClick = { newsId ->
                        navController.navigate(
                            "news_detail/${Uri.encode(newsId)}"
                        )
                    }
                )
            }
            composable(AppRoutes.CACHE_LAB) {
                CacheLabRoute()
            }
            composable(
                route = AppRoutes.NEWS_DETAIL,
                arguments = listOf(
                    navArgument("newsId") {
                        type = NavType.StringType
                    }
                )
            ) {
                val viewModel: NewsDetailViewModel = hiltViewModel()

                NewsDetailRoute(
                    viewModel = viewModel
                )
            }
        }
    }
}