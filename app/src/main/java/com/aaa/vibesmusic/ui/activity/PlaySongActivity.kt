package com.aaa.vibesmusic.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aaa.vibesmusic.databinding.ActivitySongPlayerBinding

class PlaySongActivity : AppCompatActivity() {
    private var _binding: ActivitySongPlayerBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivitySongPlayerBinding.inflate(this.layoutInflater)
        setContentView(binding.root)
    }
}