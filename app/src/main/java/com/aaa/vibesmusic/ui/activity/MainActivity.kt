package com.aaa.vibesmusic.ui.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.storage.StorageUtil
import com.aaa.vibesmusic.ui.import.ImportSongsScreen
import com.aaa.vibesmusic.ui.library.SongLibrary
import com.aaa.vibesmusic.ui.nav.Screens

class MainActivity : AppCompatActivity(), ServiceConnection {
    private lateinit var mediaPlayerService: MediaPlayerService

    companion object {
        // Keeping this to avoid errors during development
        var SNACK_BAR_VIEW: CoordinatorLayout? = null
    }

    override fun onStart() {
        super.onStart()
        if(!this::mediaPlayerService.isInitialized) {
            val serviceIntent: Intent = Intent(this.applicationContext, MediaPlayerService::class.java)
            this.application.bindService(serviceIntent, this, BIND_AUTO_CREATE)
            this.application.startService(serviceIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        VibesMusicDatabase.getInstance(applicationContext)
        StorageUtil.setup(this.applicationContext)

        setContent {
            VibesMusicApp()
        }
    }

    @Composable
    fun VibesMusicApp() {
        val navController = rememberNavController()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = colorResource(id = R.color.navbar_color),
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    Screens.SCREENS.forEach { screen ->
                        val navItemText: String = stringResource(id = screen.screenNavText)
                        val isSelected: Boolean = currentDestination?.hierarchy?.any{ it.route == screen.route } == true

                        NavigationBarItem(
                            colors = NavigationBarItemColors(
                                selectedIconColor = Color.Gray,
                                selectedTextColor = Color.Gray,
                                selectedIndicatorColor = Color.White,
                                unselectedIconColor = Color.White,
                                unselectedTextColor = Color.White,
                                disabledIconColor = Color.Gray,
                                disabledTextColor = Color.Gray
                            ),
                            icon = {
                                Icon(
                                    painter = painterResource(id = screen.screenNavIcon),
                                    contentDescription = navItemText
                                )
                           },
                            label = {
                                Text(
                                    text = navItemText
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screens.MusicLibrary.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = Screens.MusicLibrary.route) {
                    SongLibrary()
                }

                composable(route = Screens.ImportMusic.route) {
                    ImportSongsScreen()
                }
            }
        }
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