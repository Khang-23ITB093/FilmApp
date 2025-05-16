package com.ericg.neatflix.screens

import android.annotation.SuppressLint
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Light
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.ericg.neatflix.R
import com.ericg.neatflix.data.local.MyListMovie
import com.ericg.neatflix.model.Cast
import com.ericg.neatflix.model.Film
import com.ericg.neatflix.model.Genre
import com.ericg.neatflix.sharedComposables.MovieGenreChip
import com.ericg.neatflix.ui.theme.AppOnPrimaryColor
import com.ericg.neatflix.ui.theme.ButtonColor
import com.ericg.neatflix.util.Constants.BASE_BACKDROP_IMAGE_URL
import com.ericg.neatflix.util.Constants.BASE_POSTER_IMAGE_URL
import com.ericg.neatflix.viewmodel.DetailsViewModel
import com.ericg.neatflix.viewmodel.HomeViewModel
import com.ericg.neatflix.viewmodel.WatchListViewModel
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import android.os.Build.VERSION.SDK_INT
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import java.text.SimpleDateFormat
import java.util.*
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ericg.neatflix.screens.destinations.MoviePlayerDestinationHelper
import com.ericg.neatflix.screens.destinations.MoviePlayerScreenDestination

@RootNavGraph
@Destination
@Composable
fun MovieDetails(
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel = hiltViewModel(),
    detailsViewModel: DetailsViewModel = hiltViewModel(),
    watchListViewModel: WatchListViewModel = hiltViewModel(),
    currentFilm: Film
) {
    var film by remember {
        mutableStateOf(currentFilm)
    }

    val date = SimpleDateFormat.getDateTimeInstance().format(Date())
    val watchListMovie = MyListMovie(
        mediaId = film.id,
        imagePath = film.posterPath,
        title = film.title,
        releaseDate = film.releaseDate,
        rating = film.voteAverage,
        addedOn = date
    )

    val addedToList = watchListViewModel.addedToWatchList.value
    val similarFilms = detailsViewModel.similarMovies.value.collectAsLazyPagingItems()
    val movieCastList = detailsViewModel.movieCast.value
    val filmType = homeViewModel.selectedFilmType.value

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    LaunchedEffect(key1 = film) {
        detailsViewModel.getSimilarFilms(filmId = film.id, filmType)
        detailsViewModel.getFilmCast(filmId = film.id, filmType)
        watchListViewModel.exists(mediaId = film.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF180E36))
    ) {

        // BACKDROP IMAGE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("$BASE_BACKDROP_IMAGE_URL/${film.backdropPath}")
                        .crossfade(true)
                        .build(),
                    contentDescription = "Header backdrop image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.backdrop_not_available),
                )

                // Back button
                IconButton(
                    onClick = { navigator.navigateUp() },
                    modifier = Modifier
                        .padding(12.dp)
                        .size(38.dp)
                        .background(ButtonColor.copy(alpha = 0.78F), CircleShape)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "back button",
                        tint = AppOnPrimaryColor,
                        modifier = Modifier.padding(6.dp)
                    )
                }

                // Play button
                PlayButton(
                    film = film,
                    navigator = navigator,
                    context = context,
                    iconTint = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(56.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0XFF180E36).copy(alpha = 0.5F),
                                    Color(0XFF180E36)
                                )
                            )
                        )
                )
            }

            // POSTER + INFO
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (120).dp)
                    .padding(horizontal = 16.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("$BASE_POSTER_IMAGE_URL/${film.posterPath}")
                        .crossfade(true)
                        .build(),
                    contentDescription = "movie poster",
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .width(110.dp)
                        .height(165.dp)
                        .border(2.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.image_not_available)
                )

                Column(
                    modifier = Modifier
                        .padding(start = 12.dp, top = 12.dp, bottom = 8.dp)
                        .weight(1f)
                ) {
                    var paddingValue by remember { mutableStateOf(2) }
                    Text(
                        text = when (film.mediaType) {
                            "tv" -> {
                                paddingValue = 2
                                "Series"
                            }

                            "movie" -> {
                                paddingValue = 2
                                "Movie"
                            }

                            else -> {
                                paddingValue = 0
                                ""
                            }
                        },
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(size = 4.dp))
                            .background(Color.DarkGray.copy(alpha = 0.5F))
                            .padding(paddingValue.dp),
                        color = AppOnPrimaryColor.copy(alpha = 0.78F),
                        fontSize = 12.sp,
                    )

                    Text(
                        text = film.title,
                        modifier = Modifier
                            .padding(top = 2.dp, start = 4.dp, bottom = 8.dp)
                            .fillMaxWidth(),
                        maxLines = 2,
                        fontSize = 18.sp,
                        fontWeight = Bold,
                        color = Color.White.copy(alpha = 0.78F)
                    )

                    Text(
                        text = film.releaseDate,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                        fontSize = 15.sp,
                        fontWeight = Light,
                        color = Color.White.copy(alpha = 0.56F)
                    )

                    RatingBar(
                        value = (film.voteAverage / 2).toFloat(),
                        style = RatingBarStyle.Fill(),
                        onValueChange = {},
                        onRatingChanged = {},
                        modifier = Modifier.padding(horizontal = 6.dp),
                        numOfStars = 5,
                        size = 16.dp,
                        spaceBetween = 4.dp,
                        isIndicator = true,
                        stepSize = StepSize.HALF,
                        hideInactiveStars = false,
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 0.dp, bottom = 8.dp)
                            .fillMaxWidth(),
                    ) {
                        if (film.adult)
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .border(
                                        1.dp,
                                        color = if (film.adult) Color(0xFFFF6F6F) else Color.White.copy(
                                            alpha = 0.78F
                                        )
                                    )
                                    .background(if (film.adult) Color(0xFFFF6F6F).copy(alpha = 0.14F) else Color.Transparent)
                                    .padding(4.dp)
                            ) {
                                val color: Color
                                Text(
                                    text = "18+",
                                    fontSize = 14.sp,
                                    fontWeight = Normal,
                                    color = Color(0xFFFF6F6F)
                                )
                            }

                        IconButton(onClick = {
                            if (addedToList != 0) {
                                watchListViewModel.removeFromWatchList(watchListMovie.mediaId)
                                Toast.makeText(
                                    context, "Removed from watchlist", LENGTH_SHORT
                                ).show()
                            } else {
                                watchListViewModel.addToWatchList(watchListMovie)
                                Toast.makeText(
                                    context, "Added to watchlist", LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Icon(
                                painter = painterResource(
                                    id = if (addedToList != 0) R.drawable.ic_added_to_list
                                    else R.drawable.ic_add_to_list
                                ),
                                tint = AppOnPrimaryColor,
                                contentDescription = "add to watch list icon"
                            )
                        }
                    }
                }
            }
        }

        LazyRow(
            modifier = Modifier
                .padding(top = (96).dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
                .fillMaxWidth()
        ) {
            val filmGenres: List<Genre> = homeViewModel.filmGenres.filter { genre ->
                return@filter if (film.genreIds.isNullOrEmpty()) false else
                    film.genreIds!!.contains(genre.id)
            }
            filmGenres.forEach { genre ->
                item {
                    MovieGenreChip(
                        background = ButtonColor,
                        textColor = AppOnPrimaryColor,
                        genre = genre.name
                    )
                }
            }
        }

        ExpandableText(
            text = film.overview,
            modifier = Modifier
                .padding(top = 3.dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
                .fillMaxWidth()
        )

        LazyColumn(
            horizontalAlignment = Alignment.Start
        ) {
            item {
                AnimatedVisibility(visible = (movieCastList.isNotEmpty())) {
                    Text(
                        text = "Cast",
                        fontWeight = Bold,
                        fontSize = 18.sp,
                        color = AppOnPrimaryColor,
                        modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 4.dp)
                    )
                }
            }
            item {
                LazyRow(modifier = Modifier.padding(4.dp)) {
                    movieCastList.forEach { cast ->
                        item { CastMember(cast = cast) }
                    }
                }
            }
            item {
                if (similarFilms.itemCount != 0) {
                    Text(
                        text = "Similar",
                        fontWeight = Bold,
                        fontSize = 18.sp,
                        color = AppOnPrimaryColor,
                        modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 4.dp)
                    )
                }
            }

            item {
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(count = similarFilms.itemCount) { index ->
                        val thisMovie = similarFilms[index]
                        thisMovie?.let { movie ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("${BASE_POSTER_IMAGE_URL}/${movie.posterPath}")
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Movie item",
                                modifier = Modifier
                                    .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .size(130.dp, 195.dp)
                                    .clickable {
                                        film = movie
                                    },
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = R.drawable.image_not_available)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CastMember(cast: Cast?) {
    Column(
        modifier = Modifier.padding(end = 8.dp, top = 2.dp, bottom = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("$BASE_POSTER_IMAGE_URL/${cast?.profilePath}")
                .crossfade(true)
                .build(),
            contentDescription = "cast image",
            modifier = Modifier
                .clip(CircleShape)
                .size(70.dp),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.ic_user)
        )
        Text(
            text = trimName(cast?.name ?: "N/A"),
            maxLines = 1,
            color = AppOnPrimaryColor.copy(alpha = 0.5F),
            fontSize = 14.sp,
        )
        Text(
            text = trimName(cast?.department ?: "Unknown"),
            maxLines = 1,
            color = AppOnPrimaryColor.copy(alpha = 0.45F),
            fontSize = 12.sp,
        )
    }
}

fun trimName(name: String): String {
    return if (name.length <= 10) name else {
        name.removeRange(8..name.lastIndex) + "..."
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    minimizedMaxLines: Int = 2,
) {
    var cutText by remember(text) { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    val seeMoreSizeState = remember { mutableStateOf<IntSize?>(null) }
    val seeMoreOffsetState = remember { mutableStateOf<Offset?>(null) }

    // getting raw values for smart cast
    val textLayoutResult = textLayoutResultState.value
    val seeMoreSize = seeMoreSizeState.value
    val seeMoreOffset = seeMoreOffsetState.value

    LaunchedEffect(text, expanded, textLayoutResult, seeMoreSize) {
        val lastLineIndex = minimizedMaxLines - 1
        if (!expanded && textLayoutResult != null && seeMoreSize != null &&
            lastLineIndex + 1 == textLayoutResult.lineCount &&
            textLayoutResult.isLineEllipsized(lastLineIndex)
        ) {
            var lastCharIndex = textLayoutResult.getLineEnd(lastLineIndex, visibleEnd = true) + 1
            var charRect: Rect
            do {
                lastCharIndex -= 1
                charRect = textLayoutResult.getCursorRect(lastCharIndex)
            } while (
                charRect.left > textLayoutResult.size.width - seeMoreSize.width
            )
            seeMoreOffsetState.value = Offset(charRect.left, charRect.bottom - seeMoreSize.height)
            cutText = text.substring(startIndex = 0, endIndex = lastCharIndex)
        }
    }

    Box(modifier) {
        Text(
            color = AppOnPrimaryColor,
            text = cutText ?: text,
            modifier = Modifier
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    if (expanded) {
                        expanded = false
                    }
                },
            maxLines = if (expanded) Int.MAX_VALUE else minimizedMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResultState.value = it },
        )

        if (!expanded) {
            val density = LocalDensity.current
            Text(
                color = Color(0x2DFF978C).copy(alpha = 0.78F),
                text = "... See more",
                fontWeight = Bold,
                fontSize = 14.sp,
                onTextLayout = { seeMoreSizeState.value = it.size },
                modifier = Modifier
                    .then(
                        if (seeMoreOffset != null)
                            Modifier.offset(
                                x = with(density) { seeMoreOffset.x.toDp() },
                                y = with(density) { seeMoreOffset.y.toDp() },
                            )
                        else Modifier
                    )
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        expanded = true
                        cutText = null
                    }
                    .alpha(if (seeMoreOffset != null) 1f else 0f)
                    .verticalScroll(
                        enabled = true,
                        state = rememberScrollState()
                    )
            )
        }
    }
}

@Composable
private fun PlayButton(
    film: Film,
    navigator: DestinationsNavigator,
    context: android.content.Context,
    iconTint: Color = AppOnPrimaryColor,
    modifier: Modifier = Modifier,
    iconSize: Modifier = Modifier.size(32.dp)
) {
    IconButton(
        onClick = {
            MoviePlayerDestinationHelper.fromFilm(film)?.let { args ->
                navigator.navigate(
                    MoviePlayerScreenDestination(
                        movieId = args.movieId,
                        movieTitle = args.movieTitle,
                        moviePoster = args.moviePoster
                    )
                )
            } ?: run {
                Toast.makeText(
                    context,
                    "Cannot play this movie at the moment",
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            tint = iconTint,
            modifier = iconSize
        )
    }
}
