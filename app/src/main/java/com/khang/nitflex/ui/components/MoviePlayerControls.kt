package com.khang.nitflex.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun MoviePlayerControls(
    player: ExoPlayer?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = {
                player?.let {
                    if (it.isPlaying) it.pause() else it.play()
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = if (player?.isPlaying == true) "Pause" else "Play"
            )
        }
    }
} 