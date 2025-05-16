package com.ericg.neatflix.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.DestinationsNavHost

@Composable
fun MyAppNavGraph(
    navController: NavHostController
) {
    DestinationsNavHost(
        navGraph = NavGraphs.root,
        navController = navController
    )
}