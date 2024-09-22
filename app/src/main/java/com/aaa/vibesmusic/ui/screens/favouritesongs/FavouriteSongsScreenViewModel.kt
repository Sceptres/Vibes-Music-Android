package com.aaa.vibesmusic.ui.screens.favouritesongs

import android.app.Application
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.common.SongItem
import com.aaa.vibesmusic.ui.viewmodel.PlayerServiceViewModel

class FavouriteSongsScreenViewModel(application: Application) : PlayerServiceViewModel(application) {
    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FavouriteSongsScreenViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.getApplication())

    val favouriteSongs: MutableList<SongItem> = mutableStateListOf()
    private val favouriteSongsLiveData: LiveData<List<Song>> = this.getFavouriteSongsLiveData()
    private val favouriteSongsObserver: Observer<List<Song>> = Observer {
        this.favouriteSongs.clear()
        this.favouriteSongs.addAll(it.mapIndexed { index, song -> SongItem(index, song) })
    }

    init {
        this.favouriteSongsLiveData.observeForever(this.favouriteSongsObserver)
    }

    fun onSongClick(launcher: ManagedActivityResultLauncher<String, Boolean>, context: Context, index: Int) {
        super.onSongClicked(
            launcher = launcher,
            context = context,
            songs = this.favouriteSongs.map(SongItem::song),
            index = index
        )
        this.favouriteSongsLiveData.observeForever(super.songsUpdatePlayerObserver)
    }

    private fun getFavouriteSongsLiveData(): LiveData<List<Song>> {
        return this.db.songDao().favouriteSongs
    }

    override fun onCleared() {
        super.onCleared()
        this.favouriteSongsLiveData.removeObserver(this.favouriteSongsObserver)
        this.favouriteSongsLiveData.removeObserver(super.songsUpdatePlayerObserver)
    }
}