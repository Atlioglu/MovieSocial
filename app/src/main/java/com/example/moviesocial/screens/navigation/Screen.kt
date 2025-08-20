package com.example.moviesocial.screens.navigation


// sealed class is better than enum here, for adding parameters
sealed class Screen(val rout: String) {
    object Splash: Screen("splash")
    object Home: Screen("home_list_screen")
    object Search: Screen("search_screen/")
    object Fav: Screen("fav_screen")
    object Assistant: Screen("assistant_screen")
    object Setting: Screen("setting_screen")
    object Scratch: Screen("scratch_screen")
    object QrScreen: Screen("qr_screen")
}