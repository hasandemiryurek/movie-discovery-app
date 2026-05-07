package com.example.movieapp.ui.movielist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.ui.res.stringResource
import com.example.movieapp.R
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.movieapp.ui.theme.Black
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.movieapp.ui.components.ErrorScreen
import com.example.movieapp.ui.components.LoadingScreen
import com.example.movieapp.ui.components.MovieListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()
    val isDark = isSystemInDarkTheme()

    val screenTitle = when (viewModel.category) {
        "NOW_PLAYING" -> stringResource(R.string.section_now_playing)
        "TOP_RATED"   -> stringResource(R.string.section_top_rated)
        "UPCOMING"    -> stringResource(R.string.section_upcoming)
        else          -> stringResource(R.string.section_popular)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Black else MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary

                )
            )
        }
    ) { paddingValues ->
        when (movies.loadState.refresh) {
            is LoadState.Loading -> LoadingScreen(modifier = Modifier.padding(paddingValues))
            is LoadState.Error -> {
                val e = movies.loadState.refresh as LoadState.Error
                ErrorScreen(
                    message = e.error.localizedMessage ?: "Error",
                    onRetry = { movies.retry() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        count = movies.itemCount,
                        key = movies.itemKey { it.id }
                    ) { index ->
                        movies[index]?.let { movie ->
                            MovieListItem(
                                movie = movie,
                                onClick = { onMovieClick(movie.id) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }

                    when (movies.loadState.append) {
                        is LoadState.Loading -> item { LoadingScreen(modifier = Modifier.height(80.dp)) }
                        is LoadState.Error -> {
                            val e = movies.loadState.append as LoadState.Error
                            item {
                                ErrorScreen(
                                    message = e.error.localizedMessage ?: "Error",
                                    onRetry = { movies.retry() },
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