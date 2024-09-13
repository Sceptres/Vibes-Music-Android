package com.aaa.vibesmusic

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.ViewTreeObserver
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.player.MediaPlayerService.MediaPlayerServiceBinder
import com.aaa.vibesmusic.player.services.SongsPlayedListener
import com.aaa.vibesmusic.preferences.PreferencesManager
import com.aaa.vibesmusic.ui.activity.MainActivity
import com.aaa.vibesmusic.ui.monetization.Ads
import com.aaa.vibesmusic.ui.monetization.Ads.Companion.loadInterstitial
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.Objects

class VibesMusicApp : Application(), ActivityLifecycleCallbacks, ServiceConnection,
    SongsPlayedListener {
    private var mediaPlayerService: MediaPlayerService? = null
    private var shouldLoadAd = false
    private var isAppInBackground = false
    private lateinit var currentActivity: Activity
    private lateinit var manager: PreferencesManager
    private val waitListener = ViewTreeObserver.OnPreDrawListener { false }

    override fun onCreate() {
        super.onCreate()
        this.manager = PreferencesManager(this)
        this.registerActivityLifecycleCallbacks(this)
        MobileAds.initialize(this)

        this.manager.incrementNumSessions()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is MainActivity) {
            this.currentActivity = activity
            if (!this.manager.isFirstAppUse) {
                val content = activity.findViewById<View>(android.R.id.content)
                content.viewTreeObserver.addOnPreDrawListener(this.waitListener)

                loadInterstitial(
                    this.applicationContext,
                    Ads.OPEN_APP_AD_ID,
                    {
                        content.viewTreeObserver.removeOnPreDrawListener(waitListener)
                        content.viewTreeObserver.addOnPreDrawListener { true }
                    },
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            super.onAdLoaded(interstitialAd)
                            interstitialAd.show(activity)
                            content.viewTreeObserver.removeOnPreDrawListener(waitListener)
                            content.viewTreeObserver.addOnPreDrawListener { true }
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            super.onAdFailedToLoad(loadAdError)
                            content.viewTreeObserver.removeOnPreDrawListener(waitListener)
                            content.viewTreeObserver.addOnPreDrawListener { true }
                        }
                    })
            } else {
                activity.requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PermissionsUtil.POST_NOTIF_CODE
                )
                this.manager.setIsFirstAppUse(false)
            }

            if (Objects.isNull(this.mediaPlayerService)) {
                val serviceIntent = Intent(
                    this.applicationContext,
                    MediaPlayerService::class.java
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.startForegroundService(serviceIntent)
                } else {
                    this.startService(serviceIntent)
                }

                this.bindService(serviceIntent, this, BIND_AUTO_CREATE)
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        if (this.shouldLoadAd) this.loadMusicPlayedAd(activity)
        this.isAppInBackground = false
    }

    override fun onActivityPaused(activity: Activity) {
        this.isAppInBackground = true
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity is MainActivity && Objects.nonNull(this.mediaPlayerService)) {
            mediaPlayerService?.terminateSelf()
            this.mediaPlayerService = null
        }
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val binder = service as MediaPlayerServiceBinder
        this.mediaPlayerService = binder.mediaPlayerService
        this.mediaPlayerService?.setSongsPlayedListener(this)
    }

    override fun onServiceDisconnected(name: ComponentName) {}

    override fun onSongPlayed(numSongs: Int) {
        if (numSongs != 0 && numSongs % 20 == 0) this.shouldLoadAd = true

        if (!this.isAppInBackground && this.shouldLoadAd) this.loadMusicPlayedAd(
            this.currentActivity
        )
    }

    /**
     *
     * @param activity The [Activity] to load the ad on
     */
    private fun loadMusicPlayedAd(activity: Activity) {
        shouldLoadAd = false
        loadInterstitial(
            this.applicationContext,
            Ads.MUSIC_PLAYED_AD_ID,
            {},
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    interstitialAd.show(activity)
                    shouldLoadAd = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    shouldLoadAd = true
                }
            })
    }
}