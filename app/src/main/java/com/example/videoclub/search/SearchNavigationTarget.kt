package com.example.videoclub.search

 sealed interface SearchNavigationTarget {
    data class Details(val movieId: String): SearchNavigationTarget
}