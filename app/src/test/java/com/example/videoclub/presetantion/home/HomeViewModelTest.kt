package com.example.videoclub.presetantion.home

import com.example.videoclub.MainDispatcherRule
import com.example.videoclub.data.domain.Movie
import com.example.videoclub.homeScreen.HomeNavigationTarget
import com.example.videoclub.homeScreen.HomeUiAction
import com.example.videoclub.homeScreen.HomeUiState
import com.example.videoclub.homeScreen.HomeViewModel
import com.example.videoclub.homeScreen.usecase.ClearMoviesUseCase
import com.example.videoclub.homeScreen.usecase.ObservePopularMoviesUseCase
import com.example.videoclub.homeScreen.usecase.SyncPopularMoviesUseCase
import com.example.videoclub.network.model.MovieResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import org.junit.Test
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.collections.emptyList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Rule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runCurrent
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val observePopularMoviesUseCase =
        mockk<ObservePopularMoviesUseCase>()

    private val syncPopularMoviesUseCase =
        mockk<SyncPopularMoviesUseCase>()

    private val clearMoviesUseCase =
        mockk<ClearMoviesUseCase>()


    @Test
    fun init_clearsMoviesAndLoadsFirstPage() = runTest {
        //Given
        every {
            observePopularMoviesUseCase()
        } returns flowOf(emptyList())

        coEvery {
            clearMoviesUseCase()
        } returns Unit

        coEvery {
            syncPopularMoviesUseCase(1)
        } returns Result.success(
            MovieResponseDto(
                page = 1,
                totalPages = 10,
                popularMovies = emptyList()
            )
        )
        //When
        HomeViewModel(
            observePopularMoviesUseCase,
            syncPopularMoviesUseCase,
            clearMoviesUseCase
        )
        //Then
        coVerify {
            clearMoviesUseCase()
        }
        coVerify {
            syncPopularMoviesUseCase(1)
        }
    }
    @Test //ViewModel correctly transforms the domain models into UI models.
    fun uiState_returnsMoviesFromUseCase() = runTest {
        // Given
        val movies = listOf(
            Movie(
                id = "1",
                imageUrl = "batman.jpg",
                title = "Batman",
                overview = "Batman overview",
                isFavorite = false
            ),
            Movie(
                id = "2",
                imageUrl = "superman.jpg",
                title = "Superman",
                overview = "Superman overview",
                isFavorite = true
            )
        )
        every {
            observePopularMoviesUseCase()
        } returns flowOf(movies)
    //"When the ViewModel calls observePopularMoviesUseCase(),
    //return these two movies."
        coEvery {
            clearMoviesUseCase()
        } returns Unit
        coEvery {
            syncPopularMoviesUseCase(1)
        } returns Result.success(
            MovieResponseDto(
                page = 1,
                totalPages = 10,
                popularMovies = emptyList()
            )
        )
        // When
        val viewModel = HomeViewModel(
            observePopularMoviesUseCase,
            syncPopularMoviesUseCase,
            clearMoviesUseCase
        )
        // Start observing uiState
        val state = viewModel.uiState.first {
            it is HomeUiState.Data
        }

        advanceUntilIdle()
    //Without this, your test might read uiState
    // before the ViewModel has finished its work.

        // Then
        assertTrue(state is HomeUiState.Data)
    //This checks that the ViewModel is
    //no longer in the Loading state.

        val data = state as HomeUiState.Data

        assertEquals(2, data.movies.size)

        assertEquals("1", data.movies[0].id)
        assertEquals("Batman", data.movies[0].title)
        assertEquals("batman.jpg", data.movies[0].imageUrl)

        assertEquals("2", data.movies[1].id)
        assertEquals("Superman", data.movies[1].title)
        assertEquals("superman.jpg", data.movies[1].imageUrl)

        assertFalse(data.isLoadingMore)
    }
    @Test
    fun loadNextPage_loadsSecondPage() = runTest {
        // Given
        every {
            observePopularMoviesUseCase()
        } returns flowOf(emptyList())
        coEvery {
            clearMoviesUseCase()
        } returns Unit
        coEvery {
            syncPopularMoviesUseCase(1)
        } returns Result.success(
            MovieResponseDto(
                page = 1,
                totalPages = 10,
                popularMovies = emptyList()
            )
        )

        coEvery {
            syncPopularMoviesUseCase(2)
        } returns Result.success(
            MovieResponseDto(
                page = 2,
                totalPages = 10,
                popularMovies = emptyList()
            )
        )
        // When
        val viewModel = HomeViewModel(
            observePopularMoviesUseCase,
            syncPopularMoviesUseCase,
            clearMoviesUseCase
        )
        advanceUntilIdle()
        viewModel.onAction(HomeUiAction.LoadNextPage)
        // Then
        advanceUntilIdle()
        coVerify(exactly = 1) {
            syncPopularMoviesUseCase(2)
        }
        coVerify(exactly = 1) {
            syncPopularMoviesUseCase(1)
        }
    }
    @Test
    fun onMovieClick_emitsNavigationEvent() = runTest {
        // Given
        every {
            observePopularMoviesUseCase()
        } returns flowOf(emptyList())

        coEvery {
            clearMoviesUseCase()
        } returns Unit

        coEvery {
            syncPopularMoviesUseCase(1)
        } returns Result.success(
            MovieResponseDto(
                page = 1,
                totalPages = 10,
                popularMovies = emptyList()
            )
        )

        val viewModel = HomeViewModel(
            observePopularMoviesUseCase,
            syncPopularMoviesUseCase,
            clearMoviesUseCase
        )
        // Start waiting for the navigation event
        val navigationDeferred = async {
            viewModel.navigation.first()
        }

        advanceUntilIdle()

        // When
        viewModel.onMovieClick("123")

        advanceUntilIdle()

        // Then
        assertEquals(
            HomeNavigationTarget.Details("123"),
            navigationDeferred.await()
        )
    }

    //if duplicate loading
    @Test
    fun loadNextPage_doesNotLoadTwiceWhileAlreadyLoading() = runTest {
        // Given
        every {
            observePopularMoviesUseCase()
        } returns flowOf(emptyList())

        coEvery {
            clearMoviesUseCase()
        } returns Unit
        // First page loading
        coEvery {
            syncPopularMoviesUseCase(1)
        } returns Result.success(
            MovieResponseDto(
                page = 1,
                totalPages = 10,
                popularMovies = emptyList()
            )
        )
        // This controls when page 2 finishes loading
        val apiCallFinished = CompletableDeferred<Unit>()
        // Second page loading
        coEvery {
            syncPopularMoviesUseCase(2)
        } coAnswers {
            // Keep this API call running
            apiCallFinished.await()
            Result.success(
                MovieResponseDto(
                    page = 2,
                    totalPages = 10,
                    popularMovies = emptyList()
                )
            )
        }
        // When
        val viewModel = HomeViewModel(
            observePopularMoviesUseCase,
            syncPopularMoviesUseCase,
            clearMoviesUseCase
        )
        // Wait for initial page (page 1)
        advanceUntilIdle()
        // User reaches the bottom -> first request
        viewModel.onAction(HomeUiAction.LoadNextPage)
        // Allow the coroutine to start
        runCurrent()
        // User reaches the bottom again while page 2 is still loading
        viewModel.onAction(HomeUiAction.LoadNextPage)
        // Finish the fake API call
        apiCallFinished.complete(Unit)
        // Let remaining coroutines finish
        advanceUntilIdle()
        // Then
        coVerify(exactly = 1) {
            syncPopularMoviesUseCase(2)
        }
    }
    @Test
    fun loadNextPage_doesNotLoadWhenThereAreNoMorePages() = runTest {
        // Given
        every {
            observePopularMoviesUseCase()
        } returns flowOf(emptyList())
        coEvery {
            clearMoviesUseCase()
        } returns Unit
        // First page is the last page
        coEvery {
            syncPopularMoviesUseCase(1)
        } returns Result.success(
            MovieResponseDto(
                page = 1,
                totalPages = 1,
                popularMovies = emptyList()
            )
        )
        // When
        val viewModel = HomeViewModel(
            observePopularMoviesUseCase,
            syncPopularMoviesUseCase,
            clearMoviesUseCase
        )
        // Wait for initial loading
        advanceUntilIdle()
        // User tries to load more
        viewModel.onAction(HomeUiAction.LoadNextPage)
        advanceUntilIdle()
        // Then
        coVerify(exactly = 0) {
            syncPopularMoviesUseCase(2)
        }
    }
    @Test
    fun loadNextPage_doesNotLoadAfterFailure() = runTest {
        // Given
        every {
            observePopularMoviesUseCase()
        } returns flowOf(emptyList())
        coEvery {
            clearMoviesUseCase()
        } returns Unit
        // Initial API call fails
        coEvery {
            syncPopularMoviesUseCase(1)
        } returns Result.failure(
            Exception("Network error")
        )
        // When
        val viewModel = HomeViewModel(
            observePopularMoviesUseCase,
            syncPopularMoviesUseCase,
            clearMoviesUseCase
        )
        advanceUntilIdle()
        // User tries to load next page
        viewModel.onAction(HomeUiAction.LoadNextPage)
        advanceUntilIdle()
        // Then
        coVerify(exactly = 0) {
            syncPopularMoviesUseCase(2)
        }
    }
    @Test
    fun loadPage_failure_emitsErrorState() = runTest {
        // Given
        every {
            observePopularMoviesUseCase()
        } returns flowOf(emptyList())
        coEvery {
            clearMoviesUseCase()
        } returns Unit
        coEvery {
            syncPopularMoviesUseCase(1)
        } returns Result.failure(
            IOException("No internet")
        )
        // When
        val viewModel = HomeViewModel(
            observePopularMoviesUseCase,
            syncPopularMoviesUseCase,
            clearMoviesUseCase
        )
        // Then
        val state = viewModel.uiState.first {
            it is HomeUiState.Error
        }
        assertTrue(state is HomeUiState.Error)

        assertEquals(
            "No internet connection",
            (state as HomeUiState.Error).message
        )
    }
}