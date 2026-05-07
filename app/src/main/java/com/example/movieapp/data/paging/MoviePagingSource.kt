package com.example.movieapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieapp.data.remote.api.MovieApiService
import com.example.movieapp.domain.model.Movie
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE = 1

private fun pagingSourceFrom(
    load: suspend (page: Int) -> Pair<List<Movie>, Int>
) = object : PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: STARTING_PAGE
        return try {
            val (movies, totalPages) = load(page)
            LoadResult.Page(
                data = movies,
                prevKey = if (page == STARTING_PAGE) null else page - 1,
                nextKey = if (page >= totalPages) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}

fun PopularMoviesPagingSource(api: MovieApiService, language: String) = pagingSourceFrom { page ->
    val r = api.getPopularMovies(page = page, language = language)
    r.results.map { it.toDomain() } to r.totalPages
}

fun NowPlayingPagingSource(api: MovieApiService, language: String) = pagingSourceFrom { page ->
    val r = api.getNowPlayingMovies(page = page, language = language)
    r.results.map { it.toDomain() } to r.totalPages
}

fun TopRatedPagingSource(api: MovieApiService, language: String) = pagingSourceFrom { page ->
    val r = api.getTopRatedMovies(page = page, language = language)
    r.results.map { it.toDomain() } to r.totalPages
}

fun UpcomingPagingSource(api: MovieApiService, language: String) = pagingSourceFrom { page ->
    val r = api.getUpcomingMovies(page = page, language = language)
    r.results.map { it.toDomain() } to r.totalPages
}

class SearchMoviesPagingSource(
    private val api: MovieApiService,
    private val query: String,
    private val language: String
) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: STARTING_PAGE
        return try {
            val response = api.searchMovies(query = query, page = page, language = language)
            LoadResult.Page(
                data = response.results.map { it.toDomain() },
                prevKey = if (page == STARTING_PAGE) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}