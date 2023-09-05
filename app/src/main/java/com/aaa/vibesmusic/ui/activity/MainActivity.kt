package com.aaa.vibesmusic.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _binding = ActivityMainBinding.inflate(this.layoutInflater)
    }
}