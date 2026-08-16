package com.example.videoclub.details

sealed interface DetailsUiAction {
    data object BackClicked: DetailsUiAction

    data object FavoriteClicked: DetailsUiAction
}