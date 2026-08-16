package com.example.videoclub.search

sealed interface SearchUiAction {

    data class MovieClicked(val movieId: String): SearchUiAction

    data class SearchTermChanged(val searchTerm: String): SearchUiAction

    data object ClearClicked: SearchUiAction
}