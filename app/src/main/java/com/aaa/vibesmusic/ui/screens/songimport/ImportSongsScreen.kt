package com.aaa.vibesmusic.ui.screens.songimport

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.screens.songimport.composables.ImportSongsMethodButton
import kotlinx.coroutines.CoroutineScope

@Composable
fun ImportSongsScreen(
    globalScope: CoroutineScope,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val viewModel: ImportSongsScreenViewModel = viewModel(factory = ImportSongsScreenViewModel.getFactory(globalScope))
    val songActivityFileLauncher: ManagedActivityResultLauncher<Void?, List<Uri>> = viewModel.ActivityFilesLauncher(
        snackBarState = snackBarState,
        snackBarScope = snackBarScope
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.import_music),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(top = 5.dp)
            )

            Text(
                text = stringResource(id = R.string.local),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(top = 5.dp)
            )

            ImportSongsMethodButton(
                icon = painterResource(id = R.drawable.folder_24),
                text = stringResource(id = R.string.local_files),
                onClick = { songActivityFileLauncher.launch(null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
            )
        }
    }
}