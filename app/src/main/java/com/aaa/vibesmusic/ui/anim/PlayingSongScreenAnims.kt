package com.aaa.vibesmusic.ui.anim

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset

@Composable
fun PlayingSongScreenAnim(
    visibleState: MutableTransitionState<Boolean>,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = slideIn(
            initialOffset = { IntOffset(0, it.height) },
            animationSpec = tween(
                durationMillis = 500,
                easing = LinearEasing
            )
        ),
        exit = slideOut(
            targetOffset = { IntOffset(0, it.height) },
            animationSpec = tween(
                durationMillis = 500,
                easing = LinearEasing
            )
        )
    ) {
        content()
    }
}