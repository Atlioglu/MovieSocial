package com.example.moviesocial

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import com.example.moviesocial.manager.MovieDetailRouter
import com.example.moviesocial.model.MovieItem
import com.example.moviesocial.screens.QrScreen
import com.example.moviesocial.screens.assistantscreen.AssistantScreen
import com.example.moviesocial.screens.bottomnavbarscreen.BottomNavScreen
import com.example.moviesocial.screens.favscreen.FavScreen
import com.example.moviesocial.screens.homescreen.HomeScreen
import com.example.moviesocial.screens.scratchscreen.ScratchLatestMovieScreen
import com.example.moviesocial.screens.navigation.Screen
import com.example.moviesocial.screens.searchscreen.SearchScreen
import com.example.moviesocial.screens.settingsscreen.SettingScreen
import com.example.moviesocial.screens.SplashScreen
import com.example.moviesocial.service.MovieAPI
import com.example.moviesocial.ui.theme.MovieSocialTheme
import com.example.moviesocial.screens.searchscreen.SearchViewModel
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val BASE_URL = "https://api.themoviedb.org/3/"

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieAPI::class.java)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            MovieSocialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                }
                Scaffold(modifier = Modifier
                    .fillMaxSize(),
                    bottomBar = { BottomNavScreen(navController) }) { innerPadding ->

                    Box(modifier = Modifier.padding()) {
                        val graph =
                            navController.createGraph(startDestination = Screen.Splash.rout) {
                                composable(route = Screen.Splash.rout) {
                                    SplashScreen(navController)
                                }
                                composable(
                                    "search_screen/{searchString}",
                                    arguments = listOf(
                                        navArgument("searchString") {
                                            type = NavType.StringType
                                        }
                                    )
                                ) {
                                    val searchString = remember {
                                        it.arguments?.getString("searchString")
                                    }
                                    SearchScreen(navController = navController, searchString.orEmpty())
                                }
                                composable(route = Screen.Fav.rout) {
                                    FavScreen(navController = navController)
                                }
                                composable(route = Screen.Home.rout) {
                                    HomeScreen(navController)
                                }
                                composable(route = Screen.Assistant.rout) {
                                    AssistantScreen(
                                        modifier = Modifier.padding(
                                            bottom = 20.dp,
                                            start = 10.dp,
                                            end = 10.dp
                                        ),
                                        navController = navController
                                    )
                                }
                                composable(route = Screen.Setting.rout) {
                                    SettingScreen(navController)
                                }
                                composable(route = Screen.Scratch.rout) {
                                    ScratchLatestMovieScreen(navController)
                                }
                                composable(
                                    "movie_detail_screen/{movieItem}",
                                    arguments = listOf(
                                        navArgument("movieItem") {
                                            type = NavType.StringType
                                        }
                                    )
                                ) {
                                    val movieString = remember {
                                        it.arguments?.getString("movieItem")
                                    }
                                    val decodedMovieString = Uri.decode(movieString)
                                    val selectedMovie =
                                        Gson().fromJson(decodedMovieString, MovieItem::class.java)
                                    MovieDetailRouter(
                                        movieItem = selectedMovie,
                                        movieAPI = retrofit
                                    )
                                }
                                composable(route = Screen.QrScreen.rout) {
                                    QrScreen()
                                }
                            }
                        NavHost(
                            navController = navController,
                            graph = graph,
                            modifier = Modifier.padding(innerPadding)
                        )

                    }
                }
            }
        }
    }
}
