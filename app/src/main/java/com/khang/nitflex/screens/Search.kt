package com.khang.nitflex.screens

import com.khang.nitflex.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.khang.nitflex.model.Film
import com.khang.nitflex.screens.destinations.MovieDetailsDestination
import com.khang.nitflex.sharedComposables.BackButton
import com.khang.nitflex.sharedComposables.SearchBar
import com.khang.nitflex.sharedComposables.SearchResultItem
import com.khang.nitflex.ui.theme.AppOnPrimaryColor
import com.khang.nitflex.ui.theme.AppPrimaryColor
import com.khang.nitflex.util.Constants.BASE_POSTER_IMAGE_URL
import com.khang.nitflex.util.collectAsStateLifecycleAware
import com.khang.nitflex.viewmodel.HomeViewModel
import com.khang.nitflex.viewmodel.SearchViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph
@Destination
@Composable
fun SearchScreen(
    navigator: DestinationsNavigator,
    searchViewModel: SearchViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val searchResult = searchViewModel.multiSearchState.value.collectAsLazyPagingItems()
    val includeAdult =
        searchViewModel.includeAdult.value.collectAsStateLifecycleAware(initial = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPrimaryColor)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 16.dp)
                .fillMaxWidth(fraction = 0.60F)
        ) {
            val focusManager = LocalFocusManager.current
            BackButton {
                focusManager.clearFocus()
                navigator.navigateUp()
            }

            Text(
                text = "Search",
                modifier = Modifier.padding(start = 50.dp),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = AppOnPrimaryColor
            )
        }

        SearchBar(
            autoFocus = true,
            onSearch = {
                searchViewModel.searchRemoteMovie(includeAdult.value ?: true)
            })

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            when (searchResult.loadState.refresh) {
                is LoadState.NotLoading -> {
                    items(
                        count = searchResult.itemCount,
                        key = { index -> 
                            val item = searchResult[index]
                            item?.id ?: index
                        }
                    ) { index ->
                        val film = searchResult[index]
                        film?.let {
                            val focus = LocalFocusManager.current
                            SearchResultItem(
                                title = it.title,
                                mediaType = it.mediaType,
                                posterImage = "$BASE_POSTER_IMAGE_URL/${it.posterPath}",
                                genres = homeViewModel.filmGenres.filter { genre ->
                                    return@filter if (it.genreIds.isNullOrEmpty()) false else
                                        it.genreIds.contains(genre.id)
                                },
                                rating = (it.voteAverage ?: 0) as Double,
                                releaseYear = it.releaseDate,
                                onClick = {
                                    val navFilm = Film(
                                        adult = it.adult ?: false,
                                        backdropPath = it.backdropPath,
                                        posterPath = it.posterPath,
                                        genreIds = it.genreIds,
                                        genres = it.genres,
                                        mediaType = it.mediaType,
                                        id = it.id ?: 0,
                                        imdbId = it.imdbId,
                                        originalLanguage = it.originalLanguage ?: "",
                                        overview = it.overview ?: "",
                                        popularity = it.popularity ?: 0F.toDouble(),
                                        releaseDate = it.releaseDate ?: "",
                                        runtime = it.runtime,
                                        title = it.title ?: "",
                                        video = it.video ?: false,
                                        voteAverage = it.voteAverage ?: 0F.toDouble(),
                                        voteCount = it.voteCount ?: 0
                                    )
                                    focus.clearFocus()
                                    navigator.navigate(
                                        direction = MovieDetailsDestination(navFilm)
                                    ) {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                    if (searchResult.itemCount == 0) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 60.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.no_match_found),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }

                is LoadState.Loading -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.no_match_found),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}
