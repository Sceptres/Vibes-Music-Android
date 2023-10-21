package com.aaa.vibesmusic.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.databinding.ActivityMainBinding
import com.aaa.vibesmusic.storage.StorageUtil
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!

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
    }
}