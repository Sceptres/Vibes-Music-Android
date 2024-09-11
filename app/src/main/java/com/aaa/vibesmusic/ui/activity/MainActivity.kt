package com.aaa.vibesmusic.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.aaa.vibesmusic.storage.StorageUtil
import com.aaa.vibesmusic.ui.anim.PlayingSongScreenAnim
import com.aaa.vibesmusic.ui.artist.ArtistsScreen
import com.aaa.vibesmusic.ui.library.MusicLibraryScreen
import com.aaa.vibesmusic.ui.nav.Screens
import com.aaa.vibesmusic.ui.playing.PlayingSongScreen
import com.aaa.vibesmusic.ui.playlist.PlaylistScreen
import com.aaa.vibesmusic.ui.playlists.PlaylistsScreen
import com.aaa.vibesmusic.ui.playlistselect.AddSongToPlaylistScreen
import com.aaa.vibesmusic.ui.songimport.ImportSongsScreen
import com.aaa.vibesmusic.ui.songselect.AddEditPlaylistSongsScreen
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        this.firebaseAnalytics = Firebase.analytics

        VibesMusicDatabase.getInstance(applicationContext)
        StorageUtil.setup(this.applicationContext)

        setContent {
            VibesMusicApp()
        }
    }

    @Composable
    fun VibesMusicApp() {
        val job: Job = Job()
        val globalCoroutineScope: CoroutineScope = CoroutineScope(job)

        val navController = rememberNavController()
        val snackBarHostState = remember { SnackbarHostState() }
        val snackBarScope: CoroutineScope = rememberCoroutineScope()
        val playingSongScreenState: MutableTransitionState<Boolean> = remember { MutableTransitionState(false) }

        val backgroundColor: Color = colorResource(id = R.color.background_color)
        val navBarColor: Color = colorResource(id = R.color.navbar_color)

        var navBarColorState: Color by remember { mutableStateOf(backgroundColor) }
        var statusBarColorState: Color by remember { mutableStateOf(backgroundColor) }

        LaunchedEffect(navBarColorState) {
            window.navigationBarColor = navBarColorState.toArgb()
        }

        LaunchedEffect(statusBarColorState) {
            window.statusBarColor = statusBarColorState.toArgb()
        }

        if(!playingSongScreenState.currentState || !playingSongScreenState.targetState) {
            AppScaffold(
                navController = navController,
                statusBarColorSetter = { color -> statusBarColorState = color },
                snackBarHostState = snackBarHostState,
                snackBarScope = snackBarScope,
                globalCoroutineScope = globalCoroutineScope,
                openPlayingSongScreen = { playingSongScreenState.targetState = true }
            )

            if(!playingSongScreenState.targetState)
                navBarColorState = navBarColor
        }

        PlayingSongScreenAnim(visibleState = playingSongScreenState) {
            PlayingSongScreen(
                closeScreen = { playingSongScreenState.targetState = false }
            )
            statusBarColorState = backgroundColor
            navBarColorState = backgroundColor
        }
    }

    @Composable
    fun AppScaffold(
        navController: NavHostController,
        statusBarColorSetter: (Color) -> Unit,
        snackBarHostState: SnackbarHostState,
        snackBarScope: CoroutineScope,
        globalCoroutineScope: CoroutineScope,
        openPlayingSongScreen: () -> Unit
    ) {
        val navBarColor: Color = colorResource(id = R.color.navbar_color)
        val backgroundColor: Color = colorResource(id = R.color.background_color)
        val foregroundColor: Color = colorResource(id = R.color.foreground_color)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = navBarColor,
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
                startDestination = "library_nav",
                modifier = Modifier.padding(innerPadding),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                navigation(
                    startDestination = Screens.MusicLibrary.route,
                    route = "library_nav"
                ) {
                    composable(route = Screens.MusicLibrary.route) {
                        statusBarColorSetter(backgroundColor)
                        MusicLibraryScreen(
                            navController = navController,
                            snackBarState = snackBarHostState,
                            snackBarScope = snackBarScope,
                            openPlayingSongScreen = openPlayingSongScreen
                        )
                    }

                    composable(route = Screens.ARTISTS_PATH) {
                        statusBarColorSetter(backgroundColor)
                        ArtistsScreen(
                            navController = navController,
                            openPlayingSongScreen = openPlayingSongScreen
                        )
                    }

                    composable(
                        route = Screens.ADD_SONG_TO_PLAYLIST_PATH,
                        arguments = listOf(navArgument("songId") { type = NavType.IntType })
                    ) { backStack ->
                        val songId: Int = backStack.arguments?.getInt("songId") ?:
                            throw IllegalArgumentException("Missing {songId} argument")
                        statusBarColorSetter(backgroundColor)
                        AddSongToPlaylistScreen(
                            songId = songId,
                            navController = navController,
                            snackBarState = snackBarHostState,
                            snackBarScope = snackBarScope
                        )
                    }
                }

                composable(route = Screens.ImportMusic.route) {
                    statusBarColorSetter(backgroundColor)
                    ImportSongsScreen(
                        globalScope = globalCoroutineScope,
                        snackBarState = snackBarHostState,
                        snackBarScope = snackBarScope
                    )
                }

                navigation(
                    startDestination = Screens.PLAYLISTS_PATH,
                    route = "playlists_nav"
                ) {
                    composable(route = Screens.PLAYLISTS_PATH) {
                        statusBarColorSetter(backgroundColor)
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
                        statusBarColorSetter(foregroundColor)
                        PlaylistScreen(
                            playlistId = playlistId,
                            navController = navController,
                            openPlayingSongScreen = openPlayingSongScreen,
                            snackBarState = snackBarHostState,
                            snackBarScope = snackBarScope
                        )
                    }

                    composable(
                        route = Screens.ADD_EDIT_PLAYLIST_SONGS_PATH,
                        arguments = listOf(navArgument("playlistId") { type = NavType.IntType })
                    ) { backStack ->
                        val playlistId: Int = backStack.arguments?.getInt("playlistId") ?:
                        throw IllegalArgumentException("Missing {playlistId} argument")
                        statusBarColorSetter(backgroundColor)
                        AddEditPlaylistSongsScreen(
                            playlistId = playlistId,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}