package com.aaa.vibesmusic.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.nav.Screens

@Composable
fun LibraryScreen(
    navController: NavController
) {
    Box(
       modifier = Modifier
           .fillMaxSize()
           .background(colorResource(id = R.color.background_color))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Library",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light,
                fontSize = 50.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(start = 10.dp, top = 5.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Music",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light,
                fontSize = 30.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(start = 10.dp, top = 5.dp)
            )

            LibraryButton(
                text = "Songs",
                icon = painterResource(id = R.drawable.music_note),
                onClick = {
                    navController.navigate(Screens.MUSIC_LIBRARY_PATH)
                },
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            LibraryButton(
                text = "Artists",
                icon = painterResource(id = R.drawable.person),
                onClick = {
                    navController.navigate(Screens.ARTISTS_PATH)
                },
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@Composable
private fun LibraryButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(5.dp),
        colors = ButtonColors(
            containerColor = colorResource(id = R.color.foreground_color),
            contentColor = Color.White,
            disabledContainerColor = colorResource(id = R.color.background_color),
            disabledContentColor = Color.Gray
        ),
        modifier = modifier
    ) {
        Icon(
            painter = icon,
            contentDescription = "$text Icon",
            tint = Color.White,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = text,
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