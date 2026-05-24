package com.example.videoclub.data

import com.example.videoclub.network.MovieApiService
import com.example.videoclub.network.NetworkEndpoints
import com.example.videoclub.network.model.MovieResponseDto
import javax.inject.Inject

interface PopularMoviesRepository {
    suspend fun getPopularMovies(): List<Movie>
}

class PopularMoviesRepositoryImpl @Inject constructor(
  private val apiService: MovieApiService
): PopularMoviesRepository{
    override suspend fun getPopularMovies(): List<Movie> {
        // TODO return Result<List<Movie>>
        return try {
            val response : MovieResponseDto = apiService.getPopularMovies()
            response.popularMovies.map { movieDto ->
                Movie(
                    id = movieDto.id,
                    // TODO set this to a const val
                    imageUrl = "https://image.tmdb.org/t/p/original" + movieDto.imagePath,
                    title = movieDto.title
                )
            }
        }
        catch (e: Exception) {
            emptyList()
        }
    }
}