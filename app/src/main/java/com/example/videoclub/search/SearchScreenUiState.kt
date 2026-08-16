package com.example.videoclub.search

data class SearchScreenUiState(
    val searchTerm: String,
    val content: Content,
){
    sealed interface Content {

        data object  Loading: Content

        data object Empty: Content

        data class Data(val movies: List<Movie>): Content{
            data class Movie(
                val id: String,
                val imageUrl: String,
                val title: String
            )
        }
        data class Error(
            val message: String
        ) : Content
    }
    companion object {
        val INITIAL = SearchScreenUiState(
            searchTerm = "",
            content =Content.Empty,
        )
    }
}