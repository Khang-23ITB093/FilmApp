package com.khang.nitflex.screens.destinations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.khang.nitflex.screens.MoviePlayerScreen
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

fun NavGraphBuilder.moviePlayerScreen(
    navigator: DestinationsNavigator
) {
    composable(
        route = "movie_player/{movieId}/{movieTitle}/{moviePoster}",
        arguments = listOf(
            navArgument("movieId") { type = NavType.IntType },
            navArgument("movieTitle") { type = NavType.StringType },
            navArgument("moviePoster") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
        val movieTitle = backStackEntry.arguments?.getString("movieTitle") ?: ""
        val moviePoster = backStackEntry.arguments?.getString("moviePoster") ?: ""
        
        MoviePlayerScreen(
            movieId = movieId,
            movieTitle = movieTitle,
            moviePoster = moviePoster,
            navigator = navigator
        )
    }
} 