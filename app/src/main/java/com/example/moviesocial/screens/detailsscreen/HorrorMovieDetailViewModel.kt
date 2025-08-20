package com.example.moviesocial.screens.detailsscreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesocial.manager.StorageManager
import com.example.moviesocial.model.MovieDetails
import com.example.moviesocial.model.MovieItem
import com.example.moviesocial.service.MovieAPI
import kotlinx.coroutines.launch

class HorrorMovieDetailViewModel(private val storageManager: StorageManager) : ViewModel() {

    // UI State
    private val _uiState = mutableStateOf(HorrorMovieDetailUiState())
    val uiState: State<HorrorMovieDetailUiState> = _uiState

    // Movie Details
    private val _movieDetails = mutableStateOf<MovieDetails?>(null)
    val movieDetails: State<MovieDetails?> = _movieDetails

    // Genre information
    private val _isHorror = mutableStateOf(false)
    val isHorror: State<Boolean> = _isHorror


    // Light bulb state (horror theme specific)
    private val _isLightOn = mutableStateOf(true)
    val isLightOn: State<Boolean> = _isLightOn

    // Favorites state
    private val _isFavorite = mutableStateOf(false)
    val isFavorite: State<Boolean> = _isFavorite

    // Toast messages state
    private val _toastMessage = mutableStateOf<String?>(null)
    val toastMessage: State<String?> = _toastMessage

    fun loadMovieDetails(movieId: Int, movieAPI: MovieAPI) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val movieDetails = movieAPI.getMovieDetails(movieId)
                _movieDetails.value = movieDetails

                val firstGenre = movieDetails.genres.firstOrNull()?.name
                firstGenre?.let { genre ->
                    _isHorror.value = genre == "Horror"
                    _toastMessage.value = "Genre: $genre"
                }

                _uiState.value = _uiState.value.copy(isLoading = false)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error loading genre: ${e.message}"
                )
                _toastMessage.value = "Error loading genre: ${e.message}"
            }
        }
    }

    fun initializeFavoriteState(movieTitle: String) {
        _isFavorite.value = storageManager.isFavoriteMovie(movieTitle)
    }

    fun toggleFavorite(movieItem: MovieItem) {
        if (_isFavorite.value) {
            storageManager.removeFavoriteMovie(movieItem.id)
            _isFavorite.value = false
            _toastMessage.value = "Removed from favorites"
        } else {
            storageManager.saveFavoriteMovie(movieItem)
            _isFavorite.value = true
            _toastMessage.value = "Added to favorites"
        }
    }



    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

}

data class HorrorMovieDetailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDarkMode: Boolean = true
)