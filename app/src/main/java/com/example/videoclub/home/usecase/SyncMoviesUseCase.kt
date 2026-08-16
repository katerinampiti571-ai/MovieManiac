package com.example.videoclub.home.usecase

import com.example.videoclub.data.domain.MoviesRepository
import com.example.videoclub.data.domain.model.PopularMoviesMetadata
import com.example.videoclub.data.remote.model.MovieDto
import com.example.videoclub.data.remote.model.MovieResponseDto
import com.google.gson.annotations.SerializedName
import javax.inject.Inject

interface SyncPopularMoviesUseCase {
    suspend operator fun invoke(page: Int): Result<PopularMoviesMetadata>
}

class SyncPopularMoviesUseCaseImpl @Inject constructor(
    private val movieRepository: MoviesRepository,
): SyncPopularMoviesUseCase {
    override suspend fun invoke(page: Int): Result<PopularMoviesMetadata> {
        return movieRepository.syncPopularMovies(page)
    }
}