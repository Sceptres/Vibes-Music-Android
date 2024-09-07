package com.aaa.vibesmusic.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class UIUtil {
    companion object {
        /**
         * @param snackBarScope The [CoroutineScope] to launch the snackbar request on
         * @param snackBarState The [SnackbarHostState] of the snackbar
         * @param message The message of the snackbar
         * @param duration The [SnackbarDuration] of the snackbar. Defaults to [SnackbarDuration.Short]
         */
        fun showSnackBar(
            snackBarScope: CoroutineScope,
            snackBarState: SnackbarHostState,
            message: String,
            duration: SnackbarDuration = SnackbarDuration.Short
        ) {
            snackBarScope.launch {
                snackBarState.showSnackbar(
                    message = message,
                    duration = duration
                )
            }
        }
    }
}