package com.example.videoclub.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: String,
    @SerializedName("poster_path")
    val imagePath: String,
    val title: String
)