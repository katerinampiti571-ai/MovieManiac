package com.example.videoclub.network

import com.example.videoclub.network.model.MovieDto
import com.example.videoclub.network.model.MovieResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int
    ): MovieResponseDto

    @GET("search/movie")
    suspend fun search(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MovieResponseDto
}