package com.example.movieapp.ui.movielist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val category: String = checkNotNull(savedStateHandle["category"])


    val movies: Flow<PagingData<Movie>> = when (category) {
        "NOW_PLAYING" -> repository.getNowPlayingMoviesPaged()
        "POPULAR"     -> repository.getPopularMoviesPaged()
        "TOP_RATED"   -> repository.getTopRatedMoviesPaged()
        else          -> repository.getUpcomingMoviesPaged()
    }.cachedIn(viewModelScope)
}