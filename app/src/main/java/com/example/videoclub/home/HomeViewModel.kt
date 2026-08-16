package com.example.videoclub.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoclub.data.domain.model.Movie
import com.example.videoclub.home.usecase.ClearMoviesUseCase
import com.example.videoclub.home.usecase.ObservePopularMoviesUseCase
import com.example.videoclub.home.usecase.SyncPopularMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel
class HomeViewModel @Inject constructor(
    observePopularMoviesUseCase: ObservePopularMoviesUseCase,
    val syncPopularMoviesUseCase: SyncPopularMoviesUseCase,
    val clearMoviesUseCase: ClearMoviesUseCase
) : ViewModel() {
    private val _navigation = MutableSharedFlow<HomeNavigationTarget>()
    val navigation = _navigation.asSharedFlow()

    private val _nextPageErrorMessage = MutableSharedFlow<String>()
    val nextPageErrorMessage = _nextPageErrorMessage.asSharedFlow()

    private val _noMoviesErrorMessage = MutableStateFlow<String?>(null)

    private var currentPage = 1
    private var hasMorePages = true
    private var totalPages = Int.MAX_VALUE
    private val _isLoadingNextPage = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            clearMoviesUseCase()
            loadPage(page = 1)
        }
    }

    // DB + UiState aka: παίρνω δεδομένα από Room + loading state
    // και τα μετατρέπω σε UI state
    val uiState: StateFlow<HomeUiState> =
        combine(
            observePopularMoviesUseCase(),
            _isLoadingNextPage,
            _noMoviesErrorMessage
        ) { movies, isLoadingNextPage, noMoviesErrorMessage ->
            when {
                movies.isNotEmpty() -> {
                    HomeUiState.Data(
                        movies = movies.mapToUiState(),
                        isLoadingNextPage = isLoadingNextPage,
                    )
                }
                noMoviesErrorMessage != null -> {
                    HomeUiState.Error(message = noMoviesErrorMessage)
                }
                else -> {
                    HomeUiState.Loading
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(),
            initialValue = HomeUiState.Loading
        )

    fun onAction(uiAction: HomeUiAction) {
        when (uiAction) {
            is HomeUiAction.MovieClicked -> onMovieClicked(id = uiAction.movieId)
            HomeUiAction.LoadNextPage -> onLoadNextPage()
            HomeUiAction.RetryClicked -> onRetryClicked()
        }
    }

    private fun onLoadNextPage() {
        loadPage(page = currentPage + 1)
    }

    private fun loadPage(page: Int) {
        if (!hasMorePages || _isLoadingNextPage.value) return

        _isLoadingNextPage.value = page > 1

        viewModelScope.launch {
            syncPopularMoviesUseCase(page)
                .onSuccess { response ->
                    currentPage = response.page
                    totalPages = response.totalPages

                    hasMorePages = currentPage < totalPages

                    _noMoviesErrorMessage.value = null
                }
                .onFailure { exception ->
                    val message = when (exception) {
                        is IOException -> "No internet connection"
                        else -> "Something went wrong"
                    }

                    if (page == 1) {
                        _noMoviesErrorMessage.value = message
                    } else {
                        _nextPageErrorMessage.emit(
                            message
                        )
                    }
                }

            _isLoadingNextPage.value = false
        }
    }

    private fun onMovieClicked(id: String) {
        viewModelScope.launch {
            _navigation.emit(HomeNavigationTarget.Details(id))
        }
    }

    private fun onRetryClicked() {
        _noMoviesErrorMessage.value = null
        loadPage(page = currentPage)
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
}




