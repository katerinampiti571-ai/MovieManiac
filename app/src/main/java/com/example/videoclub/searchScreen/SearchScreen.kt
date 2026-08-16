package com.example.videoclub.searchScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.videoclub.R
import kotlinx.serialization.Serializable
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.concurrent.timer
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Serializable
data object SearchRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onMovieClick: (String) -> Unit
) {
    val viewModel: SearchScreenViewModel = hiltViewModel()
    val uiState: SearchScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()
    SearchScreen(
        uiState = uiState,
        onSearchTermChanged = { it: String ->
            viewModel.onSearchTermChanged(it)
        },
        onClearClicked = {viewModel.onClearClicked()},
        onMovieClick = onMovieClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: SearchScreenUiState,
    onSearchTermChanged: (String) -> Unit,
    onClearClicked: () -> Unit,
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
                        text = stringResource(R.string.Search),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,

                        )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.primary)
                ),
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(colorResource(R.color.primary)),
            ) {
                SearchBar(
                    value = uiState.searchTerm,
                    onValueChange = onSearchTermChanged,
                    shouldRequestFocus = true,
                    onClearClicked = onClearClicked
                )
                when (val content = uiState.content) {

                    SearchScreenUiState.Content.Empty -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier.imePadding(),
                                text = "Search for a movie",
                                color = Color.White
                            )
                        }
                    }

                    SearchScreenUiState.Content.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.White
                            )
                        }
                    }

                    is SearchScreenUiState.Content.Data -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 24.dp,
                                top = 16.dp,
                                end = 24.dp,
                                bottom = 100.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(
                                items = content.movies,
                                key = { movie -> movie.id }
                            ) { movie ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .width(110.dp)
                                            .height(160.dp)
                                            .clickable {onMovieClick(movie.id)},
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(movie.imageUrl)
                                            .placeholder(R.drawable.movie_cover)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = movie.title,
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(
                                        modifier = Modifier.width(16.dp)
                                    )

                                    Text(
                                        text = movie.title,
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    is SearchScreenUiState.Content.Error ->{
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                modifier = Modifier.imePadding(),
                                text = uiState.content.message,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    )
}
@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    shouldRequestFocus: Boolean = false,
    onClearClicked: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(shouldRequestFocus) {
        if (shouldRequestFocus) {
            focusRequester.requestFocus()
        }
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.Search),
                fontSize = 16.sp
            )
        },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (isFocused && value.isNotEmpty()) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource
                        ) {
                            onValueChange("")
                            onClearClicked()
                        },
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear"
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,

            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,

            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray,

            focusedLeadingIconColor = Color(0xFF6F8FC7),
            unfocusedLeadingIconColor = Color(0xFF6F8FC7),

            focusedTrailingIconColor = Color(0xFF6F8FC7),
            unfocusedTrailingIconColor = Color(0xFF6F8FC7),

            focusedBorderColor = Color(0xFF6F8FC7),
            unfocusedBorderColor = Color(0xFF6F8FC7),

            cursorColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        )
    )
}


@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen(
        uiState = SearchScreenUiState(
            searchTerm = "",
            content = SearchScreenUiState.Content.Data(
                movies = listOf(
                    SearchScreenUiState.Content.Data.Movie(
                        id = "",
                        imageUrl = "",
                        title = ""

                    )
                )
            )

        ),
        onSearchTermChanged = {},
        onClearClicked = {},
        onMovieClick = {}

        )
}