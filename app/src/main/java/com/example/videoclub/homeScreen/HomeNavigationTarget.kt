package com.example.videoclub.homeScreen

sealed interface HomeNavigationTarget {
    data class Details(val movieId: String): HomeNavigationTarget

}