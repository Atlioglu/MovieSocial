package com.example.moviesocial.screens.favscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import com.example.moviesocial.manager.StorageManager
import com.example.moviesocial.model.FavUiState

class FavViewModel(private val storageManager: StorageManager) : ViewModel() {

    private val _uiState = MutableStateFlow(FavUiState())
    val uiState: StateFlow<FavUiState> = _uiState.asStateFlow()

    fun loadFavoriteMovies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val movies = storageManager.getAllFavoriteMovies()
                _uiState.value = _uiState.value.copy(
                    favoriteMovies = movies,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }
    fun onFavButtonClicked(id: Int){
        storageManager.removeFavoriteMovie(id)
        loadFavoriteMovies()
    }


}

