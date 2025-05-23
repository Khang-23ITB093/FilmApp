package com.khang.nitflex.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Light
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.khang.nitflex.R
import com.khang.nitflex.model.Film
import com.khang.nitflex.screens.destinations.MovieDetailsDestination
import com.khang.nitflex.screens.destinations.ProfileDestination
import com.khang.nitflex.screens.destinations.SearchScreenDestination
import com.khang.nitflex.sharedComposables.LoopReverseLottieLoader
import com.khang.nitflex.ui.theme.AppOnPrimaryColor
import com.khang.nitflex.ui.theme.AppPrimaryColor
import com.khang.nitflex.ui.theme.ButtonColor
import com.khang.nitflex.util.Constants.BASE_POSTER_IMAGE_URL
import com.khang.nitflex.util.FilmType
import com.khang.nitflex.util.collectAsStateLifecycleAware
import com.khang.nitflex.viewmodel.HomeViewModel
import com.khang.nitflex.viewmodel.WatchListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

import retrofit2.HttpException
import java.io.IOException

@RootNavGraph
@Destination
@Composable
fun Home(
    navigator: DestinationsNavigator?,
    homeViewModel: HomeViewModel = hiltViewModel(),
    watchListViewModel: WatchListViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF180E36))
    ) {
        ProfileAndSearchBar(navigator!!, homeViewModel)
        NestedScroll(navigator = navigator, homeViewModel, watchListViewModel)
    }
}

@Composable
fun ProfileAndSearchBar(
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel
) {
    Row(
        modifier = Modifier
            .padding(top = 12.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
            .fillMaxWidth()
            .padding(start = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            contentAlignment = Center
        ) {
            Box(
                modifier = Modifier
                    .size(53.dp)
                    .clip(CircleShape)
                // .background(AppOnPrimaryColor)
            )
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(AppPrimaryColor)
            )
            IconButton(onClick = {
                navigator.navigate(
                    direction = ProfileDestination()
                ) {
                    launchSingleTop = true
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person_profile),
                    tint = AppOnPrimaryColor,
                    modifier = Modifier.size(32.dp),
                    contentDescription = "profile picture"
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.neatflix_logo_large),
                contentScale = ContentScale.Fit,
                alpha = 0.78F,
                modifier = Modifier
                    .padding(bottom = 8.dp, top = 4.dp)
                    .widthIn(max = 110.dp),
                contentDescription = "logo"
            )

            val filmTypes = listOf(FilmType.MOVIE, FilmType.TVSHOW)
            val selectedFilmType = homeViewModel.selectedFilmType.value

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                filmTypes.forEachIndexed { index, filmType ->
                    Text(
                        text = if (filmType == FilmType.MOVIE) "Movies" else "Tv Shows",
                        fontWeight = if (selectedFilmType == filmTypes[index]) FontWeight.Bold else Light,
                        fontSize = if (selectedFilmType == filmTypes[index]) 24.sp else 16.sp,
                        color = if (selectedFilmType == filmTypes[index])
                            AppOnPrimaryColor else Color.LightGray.copy(alpha = 0.78F),
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp, top = 8.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (homeViewModel.selectedFilmType.value != filmTypes[index]) {
                                    homeViewModel.selectedFilmType.value = filmTypes[index]
                                    homeViewModel.getFilmGenre()
                                    homeViewModel.refreshAll(null)
                                }
                            }
                    )
                }
            }

            val animOffset = animateDpAsState(
                targetValue = when (filmTypes.indexOf(selectedFilmType)) {
                    0 -> (-35).dp
                    else -> 30.dp
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy
                )
            )

            Box(
                modifier = Modifier
                    .width(46.dp)
                    .height(2.dp)
                    .offset(x = animOffset.value)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AppOnPrimaryColor)
            )
        }

        IconButton(
            onClick = {
                navigator.navigate(
                    direction = SearchScreenDestination()
                ) {
                    launchSingleTop = true
                }
            }
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "search icon",
                tint = AppOnPrimaryColor
            )
        }
    }
}

