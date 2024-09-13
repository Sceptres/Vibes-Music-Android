package com.aaa.vibesmusic.ui.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

fun NavController.navigateToImportMusicScreen() {
    this.navigate(Screens.IMPORT_MUSIC_PATH) {
        popUpTo(this@navigateToImportMusicScreen.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}