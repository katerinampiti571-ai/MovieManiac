package com.example.videoclub.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideMovieDatabase(
        @ApplicationContext context: Context
    ): MovieDatabase {
        return Room.databaseBuilder(
                context,
                MovieDatabase::class.java,
                "movie_database"
            ).fallbackToDestructiveMigration(false)
            .build()
    }
    @Provides
    fun provideMovieDao(
        database: MovieDatabase
    ): MovieDao {
        return database.movieDao()
    }
}