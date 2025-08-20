package com.example.moviesocial.screens.homescreen

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridLayoutInfo
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moviesocial.R
import com.example.moviesocial.model.MovieItem
import com.example.moviesocial.ui.theme.Purple80
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current

    val viewModel = koinViewModel<HomeViewModel>()

    val savedIndex = remember {
        val sharedPref = context.getSharedPreferences("UI_PREFS", Context.MODE_PRIVATE)
        mutableIntStateOf(sharedPref.getInt("selected_ui_index", 0))
    }

    LaunchedEffect(Unit) {
        viewModel.getMovies()
    }

    val commonProps = CommonScreenProps(
        movieList = viewModel.movieList.value,
        isLoading = viewModel.isLoading.value,
        error = viewModel.error.value,
        navController = navController,
        onLoadMore = { viewModel.loadNextPage() }
    )

    if (savedIndex.intValue == 0) {
        HomeGridScreen(commonProps)
    } else {
        HomeListScreen(commonProps)
    }
}


data class CommonScreenProps(
    val movieList: List<MovieItem>,
    val isLoading: Boolean,
    val error: String?,
    val navController: NavController,
    val onLoadMore: () -> Unit
)

@Composable
fun LoadingStateHandler(
    movieList: List<MovieItem>,
    isLoading: Boolean,
    error: String?,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(isNetworkAvailable(context)) }

    LaunchedEffect(Unit) {
        while (true) {
            isConnected = isNetworkAvailable(context)
            delay(5000)
        }
    }

    when {
        !isConnected -> {
            CenteredBox {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = "No internet.", modifier = Modifier.size(50.dp))
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "I'm sorry there is no internet Connection",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            }
        }
        isLoading && movieList.isEmpty() -> {
            CenteredBox {
                CircularProgressIndicator()
            }
        }
        error != null && movieList.isEmpty() -> {
            CenteredBox {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
        movieList.isEmpty() -> {
            CenteredBox {
                Text("No movies found")
            }
        }
        else -> {
            content()
        }
    }
}


@Composable
fun CenteredBox(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = content
    )
}


// while pagination, show a loading icon
@Composable
fun LoadMoreIndicator(isLoading: Boolean, movieList: List<MovieItem>) {
    if (isLoading && movieList.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun <T> InfiniteScrollEffect(
    listState: T,
    movieList: List<MovieItem>,
    isLoading: Boolean,
    onLoadMore: () -> Unit,
    getLayoutInfo: (T) -> Any
) {
    LaunchedEffect(listState, movieList.size, isLoading) {
        snapshotFlow {
            val layoutInfo = getLayoutInfo(listState)
            val totalItemsCount = when (layoutInfo) {
                is LazyListLayoutInfo -> layoutInfo.totalItemsCount
                is LazyGridLayoutInfo -> layoutInfo.totalItemsCount
                else -> 0
            }
            val lastVisibleItemIndex = when (layoutInfo) {
                is LazyListLayoutInfo -> layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                is LazyGridLayoutInfo -> layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                else -> -1
            }

            val shouldLoadMore = totalItemsCount > 0 &&
                    lastVisibleItemIndex >= 0 &&
                    lastVisibleItemIndex >= totalItemsCount - 3

            shouldLoadMore
        }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore && !isLoading && movieList.isNotEmpty()) {
                    onLoadMore()
                }
            }
    }
}

