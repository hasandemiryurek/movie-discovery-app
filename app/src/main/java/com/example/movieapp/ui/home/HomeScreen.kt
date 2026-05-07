package com.example.movieapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Language
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.movieapp.ui.theme.Black
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.movieapp.R
import com.example.movieapp.ui.components.ErrorScreen
import com.example.movieapp.ui.components.LoadingScreen
import com.example.movieapp.ui.components.MoviePosterCard
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.platform.LocalConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onShowAll: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()
    val snackbarHostState = remember { SnackbarHostState() }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = (screenWidth - 48.dp)

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    Spacer(modifier = Modifier.size(48.dp))
                },
                actions = {
                    var showLanguageMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showLanguageMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🇹🇷  Türkçe") },
                                onClick = {
                                    showLanguageMenu = false
                                    AppCompatDelegate.setApplicationLocales(
                                        LocaleListCompat.forLanguageTags("tr")
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🇬🇧  English") },
                                onClick = {
                                    showLanguageMenu = false
                                    AppCompatDelegate.setApplicationLocales(
                                        LocaleListCompat.forLanguageTags("en")
                                    )
                                }
                            )
                        }
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isDark) Black else MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.nowPlayingMovies.isEmpty() -> {
                LoadingScreen(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null && uiState.nowPlayingMovies.isEmpty() -> {
                ErrorScreen(
                    message = uiState.error!!,
                    onRetry = viewModel::loadHomeContent,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        SectionHeader(
                            title = stringResource(R.string.section_now_playing),
                            onShowAll = { onShowAll("NOW_PLAYING") }
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.nowPlayingMovies) { movie ->
                                MoviePosterCard(movie = movie, onClick = { onMovieClick(movie.id) })
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        SectionHeader(
                            title = stringResource(R.string.section_popular),
                            onShowAll = { onShowAll("POPULAR") }
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.popularMovies) { movie ->
                                MoviePosterCard(movie = movie, onClick = { onMovieClick(movie.id) })
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        SectionHeader(
                            title = stringResource(R.string.section_top_rated),
                            onShowAll = { onShowAll("TOP_RATED") }
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.topRatedMovies) { movie ->
                                MoviePosterCard(movie = movie, onClick = { onMovieClick(movie.id) })
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        SectionHeader(
                            title = stringResource(R.string.section_upcoming),
                            onShowAll = { onShowAll("UPCOMING") }
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.upcomingMovies) { movie ->
                                MoviePosterCard(movie = movie, onClick = { onMovieClick(movie.id) })
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onShowAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 20.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        TextButton(
            onClick = onShowAll,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.show_all),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.padding(start = 2.dp).size(10.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}