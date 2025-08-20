package com.example.moviesocial.screens.detailsscreen

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import com.example.moviesocial.model.MovieItem
import com.example.moviesocial.screens.animations.BreakingCrack
import com.example.moviesocial.screens.animations.FloatingHeart
import com.example.moviesocial.screens.animations.HeartConfig
import com.example.moviesocial.screens.animations.HeartData
import com.example.moviesocial.screens.animations.LightingState
import com.example.moviesocial.screens.animations.LineConfig
import com.example.moviesocial.screens.animations.LineData
import com.example.moviesocial.screens.favscreen.FavViewModel
import com.example.moviesocial.service.MovieAPI
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random

@Composable
fun MovieDetailScreen(
    movieItem: MovieItem,
    movieAPI: MovieAPI
) {
    val viewModel = koinViewModel<MovieDetailViewModel>()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val uiState by viewModel.uiState

    // Initialize favorite state and load movie details
    LaunchedEffect(movieItem.id) {
        viewModel.initializeFavoriteState(movieItem.title)
        viewModel.loadMovieDetails(movieItem.id, movieAPI)
    }

    // Show error toast if there's an error
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints {
            Surface {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(20.dp, 0.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = 0.dp,
                                        bottomEnd = 24.dp,
                                        bottomStart = 24.dp
                                    )
                                )
                        ) {
                            MovieHeader(movieItem, this@BoxWithConstraints.maxHeight)
                        }
                        MovieAdditionalDetails(movieItem)
                        MovieDetails(movieItem)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            FABAddFav(
                                modifier = Modifier,
                                movieItem = movieItem,
                                viewModel = viewModel
                            )
                        }
                    }

                }
            }
        }
    }
}




@Composable
fun MovieDetailScreenWithHeartAndBreak(
    movieItem: MovieItem,
    movieAPI: MovieAPI,
    viewModel: MovieDetailViewModel
) {

    // Heart animation states
    val heartList = remember { mutableStateListOf<HeartData>() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val heartConfig = HeartConfig(
        radiusMultiplier = 1f,
        delayDuration = 300L
    )

    // Break glass animation states
    val crackList = remember { mutableStateListOf<LineData>() }
    val crackConfig = LineConfig(
        radiusMultiplier = 1f,
        delayDuration = 50L,
        maxRadius = 700f,
        minLength = 50f,
        maxThickness = 7f
    )

    val context = LocalContext.current
    val uiState by viewModel.uiState
    val isRomance by viewModel.isRomance
    val isThriller by viewModel.isThriller
    val isGlass by viewModel.isGlass

    // Initialize states and load data
    LaunchedEffect(movieItem.id) {
        viewModel.initializeFavoriteState( movieItem.title)
        viewModel.loadMovieDetails(movieItem.id, movieAPI)
    }

    // Show error toast if there's an error
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Press) {
                            val touchPosition = event.changes.first().position

                            if (isRomance) {
                                // Trigger heart animation at touch position
                                repeat(5) { index ->
                                    scope.launch {
                                        kotlinx.coroutines.delay(index * heartConfig.delayDuration)
                                        heartList.add(
                                            HeartData(
                                                id = index + Random.nextInt(1000),
                                                startPosition = touchPosition
                                            )
                                        )
                                    }
                                }
                            } else if (isThriller && isGlass) {
                                // Trigger break glass animation at touch position
                                viewModel.breakGlass()
                                repeat(10) { index ->
                                    scope.launch {
                                        kotlinx.coroutines.delay(index * crackConfig.delayDuration)
                                        crackList.add(
                                            LineData(
                                                id = index + Random.nextInt(1000),
                                                startPosition = touchPosition
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .then(
                            if (isGlass && isThriller) Modifier.blur(8.dp)
                            else if (isThriller) Modifier.blur(1.5.dp)
                            else Modifier
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .padding(20.dp, 0.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 0.dp,
                                    bottomEnd = 24.dp,
                                    bottomStart = 24.dp
                                )
                            )
                    ) {
                        MovieHeader(movieItem, this@BoxWithConstraints.maxHeight)
                    }
                    MovieAdditionalDetails(movieItem)
                    MovieDetails(movieItem)


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        FABAddFav(
                            modifier = Modifier,
                            movieItem = movieItem,
                            viewModel = viewModel
                        )
                    }

                }
            }
            //
        }

        if (isRomance) {
            heartList.forEach { heartData ->
                FloatingHeart(
                    heartData = heartData,
                    config = heartConfig,
                    onAnimationEnd = { heartList.remove(heartData) }
                )
            }
        }

        if (isThriller) {
            crackList.forEach { lineData ->
                BreakingCrack(
                    lineData = lineData,
                    config = crackConfig
                )
            }
        }
    }
}

@Composable
fun MovieHeader(movieItem: MovieItem, containerHeight: Dp) {
    val configuration = LocalConfiguration.current
    val baseUrl = "https://image.tmdb.org/t/p/w500"

    // Check if screen is portrait or landscape
    val fullImageUrl = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        baseUrl + (movieItem.poster_path ?: "")
    } else {
        baseUrl + (movieItem.backdrop_path ?: "")
    }

    val painter = rememberImagePainter(
        data = fullImageUrl,
        builder = {
            error(R.drawable.movie_social)
            crossfade(1000)
        }
    )

    Image(
        modifier = Modifier
            .heightIn(max = containerHeight * 2 / 3)
            .fillMaxWidth(),
        painter = painter,
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}

@Composable
fun MovieDetails(
    movieItem: MovieItem,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)  // Give some padding inside Card
            ) {
                Text(
                    movieItem.title,
                    fontFamily = FontFamily.Serif,
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    movieItem.overview,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
fun MovieAdditionalDetails(movieItem: MovieItem) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row {
            Column(modifier = Modifier.padding(4.dp).weight(1f)) {
                SmallDetailText("Average Rating")
                RatingBar(movieItem.vote_average / 2, 5)
            }
            Column(modifier = Modifier.padding(4.dp).weight(1f)) {
                SmallDetailText("Watched By")
                SmallDetailText(movieItem.popularity.toString())
            }
            Column(modifier = Modifier.padding(4.dp).weight(1f)) {
                SmallDetailText("Released Date")
                SmallDetailText(movieItem.release_date)
            }
        }
    }
}

@Composable
fun RatingBar(rating: Double, maxRating: Int = 5) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..maxRating) {
            val isSelected = i <= rating
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (isSelected) Color(0xFFFFC700) else Color.LightGray,
                modifier = Modifier.size(20.dp).fillMaxWidth()
            )
        }
    }
}

@Composable
fun SmallDetailText(textTitle: String, titleTextFontSize: Int = 18) {
    Text(
        textTitle,
        fontSize = titleTextFontSize.sp,
        modifier = Modifier.offset(x = 4.dp).fillMaxWidth(),
        style = TextStyle(fontWeight = FontWeight.Thin),
        textAlign = TextAlign.Center,
    )
}



@Composable
fun FABAddFav(
    modifier: Modifier,
    movieItem: MovieItem,
    viewModel: MovieDetailViewModel
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
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
        )
    }
}

