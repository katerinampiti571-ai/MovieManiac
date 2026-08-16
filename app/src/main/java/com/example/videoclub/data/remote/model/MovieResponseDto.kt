package com.example.videoclub.data.remote.model

import com.google.gson.annotations.SerializedName


class MovieResponseDto (
    @SerializedName("page")
    val page: Int,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("results")
    val popularMovies: List<MovieDto>
)