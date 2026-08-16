package com.example.videoclub.data.domain

import com.example.videoclub.network.model.MovieResponseDto
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    fun observePopularMovies(): Flow<List<Movie>>

    suspend fun clearMovies()

    fun observeMovie(id: String): Flow<Movie?>

    suspend fun setMovieFavourite(
        id: String,
        isFavourite: Boolean
    )
    fun observeFavoriteMovies(): Flow<List<Movie>>

    suspend fun searchMovie(searchTerm: String): List<Movie>

    suspend fun syncPopularMovies(page: Int): Result<MovieResponseDto>
}