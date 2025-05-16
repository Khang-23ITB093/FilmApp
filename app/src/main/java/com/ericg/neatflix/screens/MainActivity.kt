package com.ericg.neatflix.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.ericg.neatflix.ui.theme.NeatFlixTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NeatFlixTheme {
                val navController = rememberNavController()
                MyAppNavGraph(navController = navController)
            }
        }
    }
}
