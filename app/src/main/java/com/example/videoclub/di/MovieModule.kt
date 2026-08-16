package com.example.videoclub.di

import com.example.videoclub.data.MoviesRepositoryImpl
import com.example.videoclub.data.domain.MoviesRepository
import com.example.videoclub.details.usecase.ObserveMovieDetailsUseCase
import com.example.videoclub.details.usecase.ObserveMovieDetailsUseCaseImpl
import com.example.videoclub.details.usecase.SetMovieFavoriteUseCase
import com.example.videoclub.details.usecase.SetMovieFavouriteUseCaseImpl
import com.example.videoclub.favorites.usecase.ObserveFavoriteMoviesUseCase
import com.example.videoclub.favorites.usecase.ObserveFavoriteMoviesUseCaseImpl
import com.example.videoclub.home.usecase.ClearMoviesUseCase
import com.example.videoclub.home.usecase.ClearMoviesUseCaseImpl
import com.example.videoclub.home.usecase.ObservePopularMoviesUseCase
import com.example.videoclub.home.usecase.ObservePopularMoviesUseCaseImpl
import com.example.videoclub.home.usecase.SyncPopularMoviesUseCase
import com.example.videoclub.home.usecase.SyncPopularMoviesUseCaseImpl
import com.example.videoclub.search.SearchMovieUseCase
import com.example.videoclub.search.SearchMovieUseCaseImpl
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
    abstract fun bindMoviesRepository(impl: MoviesRepositoryImpl): MoviesRepository

    @Binds
    abstract fun bindClearMoviesUseCase(impl: ClearMoviesUseCaseImpl): ClearMoviesUseCase

    @Binds
    abstract fun bindObserveMovieDetailsUseCase(impl: ObserveMovieDetailsUseCaseImpl): ObserveMovieDetailsUseCase

    @Binds
    abstract fun bindSetMovieFavoriteUseCase(impl: SetMovieFavouriteUseCaseImpl): SetMovieFavoriteUseCase

    @Binds
    abstract fun bindObserveMovieFavoriteUseCase(impl: ObserveFavoriteMoviesUseCaseImpl): ObserveFavoriteMoviesUseCase

    @Binds
    abstract fun bindSearchMovieUseCase(impl: SearchMovieUseCaseImpl): SearchMovieUseCase

}
