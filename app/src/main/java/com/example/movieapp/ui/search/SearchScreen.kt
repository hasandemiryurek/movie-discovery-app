package com.example.movieapp.ui.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.movieapp.R
import com.example.movieapp.ui.components.EmptyScreen
import com.example.movieapp.ui.components.ErrorScreen
import com.example.movieapp.ui.components.LoadingScreen
import com.example.movieapp.ui.components.MovieListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.search),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                SearchBar(
                    query = query,
                    onQueryChanged = viewModel::onQueryChanged,
                    onClear = viewModel::clearQuery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            when {
                query.length < 2 -> {
                    item {
                        EmptyScreen(
                            message = stringResource(R.string.search_hint),
                            modifier = Modifier.height(300.dp)
                        )
                    }
                }
                searchResults.loadState.refresh is LoadState.Loading -> {
                    item { LoadingScreen(modifier = Modifier.height(300.dp)) }
                }
                searchResults.loadState.refresh is LoadState.Error -> {
                    val e = searchResults.loadState.refresh as LoadState.Error
                    item {
                        ErrorScreen(
                            message = e.error.localizedMessage ?: "Error",
                            onRetry = { searchResults.retry() },
                            modifier = Modifier.height(300.dp)
                        )
                    }
                }
                searchResults.itemCount == 0 && searchResults.loadState.refresh is LoadState.NotLoading -> {
                    item {
                        EmptyScreen(
                            message = stringResource(R.string.no_results, query),
                            modifier = Modifier.height(300.dp)
                        )
                    }
                }
                else -> {
                    items(
                        count = searchResults.itemCount,
                        key = searchResults.itemKey { it.id }
                    ) { index ->
                        searchResults[index]?.let { movie ->
                            MovieListItem(
                                movie = movie,
                                onClick = { onMovieClick(movie.id) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }

                    when (searchResults.loadState.append) {
                        is LoadState.Loading -> item { LoadingScreen(modifier = Modifier.height(80.dp)) }
                        is LoadState.Error -> {
                            val e = searchResults.loadState.append as LoadState.Error
                            item {
                                ErrorScreen(
                                    message = e.error.localizedMessage ?: "Error",
                                    onRetry = { searchResults.retry() },
                                    modifier = Modifier.height(200.dp)
                                )
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}