package com.khang.nitflex.sharedComposables

import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.khang.nitflex.ui.theme.AppOnPrimaryColor
import com.khang.nitflex.ui.theme.ButtonColor

@Composable
fun BackButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    FloatingActionButton(
        modifier = modifier.size(42.dp),
        backgroundColor = ButtonColor,
        contentColor = AppOnPrimaryColor,
        onClick = { onClick() }) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = "back icon",
        )
    }
}
