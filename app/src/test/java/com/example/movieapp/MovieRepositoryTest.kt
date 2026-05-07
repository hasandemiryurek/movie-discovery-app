package com.example.movieapp

import com.example.movieapp.data.remote.api.MovieApiService
import com.example.movieapp.data.remote.dto.GenreDto
import com.example.movieapp.data.remote.dto.MovieDetailDto
import com.example.movieapp.data.remote.dto.MovieDto
import com.example.movieapp.data.remote.dto.MovieListResponse
import com.example.movieapp.data.repository.MovieRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

class MovieRepositoryTest {

    private lateinit var api: MovieApiService
    private lateinit var repository: MovieRepositoryImpl

    private val fakeMovieDtos = listOf(
        MovieDto(id = 1, title = "Movie 1", overview = "Overview 1",
            posterPath = "/poster1.jpg", backdropPath = null,
            releaseDate = "2024-01-01", voteAverage = 8.5, voteCount = 1000, genreIds = listOf(28)),
        MovieDto(id = 2, title = "Movie 2", overview = "Overview 2",
            posterPath = "/poster2.jpg", backdropPath = null,
            releaseDate = "2024-02-15", voteAverage = 7.2, voteCount = 500, genreIds = listOf(35))
    )

    private val fakeMovieListResponse = MovieListResponse(
        page = 1, results = fakeMovieDtos, totalPages = 5, totalResults = 100
    )

    private val fakeMovieDetailDto = MovieDetailDto(
        id = 1, title = "Movie 1", overview = "Overview 1",
        posterPath = "/poster1.jpg", backdropPath = null,
        releaseDate = "2024-01-01", voteAverage = 8.5, voteCount = 1000,
        genres = listOf(GenreDto(28, "Action"), GenreDto(12, "Adventure")),
        runtime = 148
    )

    @Before
    fun setUp() {
        api = mockk()
        repository = MovieRepositoryImpl(api)
    }

    @Test
    fun `getNowPlayingMovies returns success with mapped domain models`() = runTest {
        coEvery { api.getNowPlayingMovies(any(), any()) } returns fakeMovieListResponse
        val result = repository.getNowPlayingMovies()
        assertTrue(result.isSuccess)
        val movies = result.getOrNull()!!
        assertEquals(2, movies.size)
        assertEquals(1, movies[0].id)
        assertEquals("Movie 1", movies[0].title)
        assertEquals("https://image.tmdb.org/t/p/original/poster1.jpg", movies[0].posterUrl)
        assertEquals(listOf(28), movies[0].genreIds)
    }

    @Test
    fun `getTopRatedMovies returns success`() = runTest {
        coEvery { api.getTopRatedMovies(any(), any()) } returns fakeMovieListResponse
        val result = repository.getTopRatedMovies()
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()!!.size)
        coVerify(exactly = 1) { api.getTopRatedMovies(page = 1, language = any()) }
    }

    @Test
    fun `getMovieDetail returns success with genres`() = runTest {
        coEvery { api.getMovieDetail(1, any()) } returns fakeMovieDetailDto
        val result = repository.getMovieDetail(1)
        assertTrue(result.isSuccess)
        val movie = result.getOrNull()!!
        assertEquals(1, movie.id)
        assertEquals(2, movie.genres.size)
        assertEquals("Action", movie.genres[0].name)
        assertEquals(148, movie.runtime)
        assertEquals("8.5", movie.formattedRating)
    }

    @Test
    fun `getNowPlayingMovies returns failure on IOException`() = runTest {
        coEvery { api.getNowPlayingMovies(any(), any()) } throws IOException("No internet")
        val result = repository.getNowPlayingMovies()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IOException)
    }

    @Test
    fun `getMovieDetail returns failure on error`() = runTest {
        coEvery { api.getMovieDetail(any(), any()) } throws RuntimeException("Server error")
        val result = repository.getMovieDetail(999)
        assertTrue(result.isFailure)
    }

    @Test
    fun `movie posterUrl is null when posterPath is null`() = runTest {
        val dtoWithNullPoster = fakeMovieDtos[0].copy(posterPath = null)
        val movie = dtoWithNullPoster.toDomain()
        assertEquals(null, movie.posterUrl)
    }

    @Test
    fun `formattedRating formats to one decimal place`() = runTest {
        coEvery { api.getNowPlayingMovies(any(), any()) } returns fakeMovieListResponse
        val result = repository.getNowPlayingMovies()
        val movie = result.getOrNull()!!.first()
        assertEquals("8.5", movie.formattedRating)
    }
}