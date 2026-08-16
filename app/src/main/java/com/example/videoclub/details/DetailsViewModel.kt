package com.example.videoclub.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.videoclub.details.usecase.ObserveMovieDetailsUseCase
import com.example.videoclub.details.usecase.SetMovieFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val observeMovieDetailsUseCase: ObserveMovieDetailsUseCase,
    private val setMovieFavouriteUseCase: SetMovieFavoriteUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<DetailsRoute>()

    var movieId = route.movieId
    private val _navigation = MutableSharedFlow<DetailsNavigationTarget>()
    val navigation = _navigation.asSharedFlow()
    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeMovieDetailsUseCase(movieId).collect { movie ->
                if (movie == null) {
                    _uiState.value = DetailsUiState.Error(
                        message = "Could not resolve movie with movieId: $movieId"
                    )
                } else {
                    _uiState.value = DetailsUiState.Data(
                        title = movie.title,
                        description = movie.overview,
                        imageUrl = movie.imageUrl,
                        isFavorite = movie.isFavorite
                    )
                }
            }
        }
    }

    fun onAction(action: DetailsUiAction) {
        when (action) {
            DetailsUiAction.BackClicked -> onBackClicked()
            DetailsUiAction.FavoriteClicked -> onFavoriteClicked()
        }
    }


    private fun onBackClicked() {
        viewModelScope.launch {
            _navigation.emit(DetailsNavigationTarget.Back)
        }
    }

    private fun onFavoriteClicked() {
        val currentState = uiState.value

        if (currentState is DetailsUiState.Data) {
            viewModelScope.launch {
                setMovieFavouriteUseCase(
                    id = movieId,
                    isFavourite = !currentState.isFavorite
                )
            }
        }
    }
}