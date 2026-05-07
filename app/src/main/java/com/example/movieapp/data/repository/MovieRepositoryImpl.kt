package com.example.movieapp.data.repository

import androidx.appcompat.app.AppCompatDelegate
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.movieapp.data.paging.NowPlayingPagingSource
import com.example.movieapp.data.paging.PopularMoviesPagingSource
import com.example.movieapp.data.paging.SearchMoviesPagingSource
import com.example.movieapp.data.paging.TopRatedPagingSource
import com.example.movieapp.data.paging.UpcomingPagingSource
import com.example.movieapp.data.remote.api.MovieApiService
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

private const val PAGE_SIZE = 20

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val api: MovieApiService
) : MovieRepository {

    private fun currentLanguage(): String {
        val lang = AppCompatDelegate.getApplicationLocales().get(0)?.language
        return if (lang == "tr") "tr-TR" else "en-US"
    }

    override suspend fun getNowPlayingMovies(page: Int): Result<List<Movie>> =
        runCatching { api.getNowPlayingMovies(page, currentLanguage()).results.map { it.toDomain() } }

    override suspend fun getPopularMovies(page: Int): Result<List<Movie>> =
        runCatching { api.getPopularMovies(page, currentLanguage()).results.map { it.toDomain() } }

    override suspend fun getTopRatedMovies(page: Int): Result<List<Movie>> =
        runCatching { api.getTopRatedMovies(page, currentLanguage()).results.map { it.toDomain() } }

    override suspend fun getUpcomingMovies(page: Int): Result<List<Movie>> =
        runCatching { api.getUpcomingMovies(page, currentLanguage()).results.map { it.toDomain() } }

    override suspend fun getMovieDetail(movieId: Int): Result<Movie> =
        runCatching { api.getMovieDetail(movieId, currentLanguage()).toDomain() }

    private fun pagedFlow(source: () -> PagingSource<Int, Movie>): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false, prefetchDistance = 3),
        pagingSourceFactory = source
    ).flow

    override fun getPopularMoviesPaged() = pagedFlow { PopularMoviesPagingSource(api, currentLanguage()) }
    override fun getNowPlayingMoviesPaged() = pagedFlow { NowPlayingPagingSource(api, currentLanguage()) }
    override fun getTopRatedMoviesPaged() = pagedFlow { TopRatedPagingSource(api, currentLanguage()) }
    override fun getUpcomingMoviesPaged() = pagedFlow { UpcomingPagingSource(api, currentLanguage()) }

    override fun searchMovies(query: String): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { SearchMoviesPagingSource(api, query, currentLanguage()) }
    ).flow
}