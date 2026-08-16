package com.example.videoclub.homeScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoclub.data.domain.Movie
import com.example.videoclub.homeScreen.usecase.ClearMoviesUseCase
import com.example.videoclub.homeScreen.usecase.ObservePopularMoviesUseCase
import com.example.videoclub.homeScreen.usecase.SyncPopularMoviesUseCase
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
    private var currentPage = 1  //κρατάει ποια σελίδα έχεις φορτώσει τελευταία
    private var isLoading = false  //προστατεύει από διπλά API calls
    private var hasMore =
        true  //λέει αν υπάρχουν άλλες σελίδες από API μέχρι να αποδειχθεί το αντίθετο
    private var totalPages = Int.MAX_VALUE //όριο σελίδων από backend
    private val _loadingMore = MutableStateFlow(false) //UiState για spinner
    private val _error = MutableStateFlow<String?>(null)
    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()
    private var initialLoadFinished = false

    init {
        viewModelScope.launch {
            clearMoviesUseCase()
            loadPage(1) //φορτώνει page 1 από API

        }
    }

    // DB + UiState aka: παίρνω δεδομένα από Room + loading state
    // και τα μετατρέπω σε UI state
    val uiState: StateFlow<HomeUiState> =
        combine(
            observePopularMoviesUseCase(),
            _loadingMore,
            _error
        ) { movies, loadingMore, error ->
            if (movies.isNotEmpty()) {
                HomeUiState.Data(
                    movies = movies.mapToUiState(),
                    isLoadingMore = loadingMore,
                )
            } else if (error != null) {
                HomeUiState.Error(error)
            } else {
                HomeUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(),
            initialValue = HomeUiState.Loading
        )

    private suspend fun loadPage(page: Int) {
        if (isLoading) return
        isLoading = true
        _loadingMore.value = page!= 1
        //api call
        syncPopularMoviesUseCase(page)
            .onSuccess { response ->
                currentPage = response.page
                totalPages = response.totalPages

                hasMore = currentPage < totalPages

                _error.value = null
            }
            .onFailure { exception ->
                val message = when (exception) {
                    is IOException -> "No internet connection"
                    else -> "Something went wrong"
                }

                if (page == 1) {
                    _error.value = message
                } else {
                    _events.emit(
                        message
                    )
                }
            }
        _loadingMore.value = false
        isLoading = false
    }

    fun loadNextPage() {

        if (isLoading) return

        viewModelScope.launch {
            loadPage(currentPage + 1)
        }
    }

    fun onMovieClick(id: String) {
        viewModelScope.launch {
            _navigation.emit(HomeNavigationTarget.Details(id))
        }
    }

    fun onAction(uiAction: HomeUiAction) {
        when (uiAction) {
            is HomeUiAction.MovieClicked -> {
                onMovieClick(id = uiAction.movieId)
            }

            HomeUiAction.LoadNextPage -> {
                loadNextPage()
            }

            HomeUiAction.RetryClicked -> {
                retry()
            }
        }
    }

    private fun retry() {
        _error.value = null
        viewModelScope.launch {
            loadPage(currentPage)
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




