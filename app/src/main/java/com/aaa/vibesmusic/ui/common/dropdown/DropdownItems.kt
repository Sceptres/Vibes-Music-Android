package com.aaa.vibesmusic.ui.common.dropdown

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.state.FavouriteSongState
import kotlinx.coroutines.CoroutineScope

@Composable
fun FavouriteDropdownMenuItem(
    song: Song,
    dropdownCloser: () -> Unit,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val context: Context = LocalContext.current
    val state: FavouriteSongState by remember { mutableStateOf(FavouriteSongState(context)) }

    CustomDropdownMenuItem(
        text = if(!song.isFavourite) "Add to Favourites" else "Remove from Favourites",
        onClick = {
            state.toggleSongFavourite(
                song = song,
                onSuccess = {
                    val songAction: String = if(!song.isFavourite) "Added" else "Removed"
                    val songToFrom: String = if(!song.isFavourite) "to" else "from"
                    UIUtil.showSnackBar(
                        snackBarScope = snackBarScope,
                        snackBarState = snackBarState,
                        message = "$songAction ${song.name} $songToFrom favourite songs."
                    )
                    dropdownCloser()
                },
                onFail = {
                    val songAction: String = if(!song.isFavourite) "add" else "remove"
                    val songToFrom: String = if(!song.isFavourite) "to" else "from"
                    UIUtil.showSnackBar(
                        snackBarScope = snackBarScope,
                        snackBarState = snackBarState,
                        message = "Failed to $songAction ${song.name} $songToFrom favourite songs."
                    )
                    dropdownCloser()
                }
            )
        }
    )
}