package com.example.videoclub.data.remote.model

import com.google.gson.annotations.SerializedName


data class MovieDto(
    val id: String,
    @SerializedName("poster_path")
    val imagePath: String,
    val title: String,
    val overview: String,
)