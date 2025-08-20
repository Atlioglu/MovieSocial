package com.example.moviesocial.manager

import android.content.Context
import com.example.moviesocial.model.MovieItem
import com.google.gson.Gson

class StorageManager(private val context: Context) {

    fun getAllFavoriteMovies(): MutableList<MovieItem> {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val existingJson = sharedPrefs.getString(FAVORITES_KEY, EMPTY_LIST_JSON) ?: EMPTY_LIST_JSON

        return try {
            gson.fromJson(existingJson, Array<MovieItem>::class.java)?.toMutableList() ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    fun removeFavoriteMovie( movieId: Int) {
        val movieList = getAllFavoriteMovies()
        movieList.removeAll { it.id == movieId }
        saveMovieList(movieList)
    }


    private fun saveMovieList(movieList: List<MovieItem>) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val newJson = gson.toJson(movieList)
        sharedPrefs.edit().putString(FAVORITES_KEY, newJson).apply()
    }

    fun saveFavoriteMovie( movie: MovieItem) {
        val movieList = getAllFavoriteMovies()
        if (movieList.none { it.title == movie.title }) {
            movieList.add(movie)
            saveMovieList(movieList)
        }
    }


    fun isFavoriteMovie(movieTitle: String): Boolean {
        val movieList = getAllFavoriteMovies()
        return movieList.any { it.title == movieTitle }
    }



    companion object{
        private const val PREFS_NAME = "movie_favorites"
        private const val FAVORITES_KEY = "favorites_list"
        private const val EMPTY_LIST_JSON = "[]"
    }
}

