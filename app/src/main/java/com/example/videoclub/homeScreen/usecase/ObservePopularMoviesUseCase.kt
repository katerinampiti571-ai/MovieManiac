package com.example.videoclub.homeScreen.usecase

import com.example.videoclub.data.domain.Movie
import com.example.videoclub.data.domain.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObservePopularMoviesUseCase {
    operator fun invoke(): Flow<List<Movie>>
}

class ObservePopularMoviesUseCaseImpl @Inject constructor(
    private val moviesRepository: MoviesRepository
): ObservePopularMoviesUseCase{
    override fun invoke(): Flow<List<Movie>> {
     return moviesRepository.observePopularMovies()
    }
}