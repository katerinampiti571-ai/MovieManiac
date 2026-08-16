package com.example.videoclub.data

import android.util.Log
import com.example.videoclub.data.domain.Movie
import com.example.videoclub.data.domain.MoviesRepository
import com.example.videoclub.data.local.FavoritesEntity
import com.example.videoclub.data.local.MovieDao
import com.example.videoclub.data.local.MovieEntity
import com.example.videoclub.network.MovieApiService
import com.example.videoclub.network.NetworkEndpoints.IMAGES_BASE_URL
import com.example.videoclub.network.model.MovieResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService,
    private val movieDao: MovieDao
) : MoviesRepository {

    override suspend fun syncPopularMovies(
        page: Int
    ): Result<MovieResponseDto> {
        return try {
            val response = apiService.getPopularMovies(
                page = page
            )
            val movieEntities = response.popularMovies.map { movieDto ->
                MovieEntity(
                    id = movieDto.id,
                    title = movieDto.title,
                    overview = movieDto.overview,
                    imageUrl = movieDto.imagePath?.let { imagePath ->
                        IMAGES_BASE_URL + imagePath
                    }.orEmpty()
                )
            }

            movieDao.addMovies(movieEntities)

            Result.success(response)

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

                Movie(
                    id = movieEntity.id,
                    title = movieEntity.title,
                    overview = movieEntity.overview,
                    imageUrl = movieEntity.imageUrl,
                    isFavorite = favorite?.isFavourite ?: false
                )
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

            if (movieEntity == null) {
                null
            } else {
                Movie(
                    id = movieEntity.id,
                    title = movieEntity.title,
                    overview = movieEntity.overview,
                    imageUrl = movieEntity.imageUrl,
                    isFavorite = isFavorite
                )
            }
        }
    }

    override suspend fun setMovieFavourite(
        id: String,
        isFavourite: Boolean
    ) {
        movieDao.saveFavorites(
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
    ): List<Movie> {
        if (searchTerm.isBlank()) {
            return emptyList()
        }

        val response = apiService.search(
            query = searchTerm
        )

        val movieEntities = response.popularMovies.map { movieDto ->
            MovieEntity(
                id = movieDto.id,
                title = movieDto.title,
                overview = movieDto.overview,
                imageUrl = movieDto.imagePath?.let { imagePath ->
                    IMAGES_BASE_URL + imagePath
                }.orEmpty()
            )
        }

        movieDao.addMovies(movieEntities)

        return movieEntities.map { movieEntity ->
            Movie(
                id = movieEntity.id,
                title = movieEntity.title,
                overview = movieEntity.overview,
                imageUrl = movieEntity.imageUrl,
                isFavorite = false
            )
        }
    }
}

