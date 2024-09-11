package com.aaa.vibesmusic.ui.screens.playlist.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaa.vibesmusic.R

@Composable
fun PlaylistTopBar(
    text: String,
    onBackArrowPressed: () -> Unit,
    onAddEditPressed: () -> Unit,
    addEditButtonSrcGenerator: @Composable () -> Painter,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(colorResource(id = R.color.foreground_color))
    ) {
        Image(
            painter = painterResource(id = R.drawable.back_arrow),
            contentDescription = "Back arrow",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(35.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onBackArrowPressed
                )
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Image(
            painter = addEditButtonSrcGenerator(),
            contentDescription = "Back arrow",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(end = 10.dp)
                .size(35.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onAddEditPressed
                )
        )
    }
}