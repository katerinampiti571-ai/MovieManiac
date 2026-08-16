package com.example.videoclub.favorites.usecase

import com.example.videoclub.data.domain.model.Movie
import com.example.videoclub.data.domain.MoviesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

interface ObserveFavoriteMoviesUseCase {

    operator fun invoke(): Flow<List<Movie>>
}

class ObserveFavoriteMoviesUseCaseImpl @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ObserveFavoriteMoviesUseCase {

    override fun invoke(): Flow<List<Movie>> {
        return moviesRepository.observeFavoriteMovies()
    }
}