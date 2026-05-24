package com.example.videoclub.home.usecase

import com.example.videoclub.data.Movie
import com.example.videoclub.data.PopularMoviesRepository
import javax.inject.Inject

interface GetPopularMoviesUseCase {
    suspend operator fun invoke(): List<Movie>
}

class GetPopularMoviesUseCaseImpl @Inject constructor(
    private val moviesRepository: PopularMoviesRepository
): GetPopularMoviesUseCase{
    override suspend fun invoke(): List<Movie> {
     return moviesRepository.getPopularMovies()
    }
}