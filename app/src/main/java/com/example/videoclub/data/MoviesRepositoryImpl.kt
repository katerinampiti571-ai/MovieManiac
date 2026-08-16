package com.example.videoclub.data

import com.example.videoclub.data.domain.model.Movie
import com.example.videoclub.data.domain.MoviesRepository
import com.example.videoclub.data.domain.model.PopularMoviesMetadata
import com.example.videoclub.data.local.model.FavoritesEntity
import com.example.videoclub.data.local.MovieDao
import com.example.videoclub.data.local.model.MovieEntity
import com.example.videoclub.data.mappers.toMovie
import com.example.videoclub.data.mappers.toMovieEntity
import com.example.videoclub.data.remote.MovieApiService
import com.example.videoclub.data.remote.NetworkEndpoints.IMAGES_BASE_URL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import okio.IOException

import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService,
    private val movieDao: MovieDao
) : MoviesRepository {

    override suspend fun syncPopularMovies(
        page: Int
    ): Result<PopularMoviesMetadata> {
        return try {
            val response = apiService.getPopularMovies(
                page = page
            )
            val movieEntities = response.popularMovies.map { movieDto ->
                movieDto.toMovieEntity()
            }

            movieDao.addMovies(movieEntities)

            Result.success(
                PopularMoviesMetadata(
                    totalPages = response.totalPages,
                    page = response.page,
                )
            )

        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override fun observePopularMovies(): Flow<List<Movie>> {
        return combine(
            movieDao.observePopularMovies(),
            movieDao.observeFavorites()
        ) { movies, favorites ->

            movies.map { movieEntity ->
                val favorite = favorites.find { favoriteEntity ->
                    favoriteEntity.id == movieEntity.id
                }

                movieEntity.toMovie(isFavorite = favorite?.isFavourite ?: false)
            }
        }
    }

    override suspend fun clearMovies() {
        movieDao.clearMovies()
    }

    override fun observeMovie(
        id: String
    ): Flow<Movie?> {
        return combine(
            movieDao.observeMovie(id),
            movieDao.isFavorite(movieId = id)
        ) { movieEntity, isFavorite ->
            movieEntity?.toMovie(isFavorite = isFavorite)
        }
    }

    override suspend fun setMovieFavourite(
        id: String,
        isFavourite: Boolean
    ) {
        movieDao.setFavorite(
            FavoritesEntity(
                id = id,
                isFavourite = isFavourite
            )
        )
    }

    override fun observeFavoriteMovies(): Flow<List<Movie>> {
        return observePopularMovies()
            .map { movies ->
                movies.filter { movie ->
                    movie.isFavorite
                }
            }
    }

    override suspend fun searchMovie(
        searchTerm: String
    ): Result<List<Movie>> {
        if (searchTerm.isBlank()) {
            return Result.success(emptyList())
        }

        val response = try {
            apiService.search(
                query = searchTerm
            )
        } catch (e: Exception) {
            return Result.failure(e)
        }

        val movieEntities = response.popularMovies.map { movieDto ->
            movieDto.toMovieEntity()
        }

        movieDao.addMovies(movieEntities)

        return Result.success(
            movieEntities.map { movieEntity ->
                movieEntity.toMovie(
                    // We don't care about isFavorite flag here
                    isFavorite = false
                )
            }
        )
    }
}

