package com.example.videoclub.details

sealed interface DetailsNavigationTarget {

    data object Back : DetailsNavigationTarget
}
