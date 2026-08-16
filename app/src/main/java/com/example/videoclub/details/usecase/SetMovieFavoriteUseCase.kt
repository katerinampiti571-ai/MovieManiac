package com.example.videoclub.details.usecase

import com.example.videoclub.data.domain.MoviesRepository
import jakarta.inject.Inject

interface SetMovieFavoriteUseCase {
    suspend operator fun invoke(
        id: String,
        isFavourite: Boolean
    )
}

class SetMovieFavouriteUseCaseImpl @Inject constructor(
    private val moviesRepository: MoviesRepository
) : SetMovieFavoriteUseCase {

    override suspend fun invoke(
        id: String,
        isFavourite: Boolean
    ) {
        moviesRepository.setMovieFavourite(
            id = id,
            isFavourite = isFavourite
        )
    }
}