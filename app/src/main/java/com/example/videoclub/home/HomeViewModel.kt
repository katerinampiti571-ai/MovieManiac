package com.example.videoclub.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoclub.data.Movie
import com.example.videoclub.home.usecase.GetPopularMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(
        HomeUiState.Loading
    )

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {

            val result = getPopularMoviesUseCase()

            _uiState.update {
                HomeUiState.Data(
                    movies = result.mapToUiState()
                )
            }
        }
    }
}

private fun List<Movie>.mapToUiState(): List<HomeUiState.Data.Movie> {
    return map {
        HomeUiState.Data.Movie(
            id = it.id,
            title = it.title,
            imageUrl = it.imageUrl
        )
    }
}




