package com.aaa.vibesmusic.ui.screens.albums

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.views.album.AlbumView

class AlbumsScreenViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AlbumsScreenViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(application)

    val albums: MutableList<AlbumView> = mutableStateListOf()
    private val albumsLiveData: LiveData<List<AlbumView>> = this.getAlbumsLiveData()
    private val albumsObserver: Observer<List<AlbumView>> = Observer {
        this.albums.clear()
        this.albums.addAll(it)
    }

    init {
        this.albumsLiveData.observeForever(this.albumsObserver)
    }

    private fun getAlbumsLiveData(): LiveData<List<AlbumView>> {
        return this.db.albumViewDao().allAlbums
    }

    override fun onCleared() {
        super.onCleared()
        this.albumsLiveData.removeObserver(this.albumsObserver)
    }
}