package com.aaa.vibesmusic.ui.dialogs.common

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp

@Composable
fun EditFieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 15.sp,
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
                fontSize = 13.sp,
                modifier = Modifier
                    .wrapContentSize()
            )
        },
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorTextColor = Color.Red,
            errorContainerColor = Color.Transparent,
        ),
        isError = !validator
    )

    if(!validator)
        Text(text = validatorErrorMsg, color = Color.Red)
}