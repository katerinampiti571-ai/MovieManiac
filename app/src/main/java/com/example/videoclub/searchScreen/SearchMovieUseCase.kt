package com.example.videoclub.searchScreen

import com.example.videoclub.data.domain.Movie
import com.example.videoclub.data.domain.MoviesRepository
import com.example.videoclub.homeScreen.HomeUiState
import javax.inject.Inject

interface SearchMovieUseCase {
    suspend operator fun invoke(searchTerm: String): List<Movie>
}

class SearchMovieUseCaseImpl @Inject constructor(
    private val moviesRepository: MoviesRepository
) : SearchMovieUseCase {

    override suspend fun invoke(searchTerm: String): List<Movie> {
        return moviesRepository.searchMovie(searchTerm)
    }
}