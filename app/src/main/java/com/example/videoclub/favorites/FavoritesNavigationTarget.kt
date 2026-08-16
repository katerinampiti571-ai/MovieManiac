package com.example.videoclub.favorites

sealed interface FavoritesNavigationTarget {
    data class MovieDetails(val movieId: String): FavoritesNavigationTarget
}