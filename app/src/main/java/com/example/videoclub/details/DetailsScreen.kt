package com.example.videoclub.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.serialization.Serializable

@Serializable
data class DetailsRoute(
    val movieId: String,
)

@Composable
fun DetailsScreen(
    onNavigate: (DetailsNavigationTarget) -> Unit
) {
    val viewModel: DetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailsScreen(
        detailsUiState = uiState,
        onAction = viewModel::onAction,
    )

    LaunchedEffect(
        key1 = Unit
    ) {
        viewModel.navigation.collect {
            onNavigate(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    detailsUiState: DetailsUiState,
    onAction: (DetailsUiAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    )
    { innerPadding ->
        when (detailsUiState) {
            DetailsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(colorResource(R.color.primary)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DetailsUiState.Data -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            ,
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(detailsUiState.imageUrl)
                            .crossfade(true)
                            .placeholder(R.drawable.movie_cover)
                            .build(),
                        contentDescription = detailsUiState.title,
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { onAction(DetailsUiAction.BackClicked) },
                        modifier = Modifier
                            .padding(22.dp)
                            .statusBarsPadding()
                            .align(Alignment.TopStart)
                            .background(
                                color = Color.Black.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { onAction(DetailsUiAction.FavoriteClicked) },
                        modifier = Modifier
                            .padding(12.dp)
                            .statusBarsPadding()
                            .padding(end = 16.dp, top = 8.dp)
                            .align(Alignment.TopEnd)
                            .background(
                                color = Color.Black.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                    ) {
                        if (detailsUiState.isFavorite) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Remove from favorites",
                                tint = Color.Red
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Favorite,
                                contentDescription = "Add to favorites",
                                tint = Color.Gray
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .padding(all = 20.dp)
                            .padding(bottom = 48.dp)
                            .align(Alignment.BottomCenter),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEEBCE4)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = detailsUiState.title,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = stringResource(R.string.details_description),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = detailsUiState.description,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
            is DetailsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(colorResource(R.color.primary)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = detailsUiState.message,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
    DetailsScreen(
        detailsUiState = DetailsUiState.Data(
            title = "Movie",
            description = "Test",
            imageUrl = "https://example.com/image.jpg",
            isFavorite = false
        ),
        onAction = {},
    )
}