@Composable
fun HomeListScreen(props: CommonScreenProps) {
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            InfiniteScrollEffect(
                listState = listState,
                movieList = props.movieList,
                isLoading = props.isLoading,
                onLoadMore = props.onLoadMore,
                getLayoutInfo = { it.layoutInfo }
            )

            LoadingStateHandler(
                movieList = props.movieList,
                isLoading = props.isLoading,
                error = props.error
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(props.movieList) { movieItem ->
                        HomeRowScreen(movieItem = movieItem, navController = props.navController)
                    }

                    item {
                        LoadMoreIndicator(props.isLoading, props.movieList)
                    }
                }
            }
        }
        FABtoScratchScreen(
            navController = props.navController,
            modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp)
        )
    }
}
@Composable
fun HomeRowScreen(movieItem: MovieItem, navController: NavController) {
    val fullImageUrl = buildImageUrl(movieItem.backdrop_path)
    val viewModel = koinViewModel<HomeViewModel>()

    var isFavorite by remember { mutableStateOf(viewModel.favoriteUiChange(movieItem)) }

    Card(
        modifier = Modifier
            .wrapContentSize(Alignment.Center)
            .padding(top = 10.dp)
            .then(
                if (isFavorite) Modifier.border(
                    width = 5.dp,
                    color = Color.Green,
                    shape = RoundedCornerShape(8.dp)
                ) else Modifier
            )
            .clickable { navigateToMovieDetail(movieItem, navController) }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            MovieImage(
                imageUrl = fullImageUrl,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = movieItem.title,
                fontSize = 20.sp,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
            if (isFavorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = Color.Red,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(24.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .clickable {
                            viewModel.onIconButtonClicked(movieItem.id)
                            isFavorite = false
                        }
                )
            }
        }
    }
}


@Composable
fun HomeGridScreen(props: CommonScreenProps) {
    var cellDp by rememberSaveable { mutableStateOf(130f) }
    val listState = rememberLazyGridState()

    InfiniteScrollEffect(
        listState = listState,
        movieList = props.movieList,
        isLoading = props.isLoading,
        onLoadMore = props.onLoadMore,
        getLayoutInfo = { it.layoutInfo }
    )

    LoadingStateHandler(
        movieList = props.movieList,
        isLoading = props.isLoading,
        error = props.error
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Column {
                Slider(
                    value = cellDp,
                    onValueChange = { cellDp = it },
                    valueRange = 64f..240f,
                    steps = 10,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(cellDp.dp),
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        top = 16.dp,
                        end = 12.dp,
                        bottom = 16.dp
                    )
                ) {
                    items(props.movieList) { movieItem ->
                        HomeGridRowScreen(
                            movieItem = movieItem,
                            navController = props.navController,
                            paddingValue = 20 - cellDp / 12
                        )
                    }

                    item {
                        LoadMoreIndicator(props.isLoading, props.movieList)
                    }
                }
            }
            FABtoScratchScreen(
                navController = props.navController,
                modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp)
            )
        }
    }
}

@Composable
fun HomeGridRowScreen(
    movieItem: MovieItem,
    navController: NavController,
    paddingValue: Float
) {
    val fullImageUrl = buildImageUrl(movieItem.poster_path)

    val viewModel = koinViewModel<HomeViewModel>()

    var isFavorite by remember { mutableStateOf(viewModel.favoriteUiChange(movieItem)) }

    Card(
        modifier = Modifier
            .wrapContentSize(Alignment.Center)
            .padding(paddingValue.dp)
            .then(
                if (isFavorite) Modifier.border(
                    width = 5.dp,
                    color = Color.Green,
                    shape = RoundedCornerShape(8.dp)
                ) else Modifier
            )
            .clickable { navigateToMovieDetail(movieItem, navController) }
    ) {
        Box {
            MovieImage(
                imageUrl = fullImageUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth()
            )

            if (isFavorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = Color.Red,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(24.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .clickable {
                            viewModel.onIconButtonClicked(movieItem.id)
                            isFavorite = false
                        }
                )
            }

        }

    }
}

// create image
@Composable
fun MovieImage(imageUrl: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .error(R.drawable.movie_social)
            .build(),
        contentDescription = "Movie image",
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}


@Composable
fun FABtoScratchScreen(navController: NavController, modifier: Modifier) {
    FloatingActionButton(
        onClick = { navController.navigate("scratch_screen") },
        modifier = modifier ,
        containerColor = Color(0xFFFFBD59)

    ) {
        Icon(painter = painterResource(R.drawable.baseline_help_outline_24)
        ,contentDescription = "Floating action button.")
    }
}


//helper funcs
private fun buildImageUrl(path: String?): String {
    val baseUrl = "https://image.tmdb.org/t/p/w500"
    return baseUrl + (path ?: "")
}

private fun navigateToMovieDetail(movieItem: MovieItem, navController: NavController) {
    val movieJson = Uri.encode(Gson().toJson(movieItem))
    navController.navigate("movie_detail_screen/$movieJson")
}



//check internter
private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        @Suppress("DEPRECATION")
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        networkInfo.isConnected
    }
}