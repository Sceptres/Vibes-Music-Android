package com.aaa.vibesmusic.ui.screens.playing.bar

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.player.PlayStatus

class PlayingSongBarState(private val context: Context) {
    private var playerService: MediaPlayerService? = null

    // Music control states
    var playStatus: PlayStatus by mutableStateOf(PlayStatus.PAUSED)
    var seekBarValue: Int by mutableIntStateOf(0)
    var currentSong: Song? by mutableStateOf(this.playerService?.currentSong)

    private val preparedListener: (MediaPlayer?) -> Unit = {
        this.playStatus = this.playerService!!.playStatus
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
            MediaPlayerService.bindTo(this.context, this.serviceConnection)
        }
    }

    private fun isPlayerServiceNotEmpty(): Boolean {
        return this.playerService?.isEmpty == false
    }

    fun connectToPlayerService() {
        this.playerService?.let { mediaPlayerService ->
            mediaPlayerService.setPreparedListener{ preparedListener(it) }
            mediaPlayerService.setOnSeekListener{ seekListener(it) }

            // Call prepared listeners once to update states when service first connected
            preparedListener(null)
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

    fun skipBackToggle() {
        if(this.isPlayerServiceNotEmpty())
            this.playerService!!.skipBackward()
    }

    fun skipForwardToggle() {
        if(this.isPlayerServiceNotEmpty())
            this.playerService!!.skipForward()
    }

    fun onSliderValueChange(value: Int) {
        if(this.isPlayerServiceNotEmpty()) {
            if(this.playerService?.isSeekerPaused == false)
                this.playerService?.pauseSeekListener()

            this.seekBarValue = value
        }
    }

    fun onSliderValueChangeFinished() {
        if(this.isPlayerServiceNotEmpty()) {
            this.playerService?.seekTo(this.seekBarValue)
        }
    }
}