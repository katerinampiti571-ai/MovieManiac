package com.example.videoclub.searchScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoclub.data.domain.Movie
import com.example.videoclub.homeScreen.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
class SearchScreenViewModel @Inject constructor(
    val searchMovieUseCase: SearchMovieUseCase
)  : ViewModel() {

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
                //If user types the same text again → don’t search again.
                .distinctUntilChanged()
                //This runs every time the text changes (after debounce)
                .collect { searchTerm ->
                    //If text is empty → don’t call API.
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
    private fun CoroutineScope.searchMovie(searchTerm: String) {

        searchJob?.cancel()

        searchJob = launch {
            try {
                val movies = searchMovieUseCase(
                    searchTerm = searchTerm
                )
                _uiState.update { state ->
                    state.copy(
                        content = SearchScreenUiState.Content.Data(
                            movies = movies.map { movie ->
                                movie.mapToUiState()
                            }
                        )
                    )
                }
            } catch (e: IOException) {
                _uiState.update { state ->
                    state.copy(
                        content = SearchScreenUiState.Content.Error(
                            message = "No internet connection"
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        content = SearchScreenUiState.Content.Error(
                            message = "Something went wrong"
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

    fun onClearClicked() {
        _uiState.update {
            it.copy(
                content = SearchScreenUiState.Content.Empty
            )
        }
    }

    fun onSearchTermChanged(searchTerm: String) {
        _uiState.update {
            it.copy(
                searchTerm = searchTerm,
            )
        }
    }
}