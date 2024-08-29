package com.aaa.vibesmusic.ui.viewgroup

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.IBinder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.player.PlayStatus
import com.aaa.vibesmusic.player.mode.PlayMode
import com.aaa.vibesmusic.player.shuffle.ShuffleMode
import com.aaa.vibesmusic.storage.StorageUtil
import com.aaa.vibesmusic.ui.activity.MainActivity
import com.aaa.vibesmusic.ui.listener.OnCloseListener
import com.aaa.vibesmusic.ui.listener.OnPlaySeekListener
import java.util.Objects
import kotlin.math.abs
import kotlin.properties.Delegates

class PlaySongViewGroup @JvmOverloads constructor(
    private val c: Context?,
    attributeSet: AttributeSet? = null
) : RelativeLayout(c, attributeSet), ServiceConnection, AnimationListener, MediaPlayer.OnPreparedListener, OnPlaySeekListener {

    private var oldStatusBarColor by Delegates.notNull<Int>()
    private var mediaPlayerService: MediaPlayerService? = null
    private var onCloseListener: OnCloseListener? = null
    private val songPlayerDropBtn: ImageButton
    private val songCoverImageView: ImageView
    private val songNamePlayer: TextView
    private val songArtistAlbumPlayer: TextView
    private val songPlayerCurrentTime: TextView
    private val songPlayerEndTime: TextView
    private val songTimeBarPlayer: SeekBar
    private val songPlayModeBtn: ImageButton
    private val songSkipBackBtn: ImageButton
    private val playSongBtn: ImageButton
    private val songSkipForwardBtn: ImageButton
    private val songShuffleBtn: ImageButton

    init {
        LayoutInflater.from(this.c).inflate(R.layout.activity_song_player, this, true)

        this.songPlayerDropBtn = this.findViewById(R.id.songPlayerDropBtn)
        this.songCoverImageView = this.findViewById(R.id.songCoverImageView)
        this.songNamePlayer = this.findViewById(R.id.songNamePlayer)
        this.songArtistAlbumPlayer = this.findViewById(R.id.songArtistAlbumPlayer)
        this.songPlayerCurrentTime = this.findViewById(R.id.songPlayerCurrentTime)
        this.songPlayerEndTime = this.findViewById(R.id.songPlayerEndTime)
        this.songTimeBarPlayer = this.findViewById(R.id.songTimeBarPlayer)
        this.songPlayModeBtn = this.findViewById(R.id.songPlayModeBtn)
        this.songSkipBackBtn = this.findViewById(R.id.songSkipBackBtn)
        this.playSongBtn = this.findViewById(R.id.playSongBtn)
        this.songSkipForwardBtn = this.findViewById(R.id.songSkipForwardBtn)
        this.songShuffleBtn = this.findViewById(R.id.songShuffleBtn)
    }

    /**
     * Set the listeners of the views
     */
    private fun setListeners() {
        // Close view listener
        this.songPlayerDropBtn.setOnClickListener {
            this.closeView()
        }

        // Play and pause
        this.playSongBtn.setOnClickListener {
            if(!this.mediaPlayerService!!.isEmpty) {
                val playStatus: PlayStatus = this.mediaPlayerService!!.playStatus

                if (playStatus == PlayStatus.PLAYING) {
                    this.mediaPlayerService!!.pause()
                } else if (playStatus == PlayStatus.PAUSED) {
                    this.mediaPlayerService!!.resume()
                }
            }
        }

        // Skip forward
        this.songSkipForwardBtn.setOnClickListener {
            if(!this.mediaPlayerService!!.isEmpty)
                this.mediaPlayerService!!.skipForward()
        }

        // Skip backward
        this.songSkipBackBtn.setOnClickListener {
            if(!this.mediaPlayerService!!.isEmpty)
                this.mediaPlayerService!!.skipBackward()
        }

        // Play mode button
        this.songPlayModeBtn.setOnClickListener {
            if(!this.mediaPlayerService!!.isEmpty) {
                val playMode: PlayMode = this.mediaPlayerService!!.playMode

                if (playMode == PlayMode.REPEAT) {
                    this.mediaPlayerService!!.playMode = PlayMode.REPEAT_ONE
                } else if (playMode == PlayMode.REPEAT_ONE) {
                    this.mediaPlayerService!!.playMode = PlayMode.REPEAT
                }
            }
        }

        // Shuffle button
        this.songShuffleBtn.setOnClickListener {
            if(!this.mediaPlayerService!!.isEmpty) {
                val shuffleMode: ShuffleMode = this.mediaPlayerService!!.shuffleMode

                if (shuffleMode == ShuffleMode.SHUFFLED) {
                    this.mediaPlayerService!!.shuffleMode = ShuffleMode.UNSHUFFLED
                } else if (shuffleMode == ShuffleMode.UNSHUFFLED) {
                    this.mediaPlayerService!!.shuffleMode = ShuffleMode.SHUFFLED
                }
            }
        }

        // Song time seekbar listener
        this.songTimeBarPlayer.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            var currentPosition: Int = 0
            val marginOfError = 1000

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                songPlayerCurrentTime.text = Song.calculateDuration(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                if(Objects.nonNull(mediaPlayerService) && mediaPlayerService!!.isPlaying) {
                    this.currentPosition = seekBar.progress
                    mediaPlayerService!!.pauseSeekListener()
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if(Objects.nonNull(mediaPlayerService) && mediaPlayerService!!.isPlaying) {
                    val seekBarCurrentPos = seekBar.progress
                    if(abs(this.currentPosition - seekBarCurrentPos) > this.marginOfError)
                        mediaPlayerService!!.seekTo(seekBarCurrentPos)
                    else
                        mediaPlayerService!!.resumeSeekListener()
                }
            }

        })
    }

    /**
     * The [OnCloseListener] of this view
     */
    fun setOnCloseListener(listener: OnCloseListener) {
        this.onCloseListener = listener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(Objects.isNull(this.mediaPlayerService)) {
            val serviceIntent: Intent = Intent(this.context, MediaPlayerService::class.java)
            val application: Application = this.context.applicationContext as Application
            application.bindService(serviceIntent, this, AppCompatActivity.BIND_AUTO_CREATE)
        }

        if(Objects.nonNull(this.mediaPlayerService)) {
            this.mediaPlayerService!!.setPreparedListener(this)
            this.mediaPlayerService!!.resumeSeekListener()
        }

        // Store the old color of the status bar to reset after closing the view group
        val activity = this.context as Activity
        this.oldStatusBarColor = activity.window.statusBarColor
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        this.mediaPlayerService!!.removePreparedListener()
    }

    /**
     * Close the view and run the [OnCloseListener] of this [View]
     */
    fun closeView() {
        if(Objects.nonNull(this.onCloseListener))
            this.onCloseListener!!.onClose()
        if(Objects.nonNull(this.mediaPlayerService))
            this.mediaPlayerService!!.pauseSeekListener()
        val animation: Animation = AnimationUtils.loadAnimation(this.context, R.anim.slide_down)
        animation.setAnimationListener(this)
        this.startAnimation(animation)
    }

    // When closing the view group, reset the status bar color
    override fun onAnimationStart(animation: Animation?) {
        val activity = this.context as Activity
    }

    override fun onAnimationEnd(animation: Animation?) {
        (this.parent as ViewGroup).removeView(this)
    }

    override fun onAnimationRepeat(animation: Animation?) {}

    override fun onPrepared(mp: MediaPlayer?) {
        if(!this.mediaPlayerService!!.isEmpty) {
            val currentSong: Song = this.mediaPlayerService!!.currentSong

            this.songNamePlayer.text = currentSong.name
            this.songArtistAlbumPlayer.text = currentSong.artist

            val bitmapToLoad =
                if (Objects.nonNull(currentSong.imageLocation) && StorageUtil.fileExists(currentSong.imageLocation))
                    currentSong.imageLocation
                else
                    R.drawable.music_cover_image
        }

        // Set the status of the play/pause button
        if (this.mediaPlayerService!!.playStatus == PlayStatus.PLAYING)
            this.playSongBtn.setImageResource(R.drawable.pause_button)
        else if (this.mediaPlayerService!!.playStatus == PlayStatus.PAUSED)
            this.playSongBtn.setImageResource(R.drawable.play_arrow)

        // Set the status of the play mode button
        if (this.mediaPlayerService!!.playMode == PlayMode.REPEAT)
            this.songPlayModeBtn.setImageResource(R.drawable.repeat)
        else if (this.mediaPlayerService!!.playMode == PlayMode.REPEAT_ONE)
            this.songPlayModeBtn.setImageResource(R.drawable.repeat_one)

        // Set status of shuffle music button
        if (this.mediaPlayerService!!.shuffleMode == ShuffleMode.SHUFFLED)
            this.songShuffleBtn.setImageResource(R.drawable.shuffle_on)
        else if (this.mediaPlayerService!!.shuffleMode == ShuffleMode.UNSHUFFLED)
            this.songShuffleBtn.setImageResource(R.drawable.shuffle_off)

        // Setup the song time seekbar
        if (!this.mediaPlayerService!!.isEmpty &&
            (this.mediaPlayerService!!.isPlaying || this.mediaPlayerService!!.playStatus == PlayStatus.PAUSED)) {
            this.songTimeBarPlayer.max = this.mediaPlayerService!!.currentSong.duration
            this.songTimeBarPlayer.progress = this.mediaPlayerService!!.currentPosition
            this.mediaPlayerService!!.setOnSeekListener(this)
            this.songPlayerEndTime.text = Song.calculateDuration(this.mediaPlayerService!!.currentSong.duration)
        } else {
            this.songTimeBarPlayer.progress = 0

            val zeroTime = Song.calculateDuration(0)
            this.songPlayerCurrentTime.text = zeroTime
            this.songPlayerEndTime.text = zeroTime
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MediaPlayerService.MediaPlayerServiceBinder
        this.mediaPlayerService = binder.mediaPlayerService

        this.setListeners()

        this.mediaPlayerService!!.setPreparedListener(this)
        this.onPrepared(null)
    }

    override fun onServiceDisconnected(name: ComponentName?) {}

    override fun onPlaySeek(player: MediaPlayer?) {
        if(Objects.nonNull(player)) {
            val activity = this.context as MainActivity
            activity.runOnUiThread{
                val currentTime = player!!.currentPosition
                this.songTimeBarPlayer.progress = currentTime
            }
        }
    }
}