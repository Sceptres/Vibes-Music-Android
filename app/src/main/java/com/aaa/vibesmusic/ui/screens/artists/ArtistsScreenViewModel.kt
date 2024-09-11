package com.aaa.vibesmusic.ui.screens.artists

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
import com.aaa.vibesmusic.database.views.artist.ArtistView

class ArtistsScreenViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ArtistsScreenViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(application)

    val artists: MutableList<ArtistView> = mutableStateListOf()
    private val artistsLiveData: LiveData<List<ArtistView>> = this.getArtistsLiveData()
    private val artistsObserver: Observer<List<ArtistView>> = Observer {
        this.artists.clear()
        this.artists.addAll(it)
    }

    init {
        this.artistsLiveData.observeForever(this.artistsObserver)
    }

    private fun getArtistsLiveData(): LiveData<List<ArtistView>> {
        return this.db.artistViewDao().allArtists
    }
}