package com.example.videoclub.search

import com.example.videoclub.data.domain.model.Movie
import com.example.videoclub.data.domain.MoviesRepository
import okio.IOException
import javax.inject.Inject

interface SearchMovieUseCase {
    suspend operator fun invoke(searchTerm: String): Result<List<Movie>>
}

class SearchMovieUseCaseImpl @Inject constructor(
    private val moviesRepository: MoviesRepository
) : SearchMovieUseCase {

    override suspend fun invoke(searchTerm: String): Result<List<Movie>> {
        return moviesRepository.searchMovie(searchTerm).fold(
            onSuccess = {
                return@fold Result.success(it)
            },
            onFailure = {
                return when {
                    it is IOException -> {
                        Result.failure(Throwable("No internet connection"))
                    }
                    else -> {
                        Result.failure(Throwable("Something went wrong"))
                    }
                }
            }
        )
    }
}