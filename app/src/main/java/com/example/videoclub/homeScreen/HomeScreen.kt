package com.example.videoclub.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.videoclub.R
import com.example.videoclub.homeScreen.HomeUiState.Data
import com.example.videoclub.ui.theme.IrishGrover
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    snackBarHostState: SnackbarHostState,
    onNavigate: (HomeNavigationTarget) -> Unit
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onLoadNextPage = { viewModel.onAction(HomeUiAction.LoadNextPage) },
        onAction = { viewModel.onAction(it) },
        )
    LaunchedEffect(
        key1 = Unit
    ) {
        viewModel.navigation.collect {
            onNavigate(it)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.events.collect {
            snackBarHostState.showSnackbar(
                message = it
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onLoadNextPage: () -> Unit,
    onAction: (HomeUiAction) -> Unit,
    ) {

    val gridState = rememberLazyGridState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colorResource(id = R.color.primary),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.video_club),
                        color = Color.White,
                        fontFamily = IrishGrover,
                        fontSize = 30.sp,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.primary),
                ),
            )
        },
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.primary)),

            ) {

            Text(
                text = "Popular",
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier
                    .padding(24.dp)
            )
            LaunchedEffect(gridState) {
                snapshotFlow {
                    val lastVisible =
                        gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

                    val total =
                        gridState.layoutInfo.totalItemsCount

                    total > 0 && lastVisible >= total - 2

                }.collect { shouldLoad ->

                    if (shouldLoad) {
                        onLoadNextPage()
                    }
                }
            }
            when (uiState) {
                is Data -> {
                    LazyVerticalGrid(
                        state = gridState,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorResource(id = R.color.primary)),
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {

                        items(uiState.movies) { movie ->
                            Column {
                                MoviePhoto(
                                    movie = movie,
                                    onAction = onAction,
                                )
                                Text(
                                    text = movie.title,
                                    color = Color.White
                                )
                            }
                        }

                        item(span = { GridItemSpan(3) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()

                            }

                        }
                    }
                }

                HomeUiState.Loading -> {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                is HomeUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.message,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onAction(HomeUiAction.RetryClicked)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Magenta
                            )
                        ) {
                            Text(
                                text = "Retry",
                                color = Color.White
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun MoviePhoto(
    movie: Data.Movie,
    onAction: (HomeUiAction) -> Unit
) {
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onAction(HomeUiAction.MovieClicked(movieId = movie.id)) },
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(movie.imageUrl)
            .placeholder(R.drawable.movie_cover)
            .crossfade(true).build(),
        contentDescription = "",
        contentScale = ContentScale.Crop,
    )

}


@Preview(showBackground = true)
@Composable
fun HomeScreenDataPreview() {
    HomeScreen(
        uiState = Data(
            movies = listOf(
                Data.Movie(
                    id = "1",
                    imageUrl = "",
                    title = "afd"
                )
            ),
            isLoadingMore = false
        ),
        onLoadNextPage = {},
        onAction = {},
    )
}
