package com.aaa.vibesmusic.ui.monetization

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdmobBanner(adId: String, modifier: Modifier = Modifier) {
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