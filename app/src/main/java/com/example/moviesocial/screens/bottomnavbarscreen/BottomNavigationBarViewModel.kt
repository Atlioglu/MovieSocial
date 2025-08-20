package com.example.moviesocial.screens.bottomnavbarscreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesocial.model.NavigationItem
import com.example.moviesocial.screens.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BottomNavigationBarViewModel : ViewModel() {

    // Navigation items
    val navigationItems = listOf(
        NavigationItem(
            title = "Home",
            icon = Icons.Default.Home,
            route = Screen.Home.rout
        ),
        NavigationItem(
            title = "Search",
            icon = Icons.Default.Search,
            route = Screen.Search.rout
        ),
        NavigationItem(
            title = "Fav",
            icon = Icons.Default.Favorite,
            route = Screen.Fav.rout
        ),
        NavigationItem(
            title = "Assistant",
            icon = Icons.Default.Face,
            route = Screen.Assistant.rout
        ),
        NavigationItem(
            title = "Settings",
            icon = Icons.Default.Settings,
            route = Screen.Setting.rout
        )
    )

    var selectedIndex = mutableIntStateOf(0)
        private set

    init {
        viewModelScope.launch {
            delay(2000)
        }
    }

    fun onNavigationItemSelected(index: Int) {
        selectedIndex.intValue = index
    }
}