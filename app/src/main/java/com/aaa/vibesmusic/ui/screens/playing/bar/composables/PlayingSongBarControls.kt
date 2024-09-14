package com.aaa.vibesmusic.ui.screens.playing.bar.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.player.PlayStatus

@Composable
fun PlayingSongBarControls(
    playStatus: PlayStatus,
    playStatusToggle: () -> Unit = {},
    skipBackToggle: () -> Unit = {},
    skipForwardToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.skip_previous_btn),
            contentDescription = "Skip to previous",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(35.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { skipBackToggle() }
        )

        Image(
            painter = if(playStatus == PlayStatus.PAUSED) painterResource(R.drawable.play_arrow) else painterResource(
                R.drawable.pause_button),
            contentDescription = "Play/Pause Button",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(45.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { playStatusToggle() }
        )

        Image(
            painter = painterResource(id = R.drawable.skip_forward_btn),
            contentDescription = "Skip to next",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(35.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { skipForwardToggle() }
        )
    }
}