package com.example.videoclub.network

import com.example.videoclub.network.model.MovieResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(): MovieResponseDto
}