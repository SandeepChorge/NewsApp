package com.newsapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.newsapp.presentation.bookmarks.BookmarkScreen
import com.newsapp.presentation.detail.DetailScreen
import com.newsapp.presentation.feed.FeedScreen
import com.newsapp.presentation.search.SearchScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


sealed class Screen(val route: String) {
    data object Feed : Screen("feed")
    data object Search : Screen("search")
    data object Bookmarks : Screen("bookmarks")
    data object Detail : Screen("detail/{articleUrl}") {
        fun createRoute(articleUrl: String): String {
            val encodedUrl = URLEncoder.encode(articleUrl, StandardCharsets.UTF_8.toString())
            return "detail/$encodedUrl"
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    data object Feed : BottomNavItem("feed", "Feed", Icons.Default.Home)
    data object Search : BottomNavItem("search", "Search", Icons.Default.Search)
    data object Bookmarks : BottomNavItem("bookmarks", "Bookmarks", Icons.Default.Bookmark)
}


@Composable
fun NewsApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Bottom navigation items
    val bottomNavItems = listOf(
        BottomNavItem.Feed,
        BottomNavItem.Search,
        BottomNavItem.Bookmarks
    )

    // Determine if bottom bar should be visible
    val shouldShowBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(text = item.title) },
                            selected = currentDestination?.hierarchy?.any {
                                it.route == item.route
                            } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationRoute!!) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    )
    { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Feed.route,
            modifier = Modifier
                .padding(paddingValues)
        ) {

            composable(Screen.Feed.route) {
                FeedScreen( onArticleClick = {articleUrl ->
                    navController.navigate(Screen.Detail.createRoute(articleUrl))
                })
            }

            // Search Screen
            composable(Screen.Search.route) {
                SearchScreen(
                    onArticleClick = { articleUrl ->
                        navController.navigate(Screen.Detail.createRoute(articleUrl))
                    },
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            // Bookmarks Screen
            composable(Screen.Bookmarks.route) {
                BookmarkScreen(
                    onArticleClick = { articleUrl ->
                        navController.navigate(Screen.Detail.createRoute(articleUrl))
                    }
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("articleUrl") {
                        type = NavType.StringType
                    }
                ),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "newsapp://article/{articleUrl}"
                    }
                )
            ) { backStackEntry ->
                val encodedUrl = backStackEntry.arguments?.getString("articleUrl")
                val articleUrl = encodedUrl?.let {
                    URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                } ?: ""

                DetailScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}
