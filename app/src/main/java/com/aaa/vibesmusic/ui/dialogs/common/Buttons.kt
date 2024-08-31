package com.aaa.vibesmusic.ui.dialogs.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class DialogButton(val btnTxt: String, val onClick: () -> Unit)

@Composable
fun DialogButtons(
    buttons: List<DialogButton>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        for(button in buttons) {
            Button(
                btn = button,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun Button(
    btn: DialogButton,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = btn.onClick,
        modifier = modifier,
    ) {
        Text(btn.btnTxt)
    }
}