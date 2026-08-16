package com.example.videoclub.homeScreen.usecase

import com.example.videoclub.data.domain.MoviesRepository
import jakarta.inject.Inject

interface ClearMoviesUseCase {
    suspend operator fun invoke()
}

class ClearMoviesUseCaseImpl @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ClearMoviesUseCase {
    override suspend fun invoke() {
        moviesRepository.clearMovies()

    }
}