package com.aaa.vibesmusic.ui.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.databinding.ActivityMainBinding
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.storage.StorageUtil
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), ServiceConnection {
    var mediaPlayerService: MediaPlayerService? = null
    var isServiceBound: Boolean = false

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!

    companion object {
        var SNACK_BAR_VIEW: CoordinatorLayout? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(this.layoutInflater)

        setContentView(binding.root)

        VibesMusicDatabase.getInstance(applicationContext)
        StorageUtil.setup(this.applicationContext)

        val bottomNav: BottomNavigationView = binding.bottomNav
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNav.setupWithNavController(navController)

        SNACK_BAR_VIEW = binding.fragmentContainer
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent: Intent = Intent(this, MediaPlayerService::class.java)
        this.application.bindService(serviceIntent, this, BIND_AUTO_CREATE)
        this.application.startService(serviceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.mediaPlayerService?.onDestroy()
        this.application.unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MediaPlayerService.MediaPlayerServiceBinder
        this.mediaPlayerService = binder.mediaPlayerService
        this.isServiceBound = true
        Log.d("SERVICE", "PLAYER SERVICE HAS BEEN CONNECTED")
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        this.mediaPlayerService = null
        this.isServiceBound = false
    }
}