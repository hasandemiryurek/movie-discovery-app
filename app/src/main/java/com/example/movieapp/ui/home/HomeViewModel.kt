package com.example.movieapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val nowPlayingMovies: List<Movie> = emptyList(),
    val popularMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val upcomingMovies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeContent()
    }

    fun loadHomeContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val nowPlayingDeferred = async { repository.getNowPlayingMovies() }
            val popularDeferred   = async { repository.getPopularMovies() }
            val topRatedDeferred  = async { repository.getTopRatedMovies() }
            val upcomingDeferred  = async { repository.getUpcomingMovies() }

            val npResult  = nowPlayingDeferred.await()
            val popResult = popularDeferred.await()
            val trResult  = topRatedDeferred.await()
            val ucResult  = upcomingDeferred.await()

            val error = listOf(npResult, popResult, trResult, ucResult)
                .firstNotNullOfOrNull { it.exceptionOrNull()?.localizedMessage }

            _uiState.update {
                it.copy(
                    nowPlayingMovies = npResult.getOrDefault(emptyList()),
                    popularMovies    = popResult.getOrDefault(emptyList()),
                    topRatedMovies   = trResult.getOrDefault(emptyList()),
                    upcomingMovies   = ucResult.getOrDefault(emptyList()),
                    isLoading = false,
                    error = error
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}