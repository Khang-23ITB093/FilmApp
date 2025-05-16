package com.khang.nitflex.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.khang.nitflex.R
import com.khang.nitflex.data.local.MyListMovie
import com.khang.nitflex.screens.destinations.HomeDestination
import com.khang.nitflex.sharedComposables.BackButton
import com.khang.nitflex.sharedComposables.SearchBar
import com.khang.nitflex.sharedComposables.SearchResultItem
import com.khang.nitflex.ui.theme.AppOnPrimaryColor
import com.khang.nitflex.ui.theme.AppPrimaryColor
import com.khang.nitflex.ui.theme.ButtonColor
import com.khang.nitflex.util.Constants
import com.khang.nitflex.util.collectAsStateLifecycleAware
import com.khang.nitflex.viewmodel.SearchViewModel
import com.khang.nitflex.viewmodel.WatchListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class)
@RootNavGraph
@Destination
@Composable
fun WatchList(
    navigator: DestinationsNavigator,
    watchListViewModel: WatchListViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    var totalDismissed by remember { mutableIntStateOf(0) }

    val myWatchList = watchListViewModel.watchList.value
        .collectAsStateLifecycleAware(initial = emptyList())

    var currentList: State<List<MyListMovie>> by remember { mutableStateOf(myWatchList) }
    if (searchViewModel.searchParamState.value.isEmpty()) {
        currentList = myWatchList
    }

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
                .fillMaxWidth()
        ) {
            val focusManager = LocalFocusManager.current
            BackButton {
                focusManager.clearFocus()
                navigator.navigateUp()
            }

            Text(
                text = "My Watch list",
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = AppOnPrimaryColor
            )

            IconButton(
                onClick = {
                    focusManager.clearFocus()
                    navigator.popBackStack()
                    navigator.navigate(HomeDestination()) {
                        this.launchSingleTop = true
                        this.restoreState
                    }
                }
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(id = R.drawable.ic_home),
                    tint = AppOnPrimaryColor,
                    contentDescription = "home icon"
                )
            }
        }

        SearchBar(
            autoFocus = false,
            onSearch = {
                val filterParam = searchViewModel.searchParam.value
                myWatchList.value.filter { movie ->
                    movie.title.contains(other = filterParam, ignoreCase = true)
                }.let {
                    currentList = mutableStateOf(it)
                    Timber.d("Filtered by $filterParam: ${it.size}")
                }
            }
        )

        fun countItems(items: Int): String {
            return when (items) {
                1 -> "Found 1 item"
                0 -> "There's nothing here!"
                else -> "Found $items items"
            }
        }

        var showNumberIndicator by remember { mutableStateOf(true) }
        AnimatedVisibility(visible = showNumberIndicator) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .border(
                        width = 1.dp, color = ButtonColor,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .background(ButtonColor.copy(alpha = 0.25F))
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = countItems(currentList.value.size),
                        color = AppOnPrimaryColor
                    )
                    IconButton(onClick = { showNumberIndicator = false }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cancel),
                            tint = AppOnPrimaryColor,
                            contentDescription = "Cancel button"
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize(),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(currentList.value, key = { it.mediaId }) { film ->
                SwipeToDismissItem(
                    modifier = Modifier,
                    onDismiss = {
                        watchListViewModel.removeFromWatchList(film.mediaId)
                        totalDismissed += 1
                    }) {
                    SearchResultItem(
                        title = film.title,
                        mediaType = null,
                        posterImage = "${Constants.BASE_POSTER_IMAGE_URL}/${film.imagePath}",
                        genres = emptyList(),
                        rating = film.rating,
                        releaseYear = film.releaseDate
                    ) { }
                }
            }
        }

        var openDialog by remember { mutableStateOf(true) }
        if (totalDismissed == 3 && openDialog && currentList.value.size > 1) {
            AlertDialog(
                title = { Text(text = "Delete All") },
                text = { Text(text = "Would you like to clear your watch list?") },
                shape = RoundedCornerShape(8.dp),
                confirmButton = {
                    TextButton(onClick = {
                        watchListViewModel.deleteWatchList()
                        openDialog = openDialog.not()
                    }) {
                        Text(text = "YES")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { openDialog = openDialog.not() }) {
                        Text(text = "NO")
                    }
                },
                backgroundColor = ButtonColor,
                contentColor = AppOnPrimaryColor,
                onDismissRequest = {
                    openDialog = openDialog.not()
                })
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SwipeToDismissItem(
    modifier: Modifier,
    onDismiss: () -> Unit,
    swippable: @Composable () -> Unit,
) {
    val dismissState = rememberDismissState(initialValue = DismissValue.Default,
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                onDismiss()
            }
            true
        })

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        background = {
            if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, ButtonColor.copy(alpha = 0.25F))
                            )
                        )
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterEnd)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color(0xFFFF6F6F),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        },
        dismissContent = {
            swippable()
        },
        directions = setOf(DismissDirection.EndToStart)
    )
}
