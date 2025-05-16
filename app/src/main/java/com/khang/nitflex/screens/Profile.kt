package com.khang.nitflex.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.khang.nitflex.R
import com.khang.nitflex.screens.destinations.StartScreenDestination
import com.khang.nitflex.screens.destinations.WatchListDestination
import com.khang.nitflex.sharedComposables.BackButton
import com.khang.nitflex.sharedComposables.CustomSwitch
import com.khang.nitflex.ui.theme.AppOnPrimaryColor
import com.khang.nitflex.ui.theme.AppPrimaryColor
import com.khang.nitflex.ui.theme.ButtonColor
import com.khang.nitflex.viewmodel.AuthViewModel
import com.khang.nitflex.viewmodel.PrefsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph
@Destination
@Composable
fun Profile(
    navigator: DestinationsNavigator,
    prefsViewModel: PrefsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUserName by authViewModel.userName.collectAsState()
    val authToken by authViewModel.authToken.collectAsState()

    LaunchedEffect(authToken) {
        if (authToken == null) {
            navigator.navigate(StartScreenDestination) {
                popUpTo(StartScreenDestination.route) { inclusive = true }
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPrimaryColor)
    ) {
        val (
            backButton,
            editButton,
            profileHeading,
            userName,
            topBgImage,
            profilePhoto,
            imageBoarder,
            translucentBr,
            btnWatchList,
            btnLogout,
            settingsBox,
            appVersion
        ) = createRefs()

        BackButton(
            modifier = Modifier
                .constrainAs(backButton) {
                    start.linkTo(parent.start, margin = 10.dp)
                    top.linkTo(parent.top, margin = 16.dp)
                }) {
            navigator.navigateUp()
        }

        FloatingActionButton(
            modifier = Modifier
                .size(42.dp)
                .constrainAs(editButton) {
                    end.linkTo(parent.end, margin = 10.dp)
                    top.linkTo(parent.top, margin = 16.dp)
                },
            backgroundColor = ButtonColor,
            contentColor = AppOnPrimaryColor,
            onClick = { }) {
            Icon(imageVector = Icons.Rounded.Edit, contentDescription = "edit profile")
        }

        Text(
            modifier = Modifier.constrainAs(profileHeading) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(backButton.top)
                bottom.linkTo(backButton.bottom)
            },
            text = "Profile",
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppOnPrimaryColor
        )

        Image(
            painter = painterResource(id = R.drawable.popcorn),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.27F)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .constrainAs(topBgImage) {
                    top.linkTo(backButton.bottom, margin = 16.dp)
                },
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color(0XFF180E36).copy(alpha = 0.5F),
                            Color(0XFF180E36).copy(alpha = 0.75F),
                            Color(0XFF180E36).copy(alpha = 1F)
                        ),
                        startY = 0F
                    )
                )
                .constrainAs(translucentBr) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(topBgImage.bottom)
                }
        )

        /** Custom boarder -> Reason: The default Image  boarder wasn't working properly */
        Box(
            modifier = Modifier
                .size(83.5.dp)
                .clip(CircleShape)
                .background(AppPrimaryColor)
                .constrainAs(imageBoarder) {
                    top.linkTo(topBgImage.bottom)
                    start.linkTo(topBgImage.start, margin = 26.dp)
                    bottom.linkTo(topBgImage.bottom)
                }
        )

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.ic_user)
                .crossfade(true)
                .build(),
            contentDescription = "profile photo",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .constrainAs(profilePhoto) {
                    top.linkTo(topBgImage.bottom)
                    start.linkTo(topBgImage.start, margin = 28.dp)
                    bottom.linkTo(topBgImage.bottom)
                },
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.ic_user)
        )

        fun userName(name: String): String {
            return if (name.length <= 16) name else {
                name.removeRange(15..name.lastIndex) + "..."
            }
        }

        Text(
            text = userName(currentUserName ?: "User"),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = AppOnPrimaryColor,
            modifier = Modifier.constrainAs(userName) {
                top.linkTo(profilePhoto.bottom, margin = 4.dp)
                start.linkTo(profilePhoto.start)
                end.linkTo(profilePhoto.end)
            }
        )

        Button(
            modifier = Modifier
                .constrainAs(btnWatchList) {
                    end.linkTo(parent.end, margin = 24.dp)
                    top.linkTo(topBgImage.bottom)
                    bottom.linkTo(topBgImage.bottom)
                },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF4C3D6D),
                contentColor = AppOnPrimaryColor
            ),
            onClick = {
                navigator.navigate(WatchListDestination)
            }
        ) {
            Text(text = "My List")
        }

        Button(
            modifier = Modifier
                .constrainAs(btnLogout) {
                    start.linkTo(btnWatchList.start)
                    end.linkTo(btnWatchList.end)
                    top.linkTo(btnWatchList.bottom, margin = 8.dp)
                },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = ButtonColor,
                contentColor = AppOnPrimaryColor
            ),
            onClick = {
                authViewModel.logoutUser()
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Logout,
                contentDescription = "Logout",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Logout")
        }

        Column(
            modifier = Modifier.constrainAs(appVersion) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 24.dp)
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.neatflix_logo_large),
                modifier = Modifier.widthIn(max = 100.dp),
                alpha = 0.78F,
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Khang test @@",
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                color = AppOnPrimaryColor.copy(alpha = 0.5F)
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(12.dp))
                .background(Color(0XFF423460).copy(alpha = 0.46F))
                .heightIn(min = 100.dp)
                .constrainAs(settingsBox) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(appVersion.top)
                    top.linkTo(profilePhoto.bottom)
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Preferences",
                    modifier = Modifier.padding(vertical = 12.dp),
                    fontSize = 18.sp,
                    color = AppOnPrimaryColor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(
                    modifier = Modifier
                        .height((0.5).dp)
                        .fillMaxWidth(1F)
                        .background(AppOnPrimaryColor.copy(alpha = 0.15F))
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable Adult Search",
                        fontSize = 18.sp,
                        color = AppOnPrimaryColor,
                        fontWeight = FontWeight.Light
                    )

                    val enableAdultSearch =
                        prefsViewModel.includeAdult.value.collectAsState(initial = true)

                    CustomSwitch(
                        checkable = true,
                        checked = enableAdultSearch.value ?: true,
                        onCheckedChange = {
                            prefsViewModel.updateIncludeAdult(
                                enableAdultSearch.value?.not() ?: false
                            )
                        })
                }
                Spacer(
                    modifier = Modifier
                        .height((0.5).dp)
                        .fillMaxWidth(0.8F)
                        .background(AppOnPrimaryColor.copy(alpha = 0.12F))
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Use Device Theme",
                        fontSize = 18.sp,
                        color = AppOnPrimaryColor,
                        fontWeight = FontWeight.Light
                    )

                    CustomSwitch(
                        checkable = remember { false },
                        checked = false,
                        onCheckedChange = {

                        })
                }
            }
        }
    }
}
