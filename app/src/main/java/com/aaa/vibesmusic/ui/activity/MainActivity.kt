package com.aaa.vibesmusic.ui.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.storage.StorageUtil
import com.aaa.vibesmusic.ui.anim.PlayingSongScreenAnim
import com.aaa.vibesmusic.ui.import.ImportSongsScreen
import com.aaa.vibesmusic.ui.library.MusicLibraryScreen
import com.aaa.vibesmusic.ui.nav.Screens
import com.aaa.vibesmusic.ui.playing.PlayingSongScreen
import com.aaa.vibesmusic.ui.playlist.PlaylistScreen
import com.aaa.vibesmusic.ui.playlists.PlaylistsScreen
import kotlinx.coroutines.CoroutineScope
import java.lang.IllegalArgumentException

class MainActivity : AppCompatActivity(), ServiceConnection {
    private lateinit var mediaPlayerService: MediaPlayerService

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
        val snackBarHostState = remember { SnackbarHostState() }
        val snackBarScope: CoroutineScope = rememberCoroutineScope()
        val playingSongScreenState: MutableTransitionState<Boolean> = remember { MutableTransitionState(false) }

        val backgroundColor: Color = colorResource(id = R.color.background_color)
        val navBarColor: Color = colorResource(id = R.color.navbar_color)

        var navBarColorState: Color by remember { mutableStateOf(backgroundColor) }

        SideEffect {
            window.navigationBarColor = navBarColorState.toArgb()
        }

        if(!playingSongScreenState.currentState || !playingSongScreenState.targetState) {
            AppScaffold(
                navController = navController,
                snackBarHostState = snackBarHostState,
                snackBarScope = snackBarScope,
                openPlayingSongScreen = { playingSongScreenState.targetState = true }
            )
            navBarColorState = navBarColor
        }

        PlayingSongScreenAnim(visibleState = playingSongScreenState) {
            PlayingSongScreen(
                closeScreen = { playingSongScreenState.targetState = false }
            )
            navBarColorState = backgroundColor

            // Ensure swiping back closes the playing song screen
            onBackPressedDispatcher.addCallback(LocalLifecycleOwner.current, object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(playingSongScreenState.currentState) {
                        playingSongScreenState.targetState = false
                    } else {
                        isEnabled = false
                        moveTaskToBack(true)
                    }
                }
            })
        }
    }

    @Composable
    fun AppScaffold(
        navController: NavHostController,
        snackBarHostState: SnackbarHostState,
        snackBarScope: CoroutineScope,
        openPlayingSongScreen: () -> Unit
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = colorResource(id = R.color.navbar_color),
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    Screens.SCREENS.forEach { screen ->
                        val navItemText: String = stringResource(id = screen.screenNavText)
                        val isSelected: Boolean = currentDestination?.hierarchy?.any { it.route == screen.route } == true

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
                modifier = Modifier.padding(innerPadding),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                composable(route = Screens.MusicLibrary.route) {
                    MusicLibraryScreen(
                        snackBarState = snackBarHostState,
                        snackBarScope = snackBarScope,
                        openPlayingSongScreen = openPlayingSongScreen
                    )
                }

                composable(route = Screens.ImportMusic.route) {
                    ImportSongsScreen()
                }

                navigation(
                    startDestination = Screens.PLAYLISTS_PATH,
                    route = "playlists_nav"
                ) {
                    composable(route = Screens.PLAYLISTS_PATH) {
                        PlaylistsScreen(
                            navController = navController,
                            snackBarState = snackBarHostState,
                            snackBarScope = snackBarScope
                        )
                    }

                    composable(
                        route = Screens.PLAYLIST_PATH,
                        arguments = listOf(navArgument("playlistId") { type = NavType.IntType })
                    ) {backStack ->
                        val playlistId: Int = backStack.arguments?.getInt("playlistId") ?:
                        throw IllegalArgumentException("Missing {playlistId} argument")
                        PlaylistScreen(
                            playlistId = playlistId,
                            navController = navController
                        )
                    }
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