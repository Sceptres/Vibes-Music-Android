package com.aaa.vibesmusic.ui.activity

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.storage.StorageUtil
import com.aaa.vibesmusic.ui.anim.PlayingSongScreenAnim
import com.aaa.vibesmusic.ui.nav.Screens
import com.aaa.vibesmusic.ui.nav.navItemNavigate
import com.aaa.vibesmusic.ui.screens.album.AlbumScreen
import com.aaa.vibesmusic.ui.screens.albums.AlbumsScreen
import com.aaa.vibesmusic.ui.screens.artist.ArtistScreen
import com.aaa.vibesmusic.ui.screens.artists.ArtistsScreen
import com.aaa.vibesmusic.ui.screens.library.LibraryScreen
import com.aaa.vibesmusic.ui.screens.musiclibrary.MusicLibraryScreen
import com.aaa.vibesmusic.ui.screens.playing.bar.PlayingSongBar
import com.aaa.vibesmusic.ui.screens.playing.screen.PlayingSongScreen
import com.aaa.vibesmusic.ui.screens.playlist.PlaylistScreen
import com.aaa.vibesmusic.ui.screens.playlists.PlaylistsScreen
import com.aaa.vibesmusic.ui.screens.playlistselect.AddSongToPlaylistScreen
import com.aaa.vibesmusic.ui.screens.songimport.ImportSongsScreen
import com.aaa.vibesmusic.ui.screens.songselect.AddEditPlaylistSongsScreen
import com.aaa.vibesmusic.ui.theme.VibesMusicTheme
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        this.firebaseAnalytics = Firebase.analytics

        VibesMusicDatabase.getInstance(applicationContext)
        StorageUtil.setup(this.applicationContext)

        setContent {
            VibesMusicTheme {
                VibesMusicApp()
            }
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

        val backgroundColor: Color = MaterialTheme.colorScheme.background
        val navBarColor: Color = MaterialTheme.colorScheme.secondary

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
        val navBarColor: Color = MaterialTheme.colorScheme.secondary

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
                                navController.navItemNavigate(screen.route)
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            ConstraintLayout(
                modifier = Modifier.padding(innerPadding)
            ) {
                val (navHost, playingSongBar) = createRefs()

                NavHost(
                    navController = navController,
                    globalCoroutineScope = globalCoroutineScope,
                    snackBarHostState = snackBarHostState,
                    snackBarScope = snackBarScope,
                    statusBarColorSetter = statusBarColorSetter,
                    modifier = Modifier
                        .constrainAs(navHost) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(playingSongBar.top)
                            width = Dimension.preferredWrapContent
                            height = Dimension.preferredWrapContent
                        }
                )

                PlayingSongBar(
                    onClick = { openPlayingSongScreen() },
                    modifier = Modifier
                        .constrainAs(playingSongBar) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                )
            }
        }
    }

    @Composable
    private fun NavHost(
        navController: NavHostController,
        globalCoroutineScope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        snackBarScope: CoroutineScope,
        statusBarColorSetter: (Color) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val backgroundColor: Color = MaterialTheme.colorScheme.background
        val foregroundColor: Color = MaterialTheme.colorScheme.primary

        NavHost(
            navController = navController,
            startDestination = Screens.LIBRARY_NAV_PATH,
            modifier = modifier,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            navigation(
                startDestination = Screens.LIBRARY_PATH,
                route = Screens.LIBRARY_NAV_PATH
            ) {
                composable(route = Screens.LIBRARY_PATH) {
                    statusBarColorSetter(backgroundColor)
                    LibraryScreen(navController = navController)
                }

                composable(route = Screens.MUSIC_LIBRARY_PATH) {
                    statusBarColorSetter(backgroundColor)
                    MusicLibraryScreen(
                        navController = navController,
                        snackBarState = snackBarHostState,
                        snackBarScope = snackBarScope,
                    )
                }

                composable(route = Screens.ARTISTS_PATH) {
                    statusBarColorSetter(backgroundColor)
                    ArtistsScreen(navController = navController,)
                }

                composable(
                    route = Screens.ARTIST_PATH,
                    arguments = listOf(navArgument("artistName") { type = NavType.StringType })
                ) { backStack ->
                    val artistName: String = backStack.arguments?.getString("artistName") ?:
                    throw IllegalArgumentException("Missing {artistName} argument")
                    statusBarColorSetter(foregroundColor)
                    ArtistScreen(
                        artistName = Uri.decode(artistName),
                        navController = navController,
                        snackBarState = snackBarHostState,
                        snackBarScope = snackBarScope,
                    )
                }

                composable(
                    route = Screens.ALBUMS_PATH
                ) {
                    statusBarColorSetter(backgroundColor)
                    AlbumsScreen(navController = navController)
                }

                composable(
                    route = Screens.ALBUM_PATH,
                    arguments = listOf(navArgument("albumName") { type = NavType.StringType })
                ) { backStack ->
                    val albumName: String = backStack.arguments?.getString("albumName") ?:
                    throw IllegalArgumentException("Missing {albumName} argument")
                    statusBarColorSetter(foregroundColor)
                    AlbumScreen(
                        albumName = Uri.decode(albumName),
                        navController = navController,
                        snackBarState = snackBarHostState,
                        snackBarScope = snackBarScope,
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
                        snackBarState = snackBarHostState,
                        snackBarScope = snackBarScope
                    )
                }
            }

            composable(route = Screens.ImportMusic.route) {
                BackHandler {}

                statusBarColorSetter(backgroundColor)
                ImportSongsScreen(
                    globalScope = globalCoroutineScope,
                    snackBarState = snackBarHostState,
                    snackBarScope = snackBarScope
                )
            }

            navigation(
                startDestination = Screens.PLAYLISTS_PATH,
                route = Screens.PLAYLISTS_NAV_PATH
            ) {
                composable(route = Screens.PLAYLISTS_PATH) {
                    BackHandler {}

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