package com.example.moviesocial.screens.searchscreen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moviesocial.screens.components.SmallMovieRow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController : NavController,
    searchString: String = "")
{

    val searchViewModel = koinViewModel<SearchViewModel>(parameters = { parametersOf(searchString) })
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val searchResults by searchViewModel.searchResults
    val isLoading by searchViewModel.isLoading
    val error by searchViewModel.error
    val searchHistory by searchViewModel.searchHistory

    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        query = text,
        onQueryChange = { newText ->
            text = newText
            if (newText.length >= 3) {
                searchViewModel.searchMovies(newText)
            } else if (newText.isEmpty()) {
                searchViewModel.clearSearchResults()
            }
        },
        onSearch = { query ->
            if (query.isNotBlank()) {
                searchViewModel.searchMovies(query)
                active = false
            }
        },
        active = active,
        onActiveChange = { isActive ->
            active = isActive
            if (!isActive) {
                searchViewModel.clearSearchResults()
            }
        },
        placeholder = {
            Text(text = "Search movies...")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (active) {
                Icon(
                    modifier = Modifier.clickable {
                        if (text.isNotEmpty()) {
                            text = ""
                            searchViewModel.clearSearchResults()
                        } else {
                            active = false
                        }
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        }
    ) {
        if (active && searchResults.isEmpty() && !isLoading && error == null) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                item {
                    if (searchHistory.isNotEmpty()) {
                        Text(
                            text = "Recent Searches",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(16.dp, 8.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                items(searchHistory) { historyItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                text = historyItem
                                searchViewModel.searchMovies(historyItem)
                                active = false
                            }
                            .padding(16.dp)
                    ) {
                        Icon(
                            modifier = Modifier.padding(end = 10.dp),
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Past search"
                        )
                        Text(text = historyItem)
                    }
                }
            }
        }
    }

    if (!active) {
        Column(
            modifier = Modifier.padding(top = 90.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "error",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                searchResults.isNotEmpty() -> {
                    LazyColumn {
                        item {
                        }

                        items(searchResults) { movie ->
                            SmallMovieRow(
                                movieItem = movie,
                                navController,
                                textButton = {
                                }
                            )
                        }
                    }
                }
                text.isNotEmpty() && searchResults.isEmpty() && !isLoading && error == null -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No movies found for \"$text\"",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


