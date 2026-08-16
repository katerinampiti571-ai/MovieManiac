package com.example.videoclub.data.domain

data class Movie(
    val id: String,
    val imageUrl: String,
    val title: String,
    val overview: String,
    val isFavorite: Boolean
)
