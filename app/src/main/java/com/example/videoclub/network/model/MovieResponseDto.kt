package com.example.videoclub.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
class MovieResponseDto (
    @SerializedName("results")
    val popularMovies: List<MovieDto>
)