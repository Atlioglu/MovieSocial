package com.example.moviesocial.model

data class FavUiState(
    val favoriteMovies: List<MovieItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)