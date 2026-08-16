package com.example.videoclub.di

import com.example.videoclub.data.MoviesRepositoryImpl
import com.example.videoclub.data.domain.MoviesRepository
import com.example.videoclub.detailsScreen.usecase.ObserveMovieDetailsUseCase
import com.example.videoclub.detailsScreen.usecase.ObserveMovieDetailsUseCaseImpl
import com.example.videoclub.detailsScreen.usecase.SetMovieFavoriteUseCase
import com.example.videoclub.detailsScreen.usecase.SetMovieFavouriteUseCaseImpl
import com.example.videoclub.favorites.usecase.ObserveFavoriteMoviesUseCase
import com.example.videoclub.favorites.usecase.ObserveFavoriteMoviesUseCaseImpl
import com.example.videoclub.homeScreen.usecase.ClearMoviesUseCase
import com.example.videoclub.homeScreen.usecase.ClearMoviesUseCaseImpl
import com.example.videoclub.homeScreen.usecase.ObservePopularMoviesUseCase
import com.example.videoclub.homeScreen.usecase.ObservePopularMoviesUseCaseImpl
import com.example.videoclub.homeScreen.usecase.SyncPopularMoviesUseCase
import com.example.videoclub.homeScreen.usecase.SyncPopularMoviesUseCaseImpl
import com.example.videoclub.searchScreen.SearchMovieUseCase
import com.example.videoclub.searchScreen.SearchMovieUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class MovieModule {

    @Binds
    abstract fun bindObservePopularMoviesUseCase(impl: ObservePopularMoviesUseCaseImpl): ObservePopularMoviesUseCase

    @Binds
    abstract fun bindSyncMoviesUseCase(impl: SyncPopularMoviesUseCaseImpl): SyncPopularMoviesUseCase

    @Binds
    abstract fun bindPopularMoviesRepository(impl: MoviesRepositoryImpl): MoviesRepository

    @Binds
    abstract fun bindClearMoviesUseCase(impl: ClearMoviesUseCaseImpl): ClearMoviesUseCase

    @Binds
    abstract fun bindGetDetailsUseCase(impl: ObserveMovieDetailsUseCaseImpl): ObserveMovieDetailsUseCase

    @Binds
    abstract fun bindSetMovieFavoriteUseCase(impl: SetMovieFavouriteUseCaseImpl): SetMovieFavoriteUseCase

    @Binds
    abstract fun observeMovieFavoriteUseCase(impl: ObserveFavoriteMoviesUseCaseImpl): ObserveFavoriteMoviesUseCase
    @Binds
    abstract fun searchMovieUseCase(impl: SearchMovieUseCaseImpl): SearchMovieUseCase

}
