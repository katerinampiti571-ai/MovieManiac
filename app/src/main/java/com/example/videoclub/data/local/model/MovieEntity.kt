package com.example.videoclub.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val overview: String,
    val imageUrl: String,
)

@Entity(tableName = "favorites")
data class FavoritesEntity(
    @PrimaryKey
    val id: String,
    val isFavourite: Boolean
)