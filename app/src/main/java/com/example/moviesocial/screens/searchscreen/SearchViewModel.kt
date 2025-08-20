package com.example.moviesocial.screens.searchscreen

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesocial.model.MovieItem
import com.example.moviesocial.service.MovieAPI
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


class SearchViewModel(private val searchString: String) : ViewModel() {
    private val BASE_URL = "https://api.themoviedb.org/3/"


    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MovieAPI::class.java)

    private val _searchResults = mutableStateOf<List<MovieItem>>(emptyList())
    val searchResults: State<List<MovieItem>> = _searchResults

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // Search history
    private val _searchHistory = mutableStateOf<List<String>>(emptyList())
    val searchHistory: State<List<String>> = _searchHistory

    init {
        if (searchString.isNotEmpty()){
            searchMovies(searchString)
        }
    }
    fun searchMovies(query: String, context: Context? = null) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = retrofit.searchMovies(query)
                _searchResults.value = response.results
                addToSearchHistory(query)

            } catch (e: HttpException) {
                val errorMsg = when (e.code()) {
                    401 -> "Authentication failed. Please check API key."
                    404 -> "Search endpoint not found."
                    429 -> "Too many requests. Please try again later."
                    500 -> "Server error. Please try again later."
                    else -> "HTTP error ${e.code()}: ${e.message()}"
                }
                _error.value = errorMsg
                _searchResults.value = emptyList()

            } catch (e: SocketTimeoutException) {
                _error.value = "Request timeout. Please check your internet connection."
                _searchResults.value = emptyList()

            } catch (e: IOException) {
                _error.value = "Network error. Please check your internet connection."
                _searchResults.value = emptyList()

            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.message}"
                _searchResults.value = emptyList()

            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun addToSearchHistory(query: String) {
        if (query.isNotBlank() && !_searchHistory.value.contains(query)) {
            _searchHistory.value = listOf(query) + _searchHistory.value.take(9) // Keep last 10 searches
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
        _error.value = null
    }
}