package com.aaa.vibesmusic.ui.screens.playing.screen

import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.player.PlayStatus
import com.aaa.vibesmusic.player.mode.PlayMode
import com.aaa.vibesmusic.player.shuffle.ShuffleMode
import com.aaa.vibesmusic.ui.state.FavouriteSongState
import io.reactivex.disposables.CompositeDisposable

class PlayingSongsViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayingSongsViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.getApplication())
    private val disposables: CompositeDisposable = CompositeDisposable()
    private val favouriteSongState: FavouriteSongState = FavouriteSongState(this.getApplication())

    private var playerService: MediaPlayerService? by mutableStateOf(null)

    // Music control states
    var playStatus: PlayStatus by mutableStateOf(PlayStatus.PAUSED)
    var playMode: PlayMode by mutableStateOf(PlayMode.REPEAT)
    var shuffleMode: ShuffleMode by mutableStateOf(ShuffleMode.UNSHUFFLED)
    var seekBarValue: Int by mutableIntStateOf(0)
    var currentSong: Song? by mutableStateOf(this.playerService?.currentSong)

    private val preparedListener: (MediaPlayer?) -> Unit = {
        this.playStatus = this.playerService!!.playStatus
        this.playMode = this.playerService!!.playMode
        this.shuffleMode = this.playerService!!.shuffleMode
        this.currentSong = if(!this.playerService!!.isEmpty) this.playerService!!.currentSong else null
    }

    private val seekListener: (MediaPlayer?) -> Unit = { player ->
        player?.let {
            val currentTime = it.currentPosition
            seekBarValue = currentTime
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.MediaPlayerServiceBinder
            playerService = binder.mediaPlayerService
            connectToPlayerService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerService = null
        }
    }

    init {
        this.initPlayerService()
    }

    private fun initPlayerService() {
        this.playerService ?: run {
            MediaPlayerService.bindTo(super.getApplication(), this.serviceConnection)
        }
    }

    private fun isPlayerServiceNotEmpty(): Boolean {
        return this.playerService?.isEmpty == false
    }

    fun toggleSongFavourite() {
        this.currentSong?.let {
            this.favouriteSongState.toggleSongFavourite(it)
        }
    }

    fun connectToPlayerService() {
        this.playerService?.let { mediaPlayerService ->
            mediaPlayerService.setPreparedListener{ preparedListener(it) }
            mediaPlayerService.setOnSeekListener{ seekListener(it) }

            // Call prepared listeners once to update states when service first connected
            this.preparedListener(null)
        }
    }

    fun pausePlayToggle() {
        if(this.isPlayerServiceNotEmpty()) {
            if (this.playStatus == PlayStatus.PLAYING) {
                this.playerService!!.pause()
            } else if (this.playStatus == PlayStatus.PAUSED) {
                this.playerService!!.resume()
            }
        }
    }

    fun playModeToggle() {
        if(this.isPlayerServiceNotEmpty()) {
            if (this.playMode == PlayMode.REPEAT) {
                this.playerService!!.playMode = PlayMode.REPEAT_ONE
            } else if (this.playMode == PlayMode.REPEAT_ONE) {
                this.playerService!!.playMode = PlayMode.REPEAT
            }
        }
    }

    fun shuffleModeToggle() {
        if(this.isPlayerServiceNotEmpty()) {
            if (this.shuffleMode == ShuffleMode.SHUFFLED) {
                this.playerService!!.shuffleMode = ShuffleMode.UNSHUFFLED
            } else if (this.shuffleMode == ShuffleMode.UNSHUFFLED) {
                this.playerService!!.shuffleMode = ShuffleMode.SHUFFLED
            }
        }
    }

    fun skipBackToggle() {
        if(this.isPlayerServiceNotEmpty())
            this.playerService!!.skipBackward()
    }

    fun skipForwardToggle() {
        if(this.isPlayerServiceNotEmpty())
            this.playerService!!.skipForward()
    }

    fun onSliderValueChange(value: Int) {
        if(this.isPlayerServiceNotEmpty() && this.playStatus == PlayStatus.PLAYING) {
            if(this.playerService?.isSeekerPaused == false)
                this.playerService?.pauseSeekListener()

            this.seekBarValue = value
        }
    }

    fun onSliderValueChangeFinished() {
        if(this.isPlayerServiceNotEmpty() && this.playStatus == PlayStatus.PLAYING) {
            this.playerService?.seekTo(this.seekBarValue)
        }
    }

    override fun onCleared() {
        super.onCleared()
        this.playerService?.removePreparedListener()
        this.playerService?.pauseSeekListener()
        getApplication<Application>().unbindService(this.serviceConnection)
        this.disposables.clear()
        this.favouriteSongState.onClear()
    }
}