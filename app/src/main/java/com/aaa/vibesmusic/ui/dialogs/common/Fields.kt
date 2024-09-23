package com.aaa.vibesmusic.ui.dialogs.common

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun EditFieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        modifier = modifier
    )
}

@Composable
fun EditField(
    valueState: MutableState<String>,
    placeholderText: String,
    validator: Boolean,
    validatorErrorMsg: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = valueState.value,
        onValueChange = { newValue -> valueState.value = newValue },
        modifier = modifier,
        placeholder = {
            Text(
                text = placeholderText,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .wrapContentSize()
            )
        },
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorTextColor = Color.Red,
            errorContainerColor = Color.Transparent,
            selectionColors = TextSelectionColors(
                handleColor = Color.White,
                backgroundColor = Color.LightGray
            )
        ),
        isError = !validator
    )

    if(!validator)
        Text(
            text = validatorErrorMsg,
            color = Color.Red,
            style = MaterialTheme.typography.bodyMedium
        )
}