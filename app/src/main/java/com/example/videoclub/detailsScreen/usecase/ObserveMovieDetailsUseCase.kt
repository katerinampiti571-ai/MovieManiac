package com.example.videoclub.detailsScreen.usecase

import com.example.videoclub.data.domain.Movie
import com.example.videoclub.data.domain.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveMovieDetailsUseCase {
    operator fun invoke(id: String): Flow<Movie?>
}

class ObserveMovieDetailsUseCaseImpl @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ObserveMovieDetailsUseCase{
    override fun invoke(id: String): Flow<Movie?>{
        return moviesRepository.observeMovie(id)
    }
}