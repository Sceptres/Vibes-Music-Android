package com.aaa.vibesmusic.ui.library

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song

class MusicLibraryViewModel : ViewModel() {

    var db: VibesMusicDatabase = VibesMusicDatabase.getInstance(null)

    // Songs data state
    private val songsLiveData = this.getSongsFromDatabase()
    val songs: MutableList<Song> = mutableStateListOf()
    private val songsObserver: Observer<List<Song>> = Observer { value ->
        songs.clear()
        songs.addAll(value)
    }

    init {
        this.songsLiveData.observeForever(this.songsObserver)
    }

    private fun getSongsFromDatabase(): LiveData<List<Song>> {
        return this.db.songDao().songs
    }

    pr

    @Composable
    fun getNotificationsPermissionLauncher(permissionGrantedHandler: (Boolean) -> Unit): ManagedActivityResultLauncher<String, Boolean> {
        return rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), permissionGrantedHandler)
    }

    override fun onCleared() {
        super.onCleared()
        this.songsLiveData.removeObserver(this.songsObserver)
    }
}