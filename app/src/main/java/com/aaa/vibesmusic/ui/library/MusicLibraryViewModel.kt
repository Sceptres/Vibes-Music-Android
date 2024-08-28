package com.aaa.vibesmusic.ui.library

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song

class MusicLibraryViewModel : ViewModel() {

    var db: VibesMusicDatabase = VibesMusicDatabase.getInstance(null)
    val songs = this.getSongsFromDatabase()

    private fun getSongsFromDatabase(): LiveData<List<Song>> {
        return this.db.songDao().songs
    }

    @Composable
    fun getNotificationsPermissionLauncher(permissionGrantedHandler: (Boolean) -> Unit): ManagedActivityResultLauncher<String, Boolean> {
        return rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), permissionGrantedHandler)
    }
}