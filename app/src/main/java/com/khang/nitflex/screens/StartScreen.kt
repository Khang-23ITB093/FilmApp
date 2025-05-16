package com.khang.nitflex.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.khang.nitflex.R
import com.khang.nitflex.screens.destinations.HomeDestination
import com.khang.nitflex.screens.destinations.LogInScreenDestination
import com.khang.nitflex.screens.destinations.StartScreenDestination
import com.khang.nitflex.sharedComposables.LottieLoader
import com.khang.nitflex.ui.theme.AppPrimaryColor
import com.khang.nitflex.viewmodel.AuthViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay

@RootNavGraph(start = true)
@Destination
@Composable
fun StartScreen(
    navigator: DestinationsNavigator,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var animateLogo by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(AppPrimaryColor)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            LottieLoader(
                modifier = Modifier.size(270.dp),
                lottieFile = R.raw.bubble
            )

            this@Column.AnimatedVisibility(
                visible = !animateLogo, 
                exit = fadeOut(animationSpec = tween(durationMillis = 1000)) +
                       scaleOut(animationSpec = tween(durationMillis = 1000)),
            ) {
                Image(
                    modifier = Modifier
                        .widthIn(max = 170.dp)
                        .alpha(0.78F),
                    painter = painterResource(id = R.drawable.neatflix_logo_large),
                    contentDescription = "Neatflix Logo"
                )
            }
        }
    }

    LaunchedEffect(Unit) { 
        delay(1500) // Time before logo starts to animate out
        animateLogo = true
    }

    LaunchedEffect(Unit) {
        // Total desired splash time before navigation
        // This should be long enough for logo animation to mostly complete
        // e.g., 1500ms (logo visible) + 1000ms (animation out) = 2500ms
        delay(2500) 

        val currentToken = authViewModel.authToken.value 
                                                    
        if (currentToken != null && currentToken.isNotEmpty()) {
            navigator.navigate(HomeDestination) {
                popUpTo(StartScreenDestination.route) { inclusive = true }
            }
        } else {
            navigator.navigate(LogInScreenDestination) {
                popUpTo(StartScreenDestination.route) { inclusive = true }
            }
        }
    }
} 