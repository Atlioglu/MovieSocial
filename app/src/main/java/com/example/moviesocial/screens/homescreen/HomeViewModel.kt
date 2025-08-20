package com.example.moviesocial.screens.homescreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesocial.manager.StorageManager
import com.example.moviesocial.model.MovieItem
import com.example.moviesocial.service.MovieAPI
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModel(private val storageManager: StorageManager) : ViewModel() {
    private val BASE_URL = "https://api.themoviedb.org/3/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MovieAPI::class.java)

    private val _movieList = mutableStateOf<List<MovieItem>>(emptyList())
    val movieList: State<List<MovieItem>> = _movieList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private var currentPage = 1
    private var isLoadingMore = false

    fun getMovies(page: Int = 1) {
        viewModelScope.launch {
            if (page == 1) {
                _isLoading.value = true
                _movieList.value = emptyList()
            }

            _error.value = null
            isLoadingMore = true

            try {
                val response = retrofit.getNowPlayingMovies(page = page)

                if (page == 1) {
                    _movieList.value = response.results
                } else {
                    _movieList.value = _movieList.value + response.results
                }

                currentPage = page
            } catch (e: Exception) {
                _error.value = "Failed to load movies: ${e.message}"
                if (page == 1) {
                    _movieList.value = emptyList()
                }
            } finally {
                _isLoading.value = false
                isLoadingMore = false
            }
        }
    }
    fun loadNextPage() {
        if (!isLoadingMore) {
            getMovies(currentPage + 1)
        }
    }

    fun getTrendingMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = retrofit.getTrendingMovies()
                _movieList.value = response.results
            } catch (e: Exception) {
                _error.value = "Failed to load trending movies: ${e.message}"
                _movieList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun favoriteUiChange(movieItem: MovieItem): Boolean {
        return storageManager.isFavoriteMovie(movieItem.title)
    }

    fun onIconButtonClicked(id: Int){
        storageManager.removeFavoriteMovie(id)
        storageManager.getAllFavoriteMovies()
    }
}
