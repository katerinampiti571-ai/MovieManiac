package com.example.videoclub.di

import com.example.videoclub.data.PopularMoviesRepository
import com.example.videoclub.data.PopularMoviesRepositoryImpl
import com.example.videoclub.home.usecase.GetPopularMoviesUseCase
import com.example.videoclub.home.usecase.GetPopularMoviesUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class MovieModule {

    @Binds
    abstract fun bindGetAllPopularMoviesUseCase(impl: GetPopularMoviesUseCaseImpl): GetPopularMoviesUseCase

    @Binds
    abstract fun bindPopularMoviesRepository(impl: PopularMoviesRepositoryImpl): PopularMoviesRepository
}
