package com.example.videoclub.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoclub.data.domain.model.Movie
import com.example.videoclub.home.HomeNavigationTarget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
internal class SearchScreenViewModel @Inject constructor(
    val searchMovieUseCase: SearchMovieUseCase
) : ViewModel() {

    private val _navigation = MutableSharedFlow<SearchNavigationTarget>()
    val navigation = _navigation.asSharedFlow()

    private val _uiState = MutableStateFlow(
        SearchScreenUiState.INITIAL
    )

    val uiState: StateFlow<SearchScreenUiState> = _uiState.asStateFlow()

    var searchJob: Job? = null

    init {
        viewModelScope.launch {
            _uiState.map {
                it.searchTerm
            }.debounce(300)
                .distinctUntilChanged()
                .collect { searchTerm ->
                    if (searchTerm.isBlank()) {
                        _uiState.update {
                            it.copy(
                                content = SearchScreenUiState.Content.Empty
                            )
                        }
                        return@collect
                    }

                    _uiState.update {
                        it.copy(
                            content = SearchScreenUiState.Content.Loading
                        )
                    }

                    searchMovie(searchTerm = searchTerm)
                }
        }
    }

    fun onAction(action: SearchUiAction) {
        when (action) {
            SearchUiAction.ClearClicked -> onClearClicked()
            is SearchUiAction.SearchTermChanged -> onSearchTermChanged(searchTerm = action.searchTerm)
            is SearchUiAction.MovieClicked -> onMovieClicked(movieId = action.movieId)
        }
    }

    private fun CoroutineScope.searchMovie(searchTerm: String) {
        searchJob?.cancel()

        searchJob = launch {
            searchMovieUseCase(
                searchTerm = searchTerm
            ).onSuccess {
                _uiState.update { state ->
                    state.copy(
                        content = SearchScreenUiState.Content.Data(
                            movies = it.map { movie ->
                                movie.mapToUiState()
                            }
                        )
                    )
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        content = SearchScreenUiState.Content.Error(
                            message = it.message ?: "Something went wrong"
                        )
                    )
                }
            }
        }
    }

    private fun Movie.mapToUiState(): SearchScreenUiState.Content.Data.Movie {
        return SearchScreenUiState.Content.Data.Movie(
            id = this.id,
            imageUrl = this.imageUrl,
            title = this.title
        )
    }

    private fun onClearClicked() {
        _uiState.update {
            it.copy(
                content = SearchScreenUiState.Content.Empty
            )
        }
    }

    private fun onSearchTermChanged(searchTerm: String) {
        _uiState.update {
            it.copy(
                searchTerm = searchTerm,
            )
        }
    }

    private fun onMovieClicked(movieId: String) {
        viewModelScope.launch {
            _navigation.emit(SearchNavigationTarget.Details(movieId = movieId))
        }
    }
}