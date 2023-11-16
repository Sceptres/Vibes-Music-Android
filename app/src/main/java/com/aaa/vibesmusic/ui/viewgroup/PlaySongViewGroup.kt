package com.aaa.vibesmusic.ui.viewgroup

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.aaa.vibesmusic.R
import kotlinx.coroutines.awaitAll

class PlaySongViewGroup @JvmOverloads constructor(private val c: Context?, attributeSet: AttributeSet? = null) :
    RelativeLayout(c, attributeSet), AnimationListener {

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

        this.songPlayerDropBtn.setOnClickListener {
            val animation: Animation = AnimationUtils.loadAnimation(this.context, R.anim.slide_down)
            animation.setAnimationListener(this)
            this.startAnimation(animation)
        }
    }

    override fun onAnimationStart(animation: Animation?) {}

    override fun onAnimationEnd(animation: Animation?) {
        this.removeAllViews()
        (this.parent as ViewGroup).removeView(this)
    }

    override fun onAnimationRepeat(animation: Animation?) {}
}