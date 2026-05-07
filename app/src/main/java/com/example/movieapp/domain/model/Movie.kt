package com.example.movieapp.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val genreIds: List<Int> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val runtime: Int? = null
) {
    val posterUrl: String?
        get() = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }

    val backdropUrl: String?
        get() = backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }

    val formattedRating: String
        get() = String.format("%.1f", voteAverage)
}

data class Genre(
    val id: Int,
    val name: String
)