package com.aaa.vibesmusic.ui.dialogs.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.aaa.vibesmusic.R

@Composable
fun ConfirmAlertDialog(
    title: String,
    text: String,
    iconTint: Color = Color.White,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    onDismiss: () -> Unit = {},
    dismissButtonText: String = "Dismiss",
    onConfirm: () -> Unit = {},
    confirmButtonText: String = "Confirm"
) {
    AlertDialog(
        containerColor = backgroundColor,
        icon = {
            Icon(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "App launcher icon",
                tint = iconTint
            )
        },
        title = {
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp
            )
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.outline,
                    disabledContentColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified
                )
            ) {
                Text(
                    text = dismissButtonText
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.outline,
                    disabledContentColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified
                )
            ) {
                Text(
                    text = confirmButtonText
                )
            }
        }
    )
}