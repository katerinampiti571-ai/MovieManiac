package com.example.videoclub.homeScreen

sealed interface HomeUiAction {
    data class MovieClicked(val movieId: String) : HomeUiAction
    data object LoadNextPage : HomeUiAction
    data object RetryClicked : HomeUiAction
}