@Composable
fun NestedScroll(
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel,
    watchListViewModel: WatchListViewModel
) {
    val trendingFilms = homeViewModel.trendingMoviesState.value.collectAsLazyPagingItems()
    val popularFilms = homeViewModel.popularFilmsState.value.collectAsLazyPagingItems()
    val topRatedFilms = homeViewModel.topRatedFilmState.value.collectAsLazyPagingItems()
    val nowPlayingFilms = homeViewModel.nowPlayingMoviesState.value.collectAsLazyPagingItems()
    val upcomingMovies = homeViewModel.upcomingMoviesState.value.collectAsLazyPagingItems()
    val backInTheDays = homeViewModel.backInTheDaysMoviesState.value.collectAsLazyPagingItems()
    val recommendedFilms = homeViewModel.recommendedMovies.value.collectAsLazyPagingItems()
    val myWatchList =
        watchListViewModel.watchList.value.collectAsStateLifecycleAware(initial = emptyList())

    LaunchedEffect(key1 = myWatchList.value.size) {
        if (myWatchList.value.isNotEmpty()) {
            homeViewModel.randomMovieId =
                myWatchList.value[(0..myWatchList.value.lastIndex).random()].mediaId
            if (recommendedFilms.itemCount == 0) {
                homeViewModel.getRecommendedFilms(movieId = homeViewModel.randomMovieId!!)
            }
        }
    }

    val listState: LazyListState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .fillMaxSize()
    ) {
        item {
            Text(
                text = "Genres",
                fontSize = 24.sp,
                color = AppOnPrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(all = 4.dp)
            )
        }
        item {
            val genres = homeViewModel.filmGenres

            LazyRow(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                items(count = genres.size) {
                    SelectableGenreChip(
                        genre = genres[it].name,
                        selected = genres[it].name == homeViewModel.selectedGenre.value.name,
                        onclick = {
                            if (homeViewModel.selectedGenre.value.name != genres[it].name) {
                                homeViewModel.selectedGenre.value = genres[it]
                                homeViewModel.filterBySetSelectedGenre(genre = genres[it])
                            }
                        }
                    )
                }
            }
        }

        item {
            Text(
                text = "Trending",
                fontSize = 24.sp,
                color = AppOnPrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }
        item {
            ScrollableMovieItems(
                landscape = true,
                navigator = navigator,
                pagingItems = trendingFilms,
                onErrorClick = {
                    homeViewModel.refreshAll()
                }
            )
        }

        item {
            Text(
                text = "Popular",
                fontSize = 24.sp,
                color = AppOnPrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 6.dp)
            )
        }
        item {
            ScrollableMovieItems(
                navigator = navigator,
                pagingItems = popularFilms,
                onErrorClick = {
                    homeViewModel.refreshAll()
                }
            )
        }

        item {
            Text(
                text = "Top Rated",
                fontSize = 24.sp,
                color = AppOnPrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 14.dp, bottom = 8.dp)
            )
        }
        item {
            ScrollableMovieItems(
                navigator = navigator,
                pagingItems = topRatedFilms,
                onErrorClick = {
                    homeViewModel.refreshAll()
                }
            )
        }

        item {
            Text(
                text = "Now Playing",
                fontSize = 24.sp,
                color = AppOnPrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 14.dp, bottom = 4.dp)
            )
        }
        item {
            ScrollableMovieItems(
                navigator = navigator,
                pagingItems = nowPlayingFilms,
                onErrorClick = {
                    homeViewModel.refreshAll()
                }
            )
        }

        if (homeViewModel.selectedFilmType.value == FilmType.MOVIE) {
            item {
                Text(
                    text = "Upcoming",
                    fontSize = 24.sp,
                    color = AppOnPrimaryColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, top = 14.dp, bottom = 4.dp)
                )
            }

            item {
                ScrollableMovieItems(
                    navigator = navigator,
                    pagingItems = upcomingMovies,
                    onErrorClick = {
                        homeViewModel.refreshAll()
                    }
                )
            }
        }

        if (recommendedFilms.itemCount != 0) {
            item {
                ShowAboutCategory(
                    name = "For You",
                    description = "Recommendation based on your watchlist"
                )
            }

            item {
                ScrollableMovieItems(
                    navigator = navigator,
                    pagingItems = recommendedFilms,
                    onErrorClick = {
                        homeViewModel.refreshAll()
                        if (myWatchList.value.isNotEmpty()) {
                            val randomMovieId =
                                myWatchList.value[(0..myWatchList.value.lastIndex).random()].mediaId
                            homeViewModel.getRecommendedFilms(movieId = randomMovieId)
                        }
                    }
                )
            }
        }

        if (backInTheDays.itemCount != 0) {
            item {
                ShowAboutCategory(
                    name = "Back in the Days",
                    description = "Films released between 1940 and 1980"
                )
            }
            item {
                ScrollableMovieItems(
                    navigator = navigator,
                    pagingItems = backInTheDays,
                    onErrorClick = {
                        homeViewModel.refreshAll()
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun FilmItem(
    film: Film,
    navigator: DestinationsNavigator,
    homeViewModel: HomeViewModel
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("${BASE_POSTER_IMAGE_URL}/${film.posterPath}")
            .crossfade(true)
            .build(),
        contentDescription = "film poster",
        modifier = Modifier
            .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .size(130.dp, 195.dp)
            .clickable {
                navigator.navigate(
                    MovieDetailsDestination(
                        currentFilm = film
                    )
                )
            },
        contentScale = ContentScale.Crop,
        error = painterResource(id = R.drawable.image_not_available)
    )
}

@Composable
private fun ScrollableMovieItems(
    landscape: Boolean = false,
    showStickyBadge: Boolean = false,
    navigator: DestinationsNavigator,
    pagingItems: LazyPagingItems<Film>,
    onErrorClick: () -> Unit
) {
    Box(
        contentAlignment = Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (!landscape) 215.dp else 195.dp)
    ) {
        when (pagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                LoopReverseLottieLoader(lottieFile = R.raw.loader)
            }

            is LoadState.NotLoading -> {
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(
                        count = pagingItems.itemCount,
                        key = { index ->
                            val item = pagingItems[index]
                            item?.id ?: index
                        }
                    ) { index ->
                        val film = pagingItems[index]
                        film?.let {
                            FilmItem(
                                film = it,
                                navigator = navigator,
                                homeViewModel = hiltViewModel()
                            )
                        }
                    }
                }
            }

            is LoadState.Error -> {
                val error = pagingItems.loadState.refresh as LoadState.Error
                val errorMessage = when (error.error) {
                    is HttpException -> "Sorry, Something went wrong!\nTap to retry"
                    is IOException -> "Connection failed. Tap to retry!"
                    else -> "Failed! Tap to retry!"
                }
                Box(contentAlignment = Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(161.25.dp)
                        .clickable {
                            onErrorClick()
                        }
                ) {
                    Text(
                        text = errorMessage,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = Light,
                        color = Color(0xFFE28B8B),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            else -> {}
        }
    }
}

@Composable
fun SelectableGenreChip(
    genre: String,
    selected: Boolean,
    onclick: () -> Unit
) {

    val animateChipBackgroundColor by animateColorAsState(
        targetValue = if (selected) Color(0xFFA0A1C2) else ButtonColor.copy(alpha = 0.5F),
        animationSpec = tween(
            durationMillis = if (selected) 100 else 50,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    Box(
        modifier = Modifier
            .padding(end = 4.dp)
            .clip(CircleShape)
            .background(
                color = animateChipBackgroundColor
            )
            .height(32.dp)
            .widthIn(min = 80.dp)
            /*.border(
                width = 0.5.dp,
                color = Color(0xC69495B1),
                shape = CircleShape
            )*/
            .padding(horizontal = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onclick()
            }
    ) {
        Text(
            text = genre,
            fontWeight = if (selected) Normal else Light,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Center),
            color = if (selected) Color(0XFF180E36) else Color.White.copy(alpha = 0.80F)
        )
    }
}

@Composable
fun ShowAboutCategory(name: String, description: String) {
    var showAboutThisCategory by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = name,
            fontSize = 24.sp,
            color = AppOnPrimaryColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                start = 4.dp, top = 14.dp,
                end = 8.dp, bottom = 4.dp
            )
        )
        IconButton(
            modifier = Modifier.padding(top = 14.dp, bottom = 4.dp),
            onClick = { showAboutThisCategory = showAboutThisCategory.not() }) {
            Icon(
                imageVector = if (showAboutThisCategory) Icons.Filled.KeyboardArrowUp else Icons.Filled.Info,
                tint = AppOnPrimaryColor,
                contentDescription = "Info Icon"
            )
        }
    }

    AnimatedVisibility(visible = showAboutThisCategory) {
        Box(
            contentAlignment = Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .border(
                    width = 1.dp, color = ButtonColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .background(ButtonColor.copy(alpha = 0.25F))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = description,
                    color = AppOnPrimaryColor
                )
            }
        }
    }
}
