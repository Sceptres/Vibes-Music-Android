package com.aaa.vibesmusic.ui.monetization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.aaa.vibesmusic.connection.ConnectionStatus
import com.aaa.vibesmusic.connection.rememberConnectionStatus
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdmobBanner(adId: String, modifier: Modifier = Modifier) {
    val connectionStatus: ConnectionStatus by rememberConnectionStatus()

    if(connectionStatus == ConnectionStatus.AVAILABLE) {
        AndroidView(
            factory = {
                AdView(it).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = adId
                    loadAd(AdRequest.Builder().build())
                }
            },
            modifier = modifier
        )
    }
}