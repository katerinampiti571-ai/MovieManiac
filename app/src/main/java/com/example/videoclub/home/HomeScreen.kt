package com.example.videoclub.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.videoclub.home.HomeUiState.Data
import com.example.videoclub.ui.theme.IrishGrover

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.video_club),
                        color = Color.White,
                        fontFamily = IrishGrover,
                        fontSize = 30.sp,
                        modifier = Modifier.padding(top = 32.dp)
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
                .fillMaxWidth()
                .padding(innerPadding)
                .background(colorResource(R.color.primary)),

            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
            }
            when (uiState) {
                is Data -> {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ){
                        items(items = uiState.movies) {
                            MoviePhoto(
                                movie = it,
                            )
                        }
                    }
                }
                HomeUiState.Loading -> {
                    // todo
                }
            }
        }

    }
}
@Composable
fun MoviePhoto(
    movie: Data.Movie,
) {
    AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(movie.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .crossfade(true).build(),
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(uiState = HomeUiState.Data(
        movies = listOf(
            Data.Movie(
                id = "1",
                imageUrl = "https://api.themoviedb.org/3/gMJngTNfaqCSCqGD4y8lVMZXKDn.jp",
                title = "afd"
            )
        )
    ))
}