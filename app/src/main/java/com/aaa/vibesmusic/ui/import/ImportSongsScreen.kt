package com.aaa.vibesmusic.ui.import

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaa.vibesmusic.R

@Composable
fun ImportSongsScreen() {
    val backgroundColor = colorResource(id = R.color.background_color)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.import_music),
                color = Color.White,
                fontSize = 50.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(top = 5.dp)
            )

            Text(
                text = stringResource(id = R.string.local),
                color = Color.White,
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(top = 5.dp)
            )

            Button(
                onClick = { /* handle click */ },
                colors = ButtonColors(
                    containerColor = colorResource(id = R.color.foreground_color),
                    contentColor = Color.White,
                    disabledContainerColor = backgroundColor,
                    disabledContentColor = Color.Gray
                ),
                border = BorderStroke(1.dp, colorResource(id = R.color.blue_selected)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.folder_24),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = stringResource(id = R.string.local_files),
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .weight(1f)
                )
            }
        }
    }
}