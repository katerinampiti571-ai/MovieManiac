package com.example.videoclub.homeScreen.usecase

import com.example.videoclub.data.domain.MoviesRepository
import com.example.videoclub.network.model.MovieResponseDto
import javax.inject.Inject

interface SyncPopularMoviesUseCase {
    suspend operator fun invoke(page: Int): Result<MovieResponseDto>
}

class SyncPopularMoviesUseCaseImpl @Inject constructor(
    private val movieRepository: MoviesRepository,
): SyncPopularMoviesUseCase {
    override suspend fun invoke(page: Int): Result<MovieResponseDto> {
        return movieRepository.syncPopularMovies(page)
    }
}