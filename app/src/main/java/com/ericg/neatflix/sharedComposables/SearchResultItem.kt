package com.ericg.neatflix.sharedComposables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ericg.neatflix.R
import com.ericg.neatflix.ui.theme.AppOnPrimaryColor
import com.ericg.neatflix.ui.theme.ButtonColor
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import com.ericg.neatflix.model.Genre as MovieGenre

@Composable
fun SearchResultItem(
    title: String?,
    mediaType: String?,
    posterImage: String?,
    genres: List<MovieGenre>?,
    rating: Double,
    releaseYear: String?,
    onClick: () -> Unit?
) {
    Box(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, bottom = 12.dp)
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ButtonColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(posterImage)
                    .crossfade(true)
                    .build(),
                contentDescription = "poster image",
                modifier = Modifier
                    .fillMaxHeight()
                    .width(86.67.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.popcorn)
            )

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxSize()
            ) {
                var paddingValue by remember { mutableStateOf(2) }
                Text(
                    text = when (mediaType) {
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
                        .background(Color.LightGray.copy(alpha = 0.2F))
                        .padding(paddingValue.dp),
                    color = AppOnPrimaryColor.copy(alpha = 0.78F),
                    fontSize = 12.sp,
                )
                Text(
                    text = title ?: "",
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    color = AppOnPrimaryColor
                )

                Text(
                    text = releaseYear ?: "",
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.LightGray
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RatingBar(
                        value = rating.toFloat() / 2,
                        style = RatingBarStyle.Fill(),
                        onValueChange = {},
                        onRatingChanged = {},
                        modifier = Modifier.padding(end = 6.dp),
                        numOfStars = 5,
                        size = 16.dp,
                        spaceBetween = 4.dp,
                        isIndicator = true,
                        stepSize = StepSize.HALF,
                        hideInactiveStars = false,
                    )
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    genres?.forEach {
                        item {
                            MovieGenreChip(genre = it.name)
                        }
                    }
                }
            }
        }
    }
}
