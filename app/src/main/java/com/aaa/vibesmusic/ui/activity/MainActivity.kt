package com.aaa.vibesmusic.ui.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.databinding.ActivityMainBinding
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.storage.StorageUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Objects

class MainActivity : AppCompatActivity(), ServiceConnection {
    private lateinit var mediaPlayerService: MediaPlayerService
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!

    companion object {
        var SNACK_BAR_VIEW: CoordinatorLayout? = null
    }

    override fun onStart() {
        super.onStart()
        if(!this::mediaPlayerService.isInitialized) {
            val serviceIntent: Intent = Intent(this.applicationContext, MediaPlayerService::class.java)
            this.application.bindService(serviceIntent, this, AppCompatActivity.BIND_AUTO_CREATE)
            this.application.startService(serviceIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PermissionsUtil.POST_NOTIF_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(this.mediaPlayerService.isPlaying)
                this.mediaPlayerService.showNotification()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MediaPlayerService.MediaPlayerServiceBinder
        this.mediaPlayerService = binder.mediaPlayerService
    }

    override fun onServiceDisconnected(name: ComponentName?) {}
}