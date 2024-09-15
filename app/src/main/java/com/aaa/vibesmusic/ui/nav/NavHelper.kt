package com.aaa.vibesmusic.ui.nav

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

/**
 * A method used to navigate to the import music screen from another nav item.
 */
fun NavController.navigateToImportMusicScreen() {
    this.navItemNavigate(Screens.IMPORT_MUSIC_PATH)
}

/**
 * A method used to navigate to the playlists music screen from another nav item
 */
fun NavController.navigateToPlaylistsScreen() {
    this.navItemNavigate(Screens.PLAYLISTS_NAV_PATH)
}

/**
 * A method used to navigate to the add/edit playlist songs screen
 * @param playlistId The [Int] playlistId that represents the playlist songs in the database
 */
fun NavController.navigateToAddEditPlaylistSongsScreen(playlistId: Int) {
    val addEditPath: String = Screens.ADD_EDIT_PLAYLIST_SONGS_PATH.replace(
        "{playlistId}",
        playlistId.toString()
    )
    this.navigate(addEditPath)
}

/**
 * A method used to navigate to the artist screen
 * @param artistName The name of the artist
 */
fun NavController.navigateToArtistScreen(artistName: String) {
    val artistNameEncoded: String = Uri.encode(artistName)
    val artistPath: String = Screens.ARTIST_PATH.replace(
        "{artistName}",
        artistNameEncoded
    )
    this.navigate(artistPath)
}

/**
 * A method used to navigate to the album screen
 * @param albumName The name of the album
 */
fun NavController.navigateToAlbumScreen(albumName: String) {
    val albumNameEncoded: String = Uri.encode(albumName)
    val albumScreenPath: String = Screens.ALBUM_PATH.replace(
        "{albumName}",
        albumNameEncoded
    )
    this.navigate(albumScreenPath)
}

/**
 * A method used to navigate to the add song to playlist screen
 * @param songId The [Int] id of the song in the database
 */
fun NavController.navigateToAddSongToPlaylistScreen(songId: Int) {
    val addSongToPlaylistPath: String = Screens.ADD_SONG_TO_PLAYLIST_PATH.replace(
        "{songId}",
        songId.toString()
    )
    this.navigate(addSongToPlaylistPath)
}

/**
 * A method used to navigate the the playlist screen
 * @param playlistId The [Int] id of the playlist in the database
 */
fun NavController.navigateToPlaylistScreen(playlistId: Int) {
    val playlistPath: String = Screens.PLAYLIST_PATH.replace(
        "{playlistId}",
        playlistId.toString()
    )
    this.navigate(playlistPath)
}

/**
 * A method used to navigate to the music library screen
 */
fun NavController.navigateToMusicLibraryScreen() {
    this.navigate(Screens.MUSIC_LIBRARY_PATH)
}

/**
 * A method used to navigate to the artists screen
 */
fun NavController.navigateToArtistsScreen() {
    this.navigate(Screens.ARTISTS_PATH)
}

/**
 * A method used to navigate to the albums screen
 */
fun NavController.navigateToAlbumsScreen() {
    this.navigate(Screens.ALBUMS_PATH)
}

/**
 * A method used to control the navigation between nav bar items
 * @param route The route of the nav bar item
 */
fun NavController.navItemNavigate(route: String) {
    this.navigate(route) {
        popUpTo(this@navItemNavigate.graph.findStartDestination().id) {
            this@popUpTo.saveState = true
        }
        this@navigate.launchSingleTop = true
        this@navigate.restoreState = true
    }
}