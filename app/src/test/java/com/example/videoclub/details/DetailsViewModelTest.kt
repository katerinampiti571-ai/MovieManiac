package com.example.videoclub.details

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.example.videoclub.data.domain.Movie
import com.example.videoclub.detailsScreen.DetailsNavigationTarget
import com.example.videoclub.detailsScreen.DetailsRoute
import com.example.videoclub.detailsScreen.DetailsUiState
import com.example.videoclub.detailsScreen.DetailsViewModel
import com.example.videoclub.detailsScreen.usecase.ObserveMovieDetailsUseCase
import com.example.videoclub.detailsScreen.usecase.SetMovieFavoriteUseCase
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
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        observeMovieDetailsUseCase = mockk()
        setMovieFavouriteUseCase = mockk()
    }

    @Test
    fun observeMovie_whenMovieExists_updatesUiStateToData() = runTest {
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
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        // When
        every {
            savedStateHandle.toRoute<DetailsRoute>()
        } returns DetailsRoute(
            movieId = "1"
        )
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
    fun observeMovie_whenMovieIsNull_keepsLoadingState() = runTest {
        // Given
        val savedStateHandle = mockk<SavedStateHandle>()
        mockkStatic("androidx.navigation.SavedStateHandleKt")

        every {
            savedStateHandle.toRoute<DetailsRoute>()
        } returns DetailsRoute(
            movieId = "1"
        )
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
            DetailsUiState.Loading,
            viewModel.uiState.value
        )
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
    }
    @Test
    fun onBackClicked_emitsBackNavigation() = runTest {
        // Given
        val savedStateHandle = mockk<SavedStateHandle>()

        mockkStatic("androidx.navigation.SavedStateHandleKt")

        every {
            savedStateHandle.toRoute<DetailsRoute>()
        } returns DetailsRoute(
            movieId = "1"
        )

        every {
            observeMovieDetailsUseCase("1")
        } returns flowOf(null)


        val viewModel = DetailsViewModel(
            observeMovieDetailsUseCase,
            setMovieFavouriteUseCase,
            savedStateHandle
        )
        // When + Then
        viewModel.navigation.test {
            viewModel.onBackClicked()

            advanceUntilIdle()

            assertEquals(
                DetailsNavigationTarget.Back,
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
    }
    @Test
    fun onFavouriteClicked_whenMovieIsNotFavourite_callsUseCaseWithTrue() = runTest {

        // Given
        val savedStateHandle = mockk<SavedStateHandle>()

        mockkStatic("androidx.navigation.SavedStateHandleKt")

        every {
            savedStateHandle.toRoute<DetailsRoute>()
        } returns DetailsRoute(
            movieId = "1"
        )
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

        // When
        val viewModel = DetailsViewModel(
            observeMovieDetailsUseCase,
            setMovieFavouriteUseCase,
            savedStateHandle
        )
        advanceUntilIdle()
        viewModel.onFavouriteClicked()
        advanceUntilIdle()
        // Then
        coVerify(exactly = 1) {
            setMovieFavouriteUseCase(
                id = "1",
                isFavourite = true
            )
        }
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
    }
    @Test
    fun onFavouriteClicked_whenMovieIsFavourite_callsUseCaseWithFalse() = runTest {

        // Given
        val savedStateHandle = mockk<SavedStateHandle>()

        mockkStatic("androidx.navigation.SavedStateHandleKt")

        every {
            savedStateHandle.toRoute<DetailsRoute>()
        } returns DetailsRoute(
            movieId = "1"
        )


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
        // When
        val viewModel = DetailsViewModel(
            observeMovieDetailsUseCase,
            setMovieFavouriteUseCase,
            savedStateHandle
        )
        // Περιμένουμε να γίνει Data state
        advanceUntilIdle()
        viewModel.onFavouriteClicked()
        advanceUntilIdle()
        // Then
        coVerify(exactly = 1) {
            setMovieFavouriteUseCase(
                id = "1",
                isFavourite = false
            )
        }
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
    }
}