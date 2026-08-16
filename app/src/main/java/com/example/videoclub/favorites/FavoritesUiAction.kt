package com.example.videoclub.favorites

sealed interface FavoritesUiAction {
    data class MovieClicked(val movieId: String): FavoritesUiAction
}