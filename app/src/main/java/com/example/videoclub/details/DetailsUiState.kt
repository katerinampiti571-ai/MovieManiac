package com.example.videoclub.details

sealed interface DetailsUiState {
    object Loading : DetailsUiState
    data class Data(
        val title: String,
        val description: String,
        val imageUrl: String,
        val isFavorite: Boolean
    ): DetailsUiState
    data class Error(
        val message: String
    ): DetailsUiState
}