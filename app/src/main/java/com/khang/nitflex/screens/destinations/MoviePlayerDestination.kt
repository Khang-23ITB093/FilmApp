package com.khang.nitflex.screens.destinations

import androidx.compose.runtime.Composable
import com.khang.nitflex.model.Film
import com.khang.nitflex.screens.MoviePlayerScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

// Helper object for creating navigation arguments
object MoviePlayerDestinationHelper {
    fun fromFilm(film: Film): MoviePlayerDestinationArgs? {
        // Validate required fields
        if (film.id == 0 || film.title.isBlank()) {
            return null
        }
        
        return MoviePlayerDestinationArgs(
            movieId = film.id,
            movieTitle = film.title,
            moviePoster = film.posterPath ?: ""
        )
    }
}

// Data class for navigation arguments
data class MoviePlayerDestinationArgs(
    val movieId: Int,
    val movieTitle: String,
    val moviePoster: String
)

// This is the actual destination composable
@Destination
@Composable
fun MoviePlayerScreenDestination(
    movieId: Int,
    movieTitle: String,
    moviePoster: String,
    navigator: DestinationsNavigator
) {
    MoviePlayerScreen(
        movieId = movieId,
        movieTitle = movieTitle,
        moviePoster = moviePoster,
        navigator = navigator
    )
} 