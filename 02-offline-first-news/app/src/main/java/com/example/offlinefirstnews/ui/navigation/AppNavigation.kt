package com.example.offlinefirstnews.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.offlinefirstnews.ui.news.detail.NewsDetailRoute
import com.example.offlinefirstnews.ui.news.detail.NewsDetailViewModel
import com.example.offlinefirstnews.ui.news.feed.NewsFeedRoute
import com.example.offlinefirstnews.ui.news.feed.NewsFeedViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "news_feed"
    ) {
        composable("news_feed") {
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
        composable(
            route = "news_detail/{newsId}",
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