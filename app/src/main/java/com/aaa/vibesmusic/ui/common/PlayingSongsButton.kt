package com.aaa.vibesmusic.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aaa.vibesmusic.R

@Composable
fun PlayingSongsButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick.invoke() },
        containerColor = colorResource(id = R.color.blue_selected),
        content = {
            Icon(
                painter = painterResource(id = R.drawable.play_arrow),
                contentDescription = "Playing songs page",
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        },
        shape = CircleShape,
        modifier = modifier
    )
}