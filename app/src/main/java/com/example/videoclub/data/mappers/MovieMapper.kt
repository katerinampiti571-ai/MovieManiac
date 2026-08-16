package com.example.videoclub.data.mappers

import com.example.videoclub.data.domain.model.Movie
import com.example.videoclub.data.local.model.MovieEntity
import com.example.videoclub.data.remote.NetworkEndpoints.IMAGES_BASE_URL
import com.example.videoclub.data.remote.model.MovieDto

fun MovieDto.toMovieEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        overview = overview,
        imageUrl = imagePath.let { imagePath ->
            IMAGES_BASE_URL + imagePath
        }
    )
}

fun MovieEntity.toMovie(
    isFavorite: Boolean,
): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        imageUrl = imageUrl,
        isFavorite = isFavorite
    )
}