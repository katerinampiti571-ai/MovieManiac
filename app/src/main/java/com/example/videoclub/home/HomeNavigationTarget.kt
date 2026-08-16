package com.example.videoclub.home

sealed interface HomeNavigationTarget {
    data class Details(val movieId: String): HomeNavigationTarget

}