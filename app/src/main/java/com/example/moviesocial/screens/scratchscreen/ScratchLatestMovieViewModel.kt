package com.example.moviesocial.screens.scratchscreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesocial.model.MovieDetails
import com.example.moviesocial.service.MovieAPI
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ScratchLatestMovieViewModel : ViewModel(){
    private val BASE_URL = "https://api.themoviedb.org/3/" // Fixed URL

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MovieAPI::class.java)

    private val _movie = mutableStateOf<MovieDetails?>(null)
    val movie: State<MovieDetails?> = _movie

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _isLoading = mutableStateOf(false)

    fun getRandomMovie(){
        _isLoading.value = true
        viewModelScope.launch{
            try {
                val randomPage = (1..10).random()

                val moviesResponse = retrofit.getNowPlayingMovies(page = randomPage)

                if (moviesResponse.results.isNotEmpty()) {
                    val randomMovie = moviesResponse.results.random()

                    val movieDetails = retrofit.getMovieDetails(randomMovie.id)

                    _movie.value = movieDetails
                    _error.value = null
                } else {
                    _error.value = "No movies found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load movies: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /*
    // not using now
    fun getLatestMovie(){
        _isLoading.value = true
        viewModelScope.launch{
            try {
                val response = retrofit.getLatestMovie()
                _movie.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load movies: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

     */
}