package com.example.videoclub.detailsScreen

sealed interface DetailsNavigationTarget {

    data object Back : DetailsNavigationTarget
}
