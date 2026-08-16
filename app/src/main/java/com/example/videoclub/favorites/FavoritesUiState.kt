package com.example.videoclub.favorites

data class FavoriteMovieUi(
    val id: String,
    val imageUrl: String,
    val title: String,
    val isFavorite: Boolean
)
sealed interface FavoritesUiState {

    data object Loading : FavoritesUiState

    data class Data(
        val movies: List<FavoriteMovieUi>
    ) : FavoritesUiState
}