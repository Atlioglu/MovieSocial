package com.example.moviesocial.screens.favscreen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moviesocial.model.MovieItem
import com.example.moviesocial.screens.components.SmallMovieRow
import com.example.moviesocial.screens.settingsscreen.AnimationSwitch
import com.example.moviesocial.screens.settingsscreen.DropDownFun
import com.example.moviesocial.screens.settingsscreen.SaveButton
import com.example.moviesocial.ui.theme.Purple80
import org.koin.androidx.compose.koinViewModel


@Composable
fun FavScreen(navController: NavController) {
    val viewModel = koinViewModel<FavViewModel>()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadFavoriteMovies()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            uiState.isLoading -> {
                LoadingContent()
            }

            else -> {
                FavoriteMoviesContent(
                    favoriteMovies = uiState.favoriteMovies,
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}


@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun FavoriteMoviesContent(
    favoriteMovies: List<MovieItem>, navController: NavController, viewModel: FavViewModel
) {

    Text(
        text = "Favorite Movies",
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    if(favoriteMovies.isEmpty()){
        FavEmpty()
    }else{


        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(favoriteMovies) { movieItem ->
                SmallMovieRow(movieItem = movieItem, navController = navController, textButton = {
                    FavTextButton(onRemoveClick = {
                        viewModel.onFavButtonClicked(movieItem.id)
                    })
                })
            }
        }
    }

}


@Composable
fun FavEmpty(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                modifier = Modifier.size(60.dp),
                imageVector = Icons.Default.Close,
                contentDescription = "Settings",
                tint = Purple80,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "There is no favorite movie yet",
                fontSize = 22.sp,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FavTextButton(
    onRemoveClick: () -> Unit,
) {
    TextButton(
        onClick = onRemoveClick
    ) {
        Text("Remove")

    }
}