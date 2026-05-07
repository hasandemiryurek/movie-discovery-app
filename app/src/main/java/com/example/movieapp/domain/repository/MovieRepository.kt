package com.example.movieapp.domain.repository

import androidx.paging.PagingData
import com.example.movieapp.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getNowPlayingMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getTopRatedMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getUpcomingMovies(page: Int = 1): Result<List<Movie>>
    fun getPopularMoviesPaged(): Flow<PagingData<Movie>>
    fun getNowPlayingMoviesPaged(): Flow<PagingData<Movie>>
    fun getTopRatedMoviesPaged(): Flow<PagingData<Movie>>
    fun getUpcomingMoviesPaged(): Flow<PagingData<Movie>>
    fun searchMovies(query: String): Flow<PagingData<Movie>>
    suspend fun getMovieDetail(movieId: Int): Result<Movie>
}