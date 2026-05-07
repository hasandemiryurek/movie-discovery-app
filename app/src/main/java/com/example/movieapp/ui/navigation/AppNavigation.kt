package com.example.movieapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.movieapp.R
import com.example.movieapp.ui.detail.DetailScreen
import com.example.movieapp.ui.home.HomeScreen
import com.example.movieapp.ui.movielist.MovieListScreen
import com.example.movieapp.ui.search.SearchScreen
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Detail : Screen("detail/{movieId}") {
        fun createRoute(movieId: Int) = "detail/$movieId"
    }
    data object MovieList : Screen("movie_list/{category}") {
        fun createRoute(category: String) = "movie_list/$category"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val bottomNavItems = listOf(Screen.Home, Screen.Search)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = bottomNavItems.any {
                currentDestination?.hierarchy?.any { dest -> dest.route == it.route } == true
            }

            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy
                            ?.any { it.route == screen.route } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = when (screen) {
                                        Screen.Home -> Icons.Filled.Home
                                        else -> Icons.Filled.Search
                                    },
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = when (screen) {
                                        Screen.Home -> stringResource(R.string.nav_home)
                                        else -> stringResource(R.string.nav_search)
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(animationSpec = tween(300)) { it } + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(300)) { -it } + fadeOut(tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(animationSpec = tween(300)) { -it } + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(animationSpec = tween(300)) { it } + fadeOut(tween(300))
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onMovieClick = { navController.navigate(Screen.Detail.createRoute(it)) },
                    onShowAll = { category -> navController.navigate(Screen.MovieList.createRoute(category)) }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onMovieClick = { navController.navigate(Screen.Detail.createRoute(it)) }
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) {
                DetailScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Screen.MovieList.route,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) {
                MovieListScreen(
                    onBack = { navController.popBackStack() },
                    onMovieClick = { navController.navigate(Screen.Detail.createRoute(it)) }
                )
            }
        }
    }
}