package com.example.moviesocial.screens.detailsscreen

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.moviesocial.manager.StorageManager
import com.example.moviesocial.model.MovieDetails
import com.example.moviesocial.model.MovieItem
import com.example.moviesocial.service.MovieAPI

class MovieDetailViewModel(private val storageManager: StorageManager) : ViewModel() {

    // UI State
    private val _uiState = mutableStateOf(MovieDetailUiState())
    val uiState: State<MovieDetailUiState> = _uiState

    // Movie Details
    private val _movieDetails = mutableStateOf<MovieDetails?>(null)
    val movieDetails: State<MovieDetails?> = _movieDetails

    // Genre information
    private val _isRomance = mutableStateOf(false)
    val isRomance: State<Boolean> = _isRomance

    private val _isThriller = mutableStateOf(false)
    val isThriller: State<Boolean> = _isThriller

    // Glass effect state
    private val _isGlass = mutableStateOf(true)
    val isGlass: State<Boolean> = _isGlass

    // Favorites state
    private val _isFavorite = mutableStateOf(false)
    val isFavorite: State<Boolean> = _isFavorite

    fun loadMovieDetails(movieId: Int, movieAPI: MovieAPI) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val movieDetails = movieAPI.getMovieDetails(movieId)
                _movieDetails.value = movieDetails

                val firstGenre = movieDetails.genres.firstOrNull()?.name
                firstGenre?.let { genre ->
                    _isRomance.value = genre == "Romance"
                    _isThriller.value = genre == "Thriller"
                }

                _uiState.value = _uiState.value.copy(isLoading = false)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error loading genre: ${e.message}"
                )
            }
        }
    }

    fun initializeFavoriteState(movieTitle: String) {
        _isFavorite.value = storageManager.isFavoriteMovie(movieTitle)
    }

    fun toggleFavorite( movieItem: MovieItem) {
        if (_isFavorite.value) {
            storageManager.removeFavoriteMovie(movieItem.id)
            _isFavorite.value = false
        } else {
            storageManager.saveFavoriteMovie(movieItem)
            _isFavorite.value = true
        }
    }

    fun breakGlass() {
        _isGlass.value = false
    }

    fun fixGlass() {
        _isGlass.value = true
    }

    fun resetGlass() {
        _isGlass.value = true
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}


// UI State data class
data class MovieDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)