package com.khang.nitflex.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.khang.nitflex.ui.theme.NitFlexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NitFlexTheme {
                val navController = rememberNavController()
                MyAppNavGraph(navController = navController)
            }
        }
    }
}
