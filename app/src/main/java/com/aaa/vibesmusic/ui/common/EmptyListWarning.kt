package com.aaa.vibesmusic.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.nav.navigateToImportMusicScreen

@Composable
fun EmptyListWarning(
    title: String,
    description: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick
                )
        ) {
            Icon(
                painter = icon,
                contentDescription = "Import Icon",
                tint = Color.Gray,
                modifier = Modifier.size(70.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                color = Color.Gray,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = description,
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun EmptySongsListWarning(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    EmptyListWarning(
        title = "Import Music",
        description = "There are no songs in the library. Click here to import music into the app!",
        icon = painterResource(id = R.drawable.ic_import),
        onClick = { navController.navigateToImportMusicScreen() },
        modifier = modifier
    )
}