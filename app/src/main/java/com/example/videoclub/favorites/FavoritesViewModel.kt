package com.example.videoclub.favorites

import android.util.Log
import androidx.compose.ui.text.Paragraph
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoclub.favorites.usecase.ObserveFavoriteMoviesUseCase
import com.example.videoclub.home.HomeUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavoriteMoviesUseCase: ObserveFavoriteMoviesUseCase
) : ViewModel() {

    private val _navigation: MutableSharedFlow<FavoritesNavigationTarget> = MutableSharedFlow()
    val navigation: SharedFlow<FavoritesNavigationTarget> = _navigation.asSharedFlow()

    val uiState: StateFlow<FavoritesUiState> =
        observeFavoriteMoviesUseCase().map { movies ->
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
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FavoritesUiState.Loading
        )

    fun onAction(uiAction: FavoritesUiAction) {
        when (uiAction) {
            is FavoritesUiAction.MovieClicked -> onMovieClicked(uiAction.movieId)
        }
    }

    private fun onMovieClicked(movieId: String) {
        viewModelScope.launch {
            _navigation.emit(FavoritesNavigationTarget.MovieDetails(movieId))
        }
    }
}