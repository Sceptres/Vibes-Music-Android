package com.aaa.vibesmusic.ui.monetization

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.aaa.vibesmusic.BuildConfig
import com.aaa.vibesmusic.connection.ConnectionStatus
import com.aaa.vibesmusic.connection.currentConnectionStatus
import com.aaa.vibesmusic.connection.rememberConnectionStatus
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

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

class Ads {
    companion object {
        const val IMPORT_MUSIC_AD_ID: String = BuildConfig.ADMOB_IMPORT_SONGS_INTERESTRIAL_AD_ID
        const val OPEN_APP_AD_ID: String = BuildConfig.ADMOB_OPEN_INTERESTRIAL_AD_ID
        const val MUSIC_PLAYED_AD_ID: String = BuildConfig.ADMOB_PLAYED_SONGS_INTERESTRIAL_AD_ID

        /**
         *
         * @param context The [Context] to load the interstitial ad on
         * @param adId The AD ID of the interstitial ad to load.
         * @param callback The [InterstitialAdLoadCallback] to use when loading the add
         */
        fun loadInterstitial(
            context: Context,
            adId: String,
            onAdNotLoaded: () -> Unit,
            callback: InterstitialAdLoadCallback
        ) {
            if(context.currentConnectionStatus == ConnectionStatus.AVAILABLE) {
                val request = AdRequest.Builder().build()
                InterstitialAd.load(context, adId, request, callback)
            } else
                onAdNotLoaded()
        }
    }
}