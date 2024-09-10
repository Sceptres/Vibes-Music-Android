package com.aaa.vibesmusic.ui.nav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.aaa.vibesmusic.R

sealed class Screens(val route: String, @StringRes val screenNavText: Int, @DrawableRes val screenNavIcon: Int) {
    companion object {
        private const val MUSIC_LIBRARY_PATH: String = "music_library"
        private const val IMPORT_MUSIC_PATH: String = "import_music"
        private const val PLAYLISTS_NAV_PATH: String = "playlists_nav"
        const val PLAYLISTS_PATH: String = "playlists"
        const val PLAYLIST_PATH: String = "playlist/{playlistId}"
        const val ADD_SONG_TO_PLAYLIST_PATH = "$MUSIC_LIBRARY_PATH/add_song_to_playlist/{songId}"

        val SCREENS = arrayOf(
            Playlists,
            MusicLibrary,
            ImportMusic
        )
    }

    data object MusicLibrary : Screens(MUSIC_LIBRARY_PATH, R.string.music_library, R.drawable.ic_music_library)
    data object ImportMusic : Screens(IMPORT_MUSIC_PATH, R.string.import_music, R.drawable.ic_import)
    data object Playlists : Screens(PLAYLISTS_NAV_PATH, R.string.playlists, R.drawable.playlist)
}