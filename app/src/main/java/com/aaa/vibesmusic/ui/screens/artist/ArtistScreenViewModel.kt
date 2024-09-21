package com.aaa.vibesmusic.ui.screens.artist

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
import com.aaa.vibesmusic.ui.viewmodel.PlayerServiceViewModel
import io.reactivex.disposables.CompositeDisposable

class ArtistScreenViewModel(application: Application, private val artist: String) : PlayerServiceViewModel(application) {
    companion object {
        fun getFactory(artist: String): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    ArtistScreenViewModel(this[APPLICATION_KEY] as Application, artist)
                }
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(application)
    private val disposables: CompositeDisposable = CompositeDisposable()

    val artistSongs: MutableList<Song> = mutableStateListOf()
    private val artistSongsLiveData: LiveData<List<Song>> = this.getArtistSongsLiveData()
    private val artistSongsObserver: Observer<List<Song>> = Observer {
        this.artistSongs.clear()
        this.artistSongs.addAll(it)
    }

    init {
        this.artistSongsLiveData.observeForever(this.artistSongsObserver)
    }

    fun onSongClicked(launcher: ManagedActivityResultLauncher<String, Boolean>, context: Context, index: Int) {
        super.onSongClicked(
            launcher = launcher,
            context = context,
            songs = this.artistSongs,
            index = index
        )
        this.artistSongsLiveData.observeForever(super.songsUpdatePlayerObserver)
    }

    private fun getArtistSongsLiveData(): LiveData<List<Song>> {
        return this.db.songDao().getArtistSongs(this.artist)
    }

    override fun onCleared() {
        super.onCleared()
        this.artistSongsLiveData.removeObserver(this.artistSongsObserver)
        this.artistSongsLiveData.removeObserver(super.songsUpdatePlayerObserver)
        this.disposables.clear()
    }
}