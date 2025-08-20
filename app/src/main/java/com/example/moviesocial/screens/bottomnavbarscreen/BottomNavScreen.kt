package com.example.moviesocial.screens.bottomnavbarscreen

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel


@Composable
fun BottomNavScreen(
    navController: NavController) {
    val viewModel = koinViewModel<BottomNavigationBarViewModel>()

    NavigationBar(containerColor = Color.White) {
        viewModel.navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = viewModel.selectedIndex.intValue == index,
                onClick = {
                    viewModel.onNavigationItemSelected(index)
                    navController.navigate(item.route)
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = {
                    Text(
                        item.title,
                        color = if (index == viewModel.selectedIndex.intValue)
                            Color.Black
                        else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}