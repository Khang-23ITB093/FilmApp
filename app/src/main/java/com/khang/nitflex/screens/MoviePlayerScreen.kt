package com.khang.nitflex.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.khang.nitflex.ui.theme.AppOnPrimaryColor
import com.khang.nitflex.ui.theme.AppPrimaryColor
import com.khang.nitflex.ui.components.MoviePlayerControls
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.khang.nitflex.sharedComposables.BackButton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun MoviePlayerScreen(
    movieId: Int,
    movieTitle: String,
    moviePoster: String,
    navigator: DestinationsNavigator
) {
    var rating by remember { mutableStateOf(0f) }
    var player by remember { mutableStateOf<ExoPlayer?>(null) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var commentText by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf(listOf<String>()) }
    
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Hide system bars for immersive experience
    DisposableEffect(Unit) {
        systemUiController.isSystemBarsVisible = false
        onDispose { systemUiController.isSystemBarsVisible = true }
    }

    // ExoPlayer setup
    DisposableEffect(Unit) {
        try {
            player = ExoPlayer.Builder(context).build().apply {
                // Using a sample video URL for demonstration
                val videoUrl = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
                
                setMediaItem(MediaItem.fromUri(videoUrl))
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        isError = true
                        errorMessage = "Error playing video: ${error.message}"
                    }
                })
                prepare()
            }
        } catch (e: Exception) {
            isError = true
            errorMessage = "Failed to initialize player: ${e.message}"
        }
        
        onDispose {
            player?.release()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> player?.pause()
                Lifecycle.Event.ON_RESUME -> player?.play()
                Lifecycle.Event.ON_DESTROY -> player?.release()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player?.release()
        }
    }

    BackHandler { navigator.navigateUp() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = movieTitle,
                        color = Color.White, // Set text color to white
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    BackButton {
                        navigator.navigateUp()
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF180E36), // Set background color
                    titleContentColor = Color.White // Set title text color
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppPrimaryColor)
                .padding(paddingValues)
        ) {
            if (isError) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navigator.navigateUp() }) {
                            Text("Go Back")
                        }
                    }
                }
            } else {
                // Video Player
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f)
                        .aspectRatio(16f / 9f)
                ) {
                    player?.let { exoPlayer ->
                        AndroidView(
                            factory = { context ->
                                PlayerView(context).apply {
                                    this.player = exoPlayer
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    MoviePlayerControls(
                        player = player,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Movie Info, Rating and Comments
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f)
                        .padding(16.dp)
                ) {
                    item {
                        Text(
                            text = movieTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            color = AppOnPrimaryColor
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Rating Section
                        Text(
                            text = "Rate this movie",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppOnPrimaryColor
                        )
                        
                        RatingBar(
                            value = rating,
                            numOfStars = 5,
                            style = RatingBarStyle.Fill(),
                            onValueChange = { rating = it },
                            onRatingChanged = { 
                                Toast.makeText(context, "Rating submitted: $it", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        Text(
                            text = "Your Rating: ${rating.toInt()}/5",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppOnPrimaryColor.copy(alpha = 0.7f)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Comments Section
                        Text(
                            text = "Comments",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppOnPrimaryColor
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Comment Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = commentText,
                                onValueChange = { commentText = it },
                                placeholder = { Text("Add a comment...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))

//                                ,colors = TextFieldDefaults.colors(
//                                    textColor = AppOnPrimaryColor,
//                                    cursorColor = AppOnPrimaryColor,
//                                    focusedIndicatorColor = Color.Transparent,
//                                    unfocusedIndicatorColor = Color.Transparent
//                                )
                            )
                            
                            IconButton(
                                onClick = {
                                    if (commentText.isNotBlank()) {
                                        comments = comments + commentText
                                        commentText = ""
                                        Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = "Send comment",
                                    tint = AppOnPrimaryColor
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Comments List
                        this@LazyColumn.items(comments) { comment ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.1f)
                                )
                            ) {
                                Text(
                                    text = comment,
                                    modifier = Modifier.padding(12.dp),
                                    color = AppOnPrimaryColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 