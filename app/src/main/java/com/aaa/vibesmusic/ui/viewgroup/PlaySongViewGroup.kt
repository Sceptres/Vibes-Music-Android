package com.aaa.vibesmusic.ui.viewgroup

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.IBinder
import android.util.AttributeSet
import android.util.Log
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
import com.aaa.vibesmusic.ui.listener.OnCloseListener
import com.bumptech.glide.Glide
import java.util.Objects

class PlaySongViewGroup @JvmOverloads constructor(
    private val c: Context?,
    attributeSet: AttributeSet? = null
) : RelativeLayout(c, attributeSet), ServiceConnection, AnimationListener, MediaPlayer.OnPreparedListener {

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
            val animation: Animation = AnimationUtils.loadAnimation(this.context, R.anim.slide_down)
            animation.setAnimationListener(this)
            this.startAnimation(animation)
        }

        // Play and pause
        this.playSongBtn.setOnClickListener {
            val playStatus: PlayStatus = this.mediaPlayerService!!.playStatus

            if(playStatus == PlayStatus.PLAYING) {
                this.playSongBtn.setImageResource(R.drawable.play_arrow)
                this.mediaPlayerService!!.pause()
            } else if(playStatus == PlayStatus.PAUSED) {
                this.playSongBtn.setImageResource(R.drawable.pause_button)
                this.mediaPlayerService!!.resume()
            }
        }

        this.songSkipForwardBtn.setOnClickListener {
            this.mediaPlayerService!!.skipForward()
        }

        this.songSkipBackBtn.setOnClickListener {
            this.mediaPlayerService!!.skipBackward()
        }

        this.songPlayModeBtn.setOnClickListener {
            val playMode: PlayMode = this.mediaPlayerService!!.playMode

            if(playMode == PlayMode.REPEAT) {
                this.songPlayModeBtn.setImageResource(R.drawable.repeat_one)
                this.mediaPlayerService!!.playMode = PlayMode.REPEAT_ONE
            } else if(playMode == PlayMode.REPEAT_ONE) {
                this.songPlayModeBtn.setImageResource(R.drawable.repeat)
                this.mediaPlayerService!!.playMode = PlayMode.REPEAT
            }
        }

        this.songShuffleBtn.setOnClickListener {
            val shuffleMode: ShuffleMode = this.mediaPlayerService!!.shuffleMode

            if(shuffleMode == ShuffleMode.SHUFFLED) {
                this.songShuffleBtn.setImageResource(R.drawable.shuffle_off)
                this.mediaPlayerService!!.shuffleMode = ShuffleMode.UNSHUFFLED
            } else if(shuffleMode == ShuffleMode.UNSHUFFLED) {
                this.songShuffleBtn.setImageResource(R.drawable.shuffle_on)
                this.mediaPlayerService!!.shuffleMode = ShuffleMode.SHUFFLED
            }
        }
    }

    /**
     * The [OnCloseListener] of this view
     */
    fun setOnCloseListener(listener: OnCloseListener) {
        this.onCloseListener = listener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("Player", "ATTACHING")
        val serviceIntent: Intent = Intent(this.context, MediaPlayerService::class.java)
        val application: Application = this.context.applicationContext as Application
        application.bindService(serviceIntent, this, AppCompatActivity.BIND_AUTO_CREATE)
        application.startService(serviceIntent)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        this.mediaPlayerService!!.removePreparedListener(this)
    }

    /**
     * Close the view and run the [OnCloseListener] of this [View]
     */
    private fun closeView() {
        if(Objects.nonNull(this.onCloseListener))
            this.onCloseListener!!.onClose()
    }

    override fun onAnimationStart(animation: Animation?) {}

    override fun onAnimationEnd(animation: Animation?) {
        this.removeAllViews()
        (this.parent as ViewGroup).removeView(this)
    }

    override fun onAnimationRepeat(animation: Animation?) {}

    override fun onPrepared(mp: MediaPlayer?) {
        val currentSong: Song = this.mediaPlayerService!!.currentSong

        this.songNamePlayer.text = currentSong.name
        this.songArtistAlbumPlayer.text = currentSong.artist

        val bitmapToLoad = if(Objects.nonNull(currentSong.imageLocation) && StorageUtil.fileExists(currentSong.imageLocation))
            currentSong.imageLocation
        else
            R.drawable.music_cover_image


        Glide.with(this)
            .load(bitmapToLoad)
            .centerCrop()
            .placeholder(R.drawable.music_cover_image)
            .into(this.songCoverImageView)

        // Set the status of the play/pause button
        if(this.mediaPlayerService!!.playStatus == PlayStatus.PLAYING)
            this.playSongBtn.setImageResource(R.drawable.pause_button)
        else if(this.mediaPlayerService!!.playStatus == PlayStatus.PAUSED)
            this.playSongBtn.setImageResource(R.drawable.play_arrow)

        // Set the status of the play mode button
        if(this.mediaPlayerService!!.playMode == PlayMode.REPEAT)
            this.songPlayModeBtn.setImageResource(R.drawable.repeat)
        else if(this.mediaPlayerService!!.playMode == PlayMode.REPEAT_ONE)
            this.songPlayModeBtn.setImageResource(R.drawable.repeat_one)

        // Set status of shuffle music button
        if(this.mediaPlayerService!!.shuffleMode == ShuffleMode.SHUFFLED)
            this.songShuffleBtn.setImageResource(R.drawable.shuffle_on)
        else if(this.mediaPlayerService!!.shuffleMode == ShuffleMode.UNSHUFFLED)
            this.songShuffleBtn.setImageResource(R.drawable.shuffle_off)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MediaPlayerService.MediaPlayerServiceBinder
        this.mediaPlayerService = binder.mediaPlayerService

        this.setListeners()

        this.mediaPlayerService!!.addPreparedListener(this)
        this.onPrepared(null)
    }

    override fun onServiceDisconnected(name: ComponentName?) {}
}