package com.example.videoclub.ui.theme

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.videoclub.R
import com.example.videoclub.details.DetailsNavigationTarget
import com.example.videoclub.details.DetailsRoute
import com.example.videoclub.details.DetailsScreen
import com.example.videoclub.favorites.FavoritesNavigationTarget
import com.example.videoclub.favorites.FavoritesScreen
import com.example.videoclub.favorites.FavoritesRoute
import com.example.videoclub.home.HomeRoute
import com.example.videoclub.home.HomeScreen
import com.example.videoclub.home.HomeNavigationTarget
import com.example.videoclub.search.SearchNavigationTarget
import com.example.videoclub.search.SearchRoute
import com.example.videoclub.search.SearchScreen

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun VideoClubApp() {
    val navController: NavHostController = rememberNavController()

    val snackBarHostState = remember { SnackbarHostState() }

    val currentBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        currentBackStackEntry?.destination?.route

    val isHomeScreen =
        currentRoute?.contains("HomeRoute") == true

    val showBottomBar =
        currentRoute != null &&
                !currentRoute.contains("DetailsRoute")

    BackHandler(
        enabled = currentRoute != null && !isHomeScreen
    ) {
        navController.navigate(HomeRoute) {
            popUpTo(HomeRoute) {
                inclusive = false
            }

            launchSingleTop = true
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.primary)),
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
            )
        },
        bottomBar = {
            if (showBottomBar) {
                VideoClubBottomBar(
                    modifier = Modifier
                        .fillMaxWidth(),

                    currentRoute = currentRoute,

                    onHomeClick = {
                        navController.navigate(HomeRoute) {
                            popUpTo(HomeRoute) {
                                inclusive = false
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },

                    onSearchClick = {
                        navController.navigate(SearchRoute) {
                            popUpTo(HomeRoute) {
                                inclusive = false
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },

                    onFavoritesClick = {
                        navController.navigate(FavoritesRoute) {
                            popUpTo(HomeRoute) {
                                inclusive = false
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(it).fillMaxSize()
        ) {
            composable<HomeRoute> {
                HomeScreen(
                    snackBarHostState = snackBarHostState,
                    onNavigate = { target ->
                        when (target) {
                            is HomeNavigationTarget.Details -> {
                                navController.navigate(
                                    DetailsRoute(
                                        movieId = target.movieId
                                    )
                                )
                            }
                        }
                    }
                )
            }
            composable<SearchRoute> {
                SearchScreen(
                    onNavigate = { target ->
                        when (target) {
                            is SearchNavigationTarget.Details -> {
                                navController.navigate(
                                    DetailsRoute(
                                        movieId = target.movieId
                                    )
                                )
                            }
                        }
                    }
                )
            }
            composable<FavoritesRoute> {
                FavoritesScreen(
                    onNavigate = { target ->
                        when (target) {
                            is FavoritesNavigationTarget.MovieDetails -> {
                                navController.navigate(
                                    DetailsRoute(
                                        movieId = target.movieId
                                    )
                                )
                            }
                        }
                    }
                )
            }
            composable<DetailsRoute> {
                DetailsScreen(
                    onNavigate = { target ->
                        when (target) {
                            DetailsNavigationTarget.Back -> {
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun VideoClubBottomBar(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    val isHomeSelected =
        currentRoute?.contains("HomeRoute") == true

    val isSearchSelected =
        currentRoute?.contains("SearchRoute") == true

    val isFavoritesSelected =
        currentRoute?.contains("FavoritesRoute") == true

    NavigationBar(
        modifier = modifier,
        containerColor = colorResource(id = R.color.primary)
    ) {
        NavigationBarItem(
            selected = isHomeSelected,
            onClick = onHomeClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text("Home")
            }
        )

        NavigationBarItem(
            selected = isSearchSelected,
            onClick = onSearchClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            label = {
                Text("Search")
            }
        )

        NavigationBarItem(
            selected = isFavoritesSelected,
            onClick = onFavoritesClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorites"
                )
            },
            label = {
                Text("Favorites")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VideoClubBottomBarPreview(
) {
    VideoClubBottomBar(
        onHomeClick = {},
        onSearchClick = {},
        onFavoritesClick = {},
        currentRoute = "",
        modifier = {} as Modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun VideoClubAppPreview() {
    VideoClubApp()
}