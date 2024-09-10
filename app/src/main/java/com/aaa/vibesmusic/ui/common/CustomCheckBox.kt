package com.aaa.vibesmusic.ui.common

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.aaa.vibesmusic.R

@Composable
fun CustomCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors().copy(
            checkedBorderColor = Color.White,
            uncheckedBorderColor = Color.White,
            checkedBoxColor = colorResource(R.color.blue_selected)
        ),
        modifier = modifier
            .wrapContentSize()
    )
}