package com.example.videoclub.details

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.example.videoclub.data.domain.model.Movie
import com.example.videoclub.details.usecase.ObserveMovieDetailsUseCase
import com.example.videoclub.details.usecase.SetMovieFavoriteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private lateinit var observeMovieDetailsUseCase: ObserveMovieDetailsUseCase
    private lateinit var setMovieFavouriteUseCase: SetMovieFavoriteUseCase

    private lateinit var viewModel: DetailsViewModel

    private val savedStateHandle: SavedStateHandle = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    @Before
    fun setup() {
        mockk<SavedStateHandle>()
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every {
            savedStateHandle.toRoute<DetailsRoute>()
        } returns DetailsRoute(
            movieId = "1"
        )

        Dispatchers.setMain(testDispatcher)
        observeMovieDetailsUseCase = mockk()
        setMovieFavouriteUseCase = mockk()
    }

    @Test
    fun `given movie id is resolved, then uiState displays data for this movie`() = runTest {
        // Given
        val movie = Movie(
            id = "1",
            title = "Batman",
            overview = "Dark knight",
            imageUrl = "image.jpg",
            isFavorite = false
        )

        every {
            observeMovieDetailsUseCase("1")
        } returns flowOf(movie)

        viewModel = DetailsViewModel(
            observeMovieDetailsUseCase,
            setMovieFavouriteUseCase,
            savedStateHandle
        )

        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value

        assertEquals(
            DetailsUiState.Data(
                title = "Batman",
                description = "Dark knight",
                imageUrl = "image.jpg",
                isFavorite = false
            ),
            state
        )
    }

    @Test
    fun `given unresolved movie, then uiState displays error state`() = runTest {
        // Given
        every {
            observeMovieDetailsUseCase("1")
        } returns flowOf(null)

        // When
        val viewModel = DetailsViewModel(
            observeMovieDetailsUseCase,
            setMovieFavouriteUseCase,
            savedStateHandle
        )

        advanceUntilIdle()

        // Then
        assertEquals(
            DetailsUiState.Error("Could not resolve movie with movieId: 1"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `when back clicked, back navigation target is emitted`() = runTest {
        // Given
        every {
            observeMovieDetailsUseCase("1")
        } returns flowOf(null)


        val viewModel = DetailsViewModel(
            observeMovieDetailsUseCase,
            setMovieFavouriteUseCase,
            savedStateHandle
        )

        // Then
        viewModel.navigation.test {
            viewModel.onAction(DetailsUiAction.BackClicked)

            advanceUntilIdle()

            assertEquals(
                DetailsNavigationTarget.Back,
                awaitItem()
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given non favorite movie, when favorite is clicked, then uiState sets isFavorite true`() =
        runTest {
            // Given
            val movie = Movie(
                id = "1",
                title = "Batman",
                overview = "Dark knight",
                imageUrl = "image.jpg",
                isFavorite = false
            )

            every {
                observeMovieDetailsUseCase("1")
            } returns flowOf(movie)

            coEvery {
                setMovieFavouriteUseCase(
                    id = "1",
                    isFavourite = true
                )
            } returns Unit
            val viewModel = DetailsViewModel(
                observeMovieDetailsUseCase,
                setMovieFavouriteUseCase,
                savedStateHandle
            )
            advanceUntilIdle()

            // When
            viewModel.onAction(DetailsUiAction.FavoriteClicked)
            advanceUntilIdle()

            // Then
            coVerify(exactly = 1) {
                setMovieFavouriteUseCase(
                    id = "1",
                    isFavourite = true
                )
            }
        }


    @Test
    fun `given favorite movie, when favorite is clicked, then uiState sets isFavorite false`() =
        runTest {
            // Given
            val movie = Movie(
                id = "1",
                title = "Batman",
                overview = "Dark knight",
                imageUrl = "image.jpg",
                isFavorite = true
            )

            every {
                observeMovieDetailsUseCase("1")
            } returns flowOf(movie)

            coEvery {
                setMovieFavouriteUseCase(
                    id = "1",
                    isFavourite = false
                )
            } returns Unit
            val viewModel = DetailsViewModel(
                observeMovieDetailsUseCase,
                setMovieFavouriteUseCase,
                savedStateHandle
            )
            advanceUntilIdle()

            // When
            viewModel.onAction(DetailsUiAction.FavoriteClicked)
            advanceUntilIdle()

            // Then
            coVerify(exactly = 1) {
                setMovieFavouriteUseCase(
                    id = "1",
                    isFavourite = false
                )
            }
        }
}