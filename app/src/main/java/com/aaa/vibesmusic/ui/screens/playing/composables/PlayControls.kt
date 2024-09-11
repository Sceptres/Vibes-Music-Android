package com.aaa.vibesmusic.ui.screens.playing.composables

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
import com.aaa.vibesmusic.player.mode.PlayMode
import com.aaa.vibesmusic.player.shuffle.ShuffleMode

@Composable
fun PlayControls(
    playStatus: PlayStatus,
    playMode: PlayMode,
    shuffleMode: ShuffleMode,
    modifier: Modifier = Modifier,
    playStatusToggle: () -> Unit = {},
    skipBackToggle: () -> Unit = {},
    skipForwardToggle: () -> Unit = {},
    playModeToggle: () -> Unit = {},
    shuffleModeToggle: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = if(playMode == PlayMode.REPEAT) R.drawable.repeat else R.drawable.repeat_one),
            contentDescription = "Play Mode Button",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(30.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { playModeToggle() }
        )

        Image(
            painter = painterResource(id = R.drawable.skip_previous_btn),
            contentDescription = "Skip to previous",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(45.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { skipBackToggle() }
        )

        Image(
            painter = painterResource(id = if(playStatus == PlayStatus.PAUSED) R.drawable.play_arrow else R.drawable.pause_button),
            contentDescription = "Play/Pause Button",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(63.dp)
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
                .size(45.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { skipForwardToggle() }
        )

        Image(
            painter = painterResource(id = if(shuffleMode == ShuffleMode.UNSHUFFLED) R.drawable.shuffle_off else R.drawable.shuffle_on),
            contentDescription = "Shuffle Button",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(30.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { shuffleModeToggle() }
        )
    }
}