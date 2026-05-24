package com.example.videoclub.home

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Data (
        val movies: List<Movie>
    ): HomeUiState {
        data class Movie(
            val id: String,
            val imageUrl: String,
            val title: String
        )
    }

}