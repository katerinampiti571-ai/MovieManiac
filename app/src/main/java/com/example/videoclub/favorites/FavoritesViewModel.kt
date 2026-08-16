package com.example.videoclub.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoclub.favorites.usecase.ObserveFavoriteMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val observeFavoriteMoviesUseCase: ObserveFavoriteMoviesUseCase
) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> =
        observeFavoriteMoviesUseCase().map { movies ->

            Log.d(
                "FAVORITES_VM",
                "Movies received: $movies"
            )

            val state: FavoritesUiState =
                FavoritesUiState.Data(
                    movies = movies.map { movie ->
                        FavoriteMovieUi(
                            id = movie.id,
                            title = movie.title,
                            imageUrl = movie.imageUrl,
                            isFavorite = movie.isFavorite
                        )
                    }
                )

            state
        }
            .catch { throwable ->
                emit(
                    FavoritesUiState.Error(
                        message = throwable.message
                            ?: "Could not load favorite movies"
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FavoritesUiState.Loading
            )
}