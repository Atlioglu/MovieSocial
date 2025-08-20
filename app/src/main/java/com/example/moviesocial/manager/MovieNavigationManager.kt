package com.example.moviesocial.manager

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.moviesocial.model.MovieDetails
import com.example.moviesocial.model.MovieItem
import com.example.moviesocial.screens.detailsscreen.HorrorMovieDetailScreen
import com.example.moviesocial.screens.detailsscreen.MovieDetailScreen
import com.example.moviesocial.screens.detailsscreen.MovieDetailScreenWithHeartAndBreak
import com.example.moviesocial.service.MovieAPI
import com.example.moviesocial.screens.detailsscreen.MovieDetailViewModel
import org.koin.androidx.compose.koinViewModel

class MovieNavigationManager(private val movieAPI: MovieAPI, private val context: Context) {


    companion object {
        private const val HORROR_GENRE = "Horror"
    }

    fun isAnimationEnabled(): Boolean {
        val sharedPref = context.getSharedPreferences("ANIMATION_PREFS", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("animation_statue", false)
    }

    suspend fun getMovieDetailDestination(movieItem: MovieItem): MovieDetailDestination {
        return try {
            val movieDetails = movieAPI.getMovieDetails(movieItem.id)
            val firstGenre = movieDetails.genres.firstOrNull()?.name

            if (firstGenre == HORROR_GENRE) {
                MovieDetailDestination.Horror(movieItem, movieDetails)
            } else {
                MovieDetailDestination.WithHeart(movieItem, movieDetails)
            }
        } catch (e: Exception) {
            MovieDetailDestination.WithHeart(movieItem, null)
        }
    }
}

sealed class MovieDetailDestination(val movieItem: MovieItem, val movieDetails: MovieDetails?) {
    class Horror(movieItem: MovieItem, movieDetails: MovieDetails?) : MovieDetailDestination(movieItem, movieDetails)
    class WithHeart(movieItem: MovieItem, movieDetails: MovieDetails?) : MovieDetailDestination(movieItem, movieDetails)
}

@Composable
fun MovieDetailRouter(
    movieItem: MovieItem,
    movieAPI: MovieAPI
) {
    val viewModel = koinViewModel<MovieDetailViewModel>()

    LaunchedEffect(viewModel.isGlass){
        viewModel.fixGlass()
    }
    val context = LocalContext.current
    val navigationManager = remember { MovieNavigationManager(movieAPI, context) }
    val isAnimationEnabled = remember { navigationManager.isAnimationEnabled() }

    if (!isAnimationEnabled) {
        MovieDetailScreen(movieItem = movieItem, movieAPI = movieAPI)
        return
    }

    var destination by remember { mutableStateOf<MovieDetailDestination?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(movieItem.id) {
        isLoading = true
        destination = navigationManager.getMovieDetailDestination(movieItem)
        isLoading = false
    }

    when {
        isLoading -> {
        }
        destination != null -> {
            when (destination) {
                is MovieDetailDestination.Horror -> {
                    HorrorMovieDetailScreen(movieItem = movieItem, movieAPI = movieAPI)
                }
                is MovieDetailDestination.WithHeart -> {
                    MovieDetailScreenWithHeartAndBreak(movieItem = movieItem, movieAPI = movieAPI, viewModel)
                }
                null -> {
                    MovieDetailScreenWithHeartAndBreak(movieItem = movieItem, movieAPI = movieAPI,viewModel)
                }
            }
        }
    }
}