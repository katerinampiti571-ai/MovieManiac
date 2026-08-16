package com.example.videoclub.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.videoclub.R
import com.example.videoclub.favorites.FavoritesUiState.Data
import com.example.videoclub.homeScreen.HomeUiAction
import kotlinx.serialization.Serializable

@Serializable
data object FavoritesRoute

@Composable
fun FavoritesScreen(
    onMovieClick: (String) -> Unit
) {
    val viewModel: FavoritesViewModel = hiltViewModel()
    val uiState: FavoritesUiState by viewModel.uiState.collectAsStateWithLifecycle()

    FavoritesScreen(
        favoritesUiState = uiState,
        onMovieClick = onMovieClick
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favoritesUiState: FavoritesUiState,
    onMovieClick: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colorResource(id = R.color.primary),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.Favorites),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier

                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.primary)
                ),
            )
        }
    ) { innerPadding ->
        when (favoritesUiState) {
            FavoritesUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(colorResource(R.color.primary))
                        .navigationBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is Data -> {
                if (favoritesUiState.movies.isEmpty()) {
                    EmptyFavoritesContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(
                            start = 32.dp,
                            top = 32.dp,
                            end = 32.dp,
                            bottom = 100.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        items(
                            items = favoritesUiState.movies,
                            key = { movie -> movie.id }
                        ) { movie ->
                            MoviePhotoFavorites(
                                movie = movie,
                                onMovieClick = onMovieClick,

                            )
                        }
                    }
                }
            }
        is FavoritesUiState.Error -> {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.primary)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = favoritesUiState.message,
                color = Color.White
            )
        }
    }
    }
}
}

@Composable
private fun EmptyFavoritesContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Δεν υπάρχουν αγαπημένες ταινίες",
            color = Color.White,
            fontSize = 18.sp
        )
    }
}

@Composable
fun MoviePhotoFavorites(
    movie: FavoriteMovieUi,
    onMovieClick: (String) -> Unit
) {
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clickable {onMovieClick(movie.id)},
        model = ImageRequest.Builder(LocalContext.current)
            .data(movie.imageUrl)
            .placeholder(R.drawable.movie_cover)
            .crossfade(true)
            .build(),
        contentDescription = movie.title,
        contentScale = ContentScale.Crop
    )
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    FavoritesScreen(
        favoritesUiState = Data(
            movies = listOf(
                FavoriteMovieUi(
                    id = "1",
                    title = "Movie One",
                    imageUrl = "",
                    isFavorite = true
                ),
                FavoriteMovieUi(
                    id = "2",
                    title = "Movie Two",
                    imageUrl = "",
                    isFavorite = true
                ),
                FavoriteMovieUi(
                    id = "3",
                    title = "Movie Three",
                    imageUrl = "",
                    isFavorite = true
                ),
                FavoriteMovieUi(
                    id = "4",
                    title = "Movie Four",
                    imageUrl = "",
                    isFavorite = true
                ),
                FavoriteMovieUi(
                    id = "5",
                    title = "Movie Five",
                    imageUrl = "",
                    isFavorite = true
                ),
                FavoriteMovieUi(
                    id = "6",
                    title = "Movie Six",
                    imageUrl = "",
                    isFavorite = true
                )
            )
        ),
        onMovieClick = {}
    )
}
