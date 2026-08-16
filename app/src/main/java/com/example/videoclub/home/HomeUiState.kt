package com.example.videoclub.home

sealed interface HomeUiState {

    data object Loading : HomeUiState

    data class Error(
        val message: String
    ) : HomeUiState

    data class Data(
        val movies: List<Movie>,
        val isLoadingNextPage: Boolean,
    ) : HomeUiState {

        data class Movie(
            val id: String,
            val imageUrl: String,
            val title: String
        )
    }
}