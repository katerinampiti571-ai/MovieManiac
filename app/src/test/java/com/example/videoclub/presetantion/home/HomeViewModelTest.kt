package com.example.videoclub.presetantion.home

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.example.videoclub.MainDispatcherRule
import com.example.videoclub.data.domain.model.Movie
import com.example.videoclub.data.domain.model.PopularMoviesMetadata
import com.example.videoclub.home.HomeNavigationTarget
import com.example.videoclub.home.HomeUiAction
import com.example.videoclub.home.HomeUiState
import com.example.videoclub.home.HomeViewModel
import com.example.videoclub.home.usecase.ClearMoviesUseCase
import com.example.videoclub.home.usecase.ObservePopularMoviesUseCase
import com.example.videoclub.home.usecase.SyncPopularMoviesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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

    @Before
    fun setUp() {
        coEvery {
            clearMoviesUseCase()
        } returns Unit

        coEvery {
            syncPopularMoviesUseCase(1)
        } returns Result.success(
            PopularMoviesMetadata(
                page = 1,
                totalPages = 10,
            )
        )
    }

    @Test
    fun `given initial state movies are cleared and first page is loading`() = runTest {
        //Given
        every {
            observePopularMoviesUseCase()
        } returns flowOf(emptyList())

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

    @Test
    fun `the initial uiState contains the returned movies and the next page is not loading`() =
        runTest {
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

            val viewModel = HomeViewModel(
                observePopularMoviesUseCase = observePopularMoviesUseCase,
                syncPopularMoviesUseCase = syncPopularMoviesUseCase,
                clearMoviesUseCase = clearMoviesUseCase
            )

            viewModel.uiState.test {
                // Then
                assertEquals(
                    HomeUiState.Data(
                        movies = listOf(
                            HomeUiState.Data.Movie(
                                id = "1",
                                imageUrl = "batman.jpg",
                                title = "Batman",
                            ),
                            HomeUiState.Data.Movie(
                                id = "2",
                                imageUrl = "superman.jpg",
                                title = "Superman",
                            ),
                        ),
                        isLoadingNextPage = false,
                    ),
                    awaitItem()
                )
            }
        }

    @Test
    fun `when load next page is requested, then syncing next page is performed and uiState is updated`() =
        runTest {
            // Given
            val movies = MutableStateFlow(
                listOf(
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
            )

            every {
                observePopularMoviesUseCase()
            } returns movies

            coEvery {
                syncPopularMoviesUseCase(page = 2)
            } answers {
                movies.value += Movie(
                    id = "3",
                    imageUrl = "spiderman.jpg",
                    title = "Spiderman",
                    overview = "Spiderman overview",
                    isFavorite = false
                )

                Result.success(
                    PopularMoviesMetadata(
                        page = 2,
                        totalPages = 10,
                    )
                )
            }

            val viewModel = HomeViewModel(
                observePopularMoviesUseCase = observePopularMoviesUseCase,
                syncPopularMoviesUseCase = syncPopularMoviesUseCase,
                clearMoviesUseCase = clearMoviesUseCase
            )

            viewModel.uiState.test {
                skipItems(1) // Skip the first UiState


                // When
                viewModel.onAction(HomeUiAction.LoadNextPage)

                // Then
                coVerify(exactly = 1) {
                    syncPopularMoviesUseCase(page = 2)
                }

                assertEquals(
                    HomeUiState.Data(
                        movies = listOf(
                            HomeUiState.Data.Movie(
                                id = "1",
                                imageUrl = "batman.jpg",
                                title = "Batman",
                            ),
                            HomeUiState.Data.Movie(
                                id = "2",
                                imageUrl = "superman.jpg",
                                title = "Superman",
                            ),
                        ),
                        isLoadingNextPage = true,
                    ),
                    awaitItem()
                )

                assertEquals(
                    HomeUiState.Data(
                        movies = listOf(
                            HomeUiState.Data.Movie(
                                id = "1",
                                imageUrl = "batman.jpg",
                                title = "Batman",
                            ),
                            HomeUiState.Data.Movie(
                                id = "2",
                                imageUrl = "superman.jpg",
                                title = "Superman",
                            ),
                            HomeUiState.Data.Movie(
                                id = "3",
                                imageUrl = "spiderman.jpg",
                                title = "Spiderman",
                            ),
                        ),
                        isLoadingNextPage = false,
                    ),
                    awaitItem()
                )
            }
        }

    @Test
    fun `when movie is clicked, then navigate to movie details`() = runTest {
        // Given
        every {
            observePopularMoviesUseCase()
        } returns flowOf(emptyList())

        val viewModel = HomeViewModel(
            observePopularMoviesUseCase,
            syncPopularMoviesUseCase,
            clearMoviesUseCase
        )

        viewModel.navigation.test {
            // When
            viewModel.onAction(HomeUiAction.MovieClicked(movieId = "spiderman-01"))

            advanceUntilIdle()

            // Then
            assertEquals(
                HomeNavigationTarget.Details("spiderman-01"),
                awaitItem()
            )
        }
    }

    @Test
    fun `given loading next page, when load next page is requested, then pages don't load twice`() =
        runTest {
            // Given
            val movies = MutableStateFlow(
                listOf(
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
            )

            every {
                observePopularMoviesUseCase()
            } returns movies

            val completeRequest = CompletableDeferred<PopularMoviesMetadata>()

            coEvery {
                syncPopularMoviesUseCase(page = 2)
            } coAnswers {
                Result.success(completeRequest.await())
            }

            // When
            val viewModel = HomeViewModel(
                observePopularMoviesUseCase,
                syncPopularMoviesUseCase,
                clearMoviesUseCase
            )

            viewModel.uiState.test {
                skipItems(1) // Skip the first UiState

                viewModel.onAction(HomeUiAction.LoadNextPage)
                awaitItem()

                // When
                viewModel.onAction(HomeUiAction.LoadNextPage)
                completeRequest.complete(
                    PopularMoviesMetadata(
                        page = 2,
                        totalPages = 10
                    )
                )

                // Then
                coVerify(exactly = 1) {
                    syncPopularMoviesUseCase(2)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }


    @Test
    fun `given no more pages, when load next page is requested, then nothing happens`() = runTest {
        // Given
        val movies = MutableStateFlow(
            listOf(
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
        )

        every {
            observePopularMoviesUseCase()
        } returns movies

        coEvery {
            syncPopularMoviesUseCase(page = 1)
        } coAnswers {
            Result.success(
                PopularMoviesMetadata(
                    totalPages = 1,
                    page = 1,
                )
            )
        }

        // When
        val viewModel = HomeViewModel(
            observePopularMoviesUseCase,
            syncPopularMoviesUseCase,
            clearMoviesUseCase
        )

        viewModel.uiState.test {
            skipItems(1) // Skip the first UiState

            // When
            viewModel.onAction(HomeUiAction.LoadNextPage)

            // Then
            expectNoEvents()
        }
    }


    @Test
    fun `given the sync fails, then UiState shows error`() = runTest {
        // Given
        every {
            observePopularMoviesUseCase()
        } returns flowOf(emptyList())

        coEvery {
            syncPopularMoviesUseCase(1)
        } returns Result.failure(Throwable("404"))

        turbineScope {
            val viewModel = HomeViewModel(
                observePopularMoviesUseCase = observePopularMoviesUseCase,
                syncPopularMoviesUseCase = syncPopularMoviesUseCase,
                clearMoviesUseCase = clearMoviesUseCase
            )

            viewModel.uiState.test {
                // Then
                assertEquals(
                    HomeUiState.Error(
                        message = "Something went wrong"
                    ),
                    awaitItem()
                )

                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}