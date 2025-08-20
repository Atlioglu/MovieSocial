package com.example.moviesocial.screens.detailsscreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.moviesocial.R
import com.example.moviesocial.manager.StorageManager
import com.example.moviesocial.model.MovieItem
import com.example.moviesocial.screens.animations.LightBulb
import com.example.moviesocial.screens.animations.LightingState
import com.example.moviesocial.screens.animations.bulbAttachments
import com.example.moviesocial.service.MovieAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HorrorMovieDetailScreen(movieItem: MovieItem, movieAPI: MovieAPI) {
    val viewModel = koinViewModel<HorrorMovieDetailViewModel>()
   // val scrollState = rememberScrollState()
    val uiState by viewModel.uiState

   // val storageManager = koinInject<StorageManager>()
    val context = LocalContext.current
   // val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(movieItem.id) {
        viewModel.initializeFavoriteState(movieItem.title)
        viewModel.loadMovieDetails(movieItem.id, movieAPI)
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }


    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        BoxWithConstraints {
            Surface {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        BrushSetting(movieItem, this@BoxWithConstraints.maxHeight)
                    }
                }
            }
        }
    }
}

@Composable
fun HorrorMovieHeader(
    movieItem: MovieItem,
    containerHeight: Dp,
    state: LightingState
) {

    var colorMatrix = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    colorMatrix = if (state.isLightOn) {
        floatArrayOf(
            1.2f, 0.1f, -0.05f, 0f, 30f,
            0.05f, 1.15f, -0.05f, 0f, 20f,
            -0.05f, -0.05f, 1.05f, 0f, -10f,
            0f, 0f, 0f, 1f, 0f
        )
    } else {
        floatArrayOf(
            2f, 0f, 0f, 0f, -180f,
            0f, 2f, 0f, 0f, -180f,
            0f, 0f, 2f, 0f, -180f,
            0f, 0f, 0f, 1f, 0f
        )
    }


    val baseUrl = "https://image.tmdb.org/t/p/w500"
    val fullImageUrl = baseUrl + (movieItem.poster_path ?: "")
    val painter = rememberImagePainter(
        data = fullImageUrl,
        builder = {
            error(R.drawable.movie_social)
            crossfade(1000)
        }
    )
    Image(
        modifier = Modifier
            .heightIn(max = containerHeight *2/3)
            .fillMaxWidth(),
        painter = painter,
        contentScale = ContentScale.Crop,
        contentDescription = null,
        colorFilter = ColorFilter.colorMatrix(ColorMatrix(colorMatrix))

    )
}

@Composable
fun HorrorMovieDetails(
    movieItem: MovieItem,
    state: LightingState
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (state.isLightOn) Color(0xFF2A2A2A) else Color(0xFF1A1A1A)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                brush = state.cardBackgroundBrush,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Column {
            Text(
                text = movieItem.title,
                fontFamily = FontFamily.Serif,
                style = LocalTextStyle.current.merge(
                    TextStyle(
                        fontWeight = FontWeight.Thin,
                        fontSize = 18.sp,
                        brush = state.lightSourceBrush
                    )
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(x = 4.dp)
                    .padding(8.dp),
            )
            Text(
                text = movieItem.overview,
                modifier = Modifier
                    .offset(x = 4.dp)
                    .padding(8.dp),
                style = LocalTextStyle.current.merge(
                    TextStyle(
                        fontWeight = FontWeight.Thin,
                        fontSize = 16.sp,
                        brush = state.lightSourceBrush
                    )
                )
            )
        }
    }
}

@Composable
fun HorrorMovieAdditionalDetails(
    movieItem: MovieItem,
    state: LightingState
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (state.isLightOn) Color(0xFF2A2A2A) else Color(0xFF1A1A1A)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                brush = state.cardBackgroundBrush,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row {
            Column(modifier = Modifier.padding(4.dp).weight(1f)) {
                HorrorSmallDetailText("Average Rating", state = state)
                HorrorRatingBar(movieItem.vote_average / 2, 5, state)
            }
            Column(modifier = Modifier.padding(4.dp).weight(1f)) {
                HorrorSmallDetailText("Watched By", state = state)
                HorrorSmallDetailText(movieItem.popularity.toString(), state = state)
            }
            Column(modifier = Modifier.padding(4.dp).weight(1f)) {
                HorrorSmallDetailText("Released Date", state = state)
                HorrorSmallDetailText(movieItem.release_date, state = state)
            }
        }
    }
}

@Composable
fun BrushSetting(movieItem: MovieItem, containerHeight: Dp) {
    val viewModel = koinViewModel<HorrorMovieDetailViewModel>()
    BoxWithConstraints(modifier = Modifier.background(Color.Black)) {
        val size = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        val scrollState = rememberScrollState()
        val lightingState = remember(size) { LightingState(size, scrollState) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Movie Header
            Box(
                modifier = Modifier.padding(20.dp, 0.dp)
                    .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomEnd = 24.dp, bottomStart = 24.dp))
            ) {
                HorrorMovieHeader(movieItem, containerHeight, state = lightingState)
            }

            // Movie Details
            HorrorMovieAdditionalDetails(movieItem, state = lightingState)
            HorrorMovieDetails(movieItem, state = lightingState)

            FABAddFavWithLighting(
                movieItem = movieItem,
                state = lightingState,
                modifier = Modifier
                    .padding(16.dp),
                viewModel = viewModel
                )
        }

        // Cable attachment to bulb
        Box(
            modifier = Modifier
                .matchParentSize()
                .bulbAttachments(lightingState)
        )

        // Light bulb
        LightBulb(state = lightingState)
    }
}



@Composable
fun FABAddFavWithLighting(
    movieItem: MovieItem,
    state: LightingState,
    modifier: Modifier = Modifier,
    viewModel: HorrorMovieDetailViewModel
) {
    val context = LocalContext.current
    val isFavorite by viewModel.isFavorite

    FloatingActionButton(
        onClick = {
            viewModel.toggleFavorite(movieItem)
            val message = if (isFavorite) "Added to favorites" else "Removed from favorites"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        },
        modifier = modifier
            .background(
                brush = state.buttonBackgroundBrush,
                shape = CircleShape
            ),
        containerColor = Color.Transparent
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = if (state.isLightOn) {
                Color.Yellow
            } else {
                Color.DarkGray
            }
        )
    }
}


@Composable
fun HorrorRatingBar(
    rating: Double,
    maxRating: Int = 5,
    state: LightingState
) {
    Row(    verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxRating) {
            val isSelected = i <= rating
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (state.isLightOn && isSelected) {
                    Color.Yellow
                } else {
                    Color.DarkGray
                } ,
                modifier = Modifier
                    .size(20.dp).fillMaxWidth()
            )
        }
    }
}

@Composable
fun HorrorSmallDetailText(
    text: String,
    titleTextFontSize: Int = 14,
    state: LightingState
) {
    Text(
        text = text,
        fontSize = titleTextFontSize.sp,
        modifier = Modifier
            .offset(x = 4.dp)
            .fillMaxWidth(),
        style = LocalTextStyle.current.merge(
            TextStyle(
                fontWeight = FontWeight.Thin,
                fontSize = titleTextFontSize.sp,
                brush = state.lightSourceBrush
            )
        ),
        textAlign = TextAlign.Center,
    )
}