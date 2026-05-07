package com.example.movieapp

import androidx.paging.PagingData
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.MovieRepository
import com.example.movieapp.ui.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: MovieRepository
    private lateinit var viewModel: HomeViewModel

    private val fakeMovies = listOf(
        Movie(id = 1, title = "Movie 1", overview = "Overview 1",
            posterPath = "/poster1.jpg", backdropPath = null,
            releaseDate = "2024-01-01", voteAverage = 8.5, voteCount = 1000),
        Movie(id = 2, title = "Movie 2", overview = "Overview 2",
            posterPath = "/poster2.jpg", backdropPath = null,
            releaseDate = "2024-02-15", voteAverage = 7.2, voteCount = 500)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        coEvery { repository.getNowPlayingMovies(any()) } returns Result.success(fakeMovies)
        coEvery { repository.getTopRatedMovies(any()) } returns Result.success(fakeMovies)
        coEvery { repository.getUpcomingMovies(any()) } returns Result.success(fakeMovies)
        coEvery { repository.getPopularMovies(any()) } returns Result.success(fakeMovies)
        coEvery { repository.getPopularMoviesPaged() } returns flowOf(PagingData.from(fakeMovies))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads home content successfully`() = runTest {
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(fakeMovies, state.nowPlayingMovies)
        assertEquals(fakeMovies, state.topRatedMovies)
        assertEquals(fakeMovies, state.upcomingMovies)
    }

    @Test
    fun `loadHomeContent sets isLoading true then false`() = runTest {
        viewModel = HomeViewModel(repository)
        assertTrue(viewModel.uiState.value.isLoading)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadHomeContent sets error when nowPlaying fails`() = runTest {
        coEvery { repository.getNowPlayingMovies(any()) } returns
                Result.failure(Exception("Network error"))
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertEquals("Network error", state.error)
        assertEquals(fakeMovies, state.topRatedMovies)
    }

    @Test
    fun `clearError clears error state`() = runTest {
        coEvery { repository.getNowPlayingMovies(any()) } returns
                Result.failure(Exception("error"))
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadHomeContent calls repository methods concurrently`() = runTest {
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()
        coVerify(exactly = 1) { repository.getNowPlayingMovies(any()) }
        coVerify(exactly = 1) { repository.getTopRatedMovies(any()) }
        coVerify(exactly = 1) { repository.getUpcomingMovies(any()) }
    }
}