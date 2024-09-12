package com.aaa.vibesmusic;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aaa.vibesmusic.monetization.Ads;
import com.aaa.vibesmusic.perms.PermissionsUtil;
import com.aaa.vibesmusic.player.MediaPlayerService;
import com.aaa.vibesmusic.player.services.SongsPlayedListener;
import com.aaa.vibesmusic.preferences.PreferencesManager;
import com.aaa.vibesmusic.ui.activity.MainActivity;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Objects;

public class VibesMusicApp extends Application implements Application.ActivityLifecycleCallbacks, ServiceConnection,
        SongsPlayedListener {
    private MediaPlayerService mediaPlayerService;
    private boolean shouldLoadAd;
    private boolean isAppInBackground;
    private Activity currentActivity;
    private PreferencesManager manager;
    private final ViewTreeObserver.OnPreDrawListener waitListener;

    public VibesMusicApp() {
        super();
        this.mediaPlayerService = null;
        this.shouldLoadAd = false;
        this.currentActivity = null;
        this.isAppInBackground = false;
        this.manager = null;
        this.waitListener = () -> false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.manager = new PreferencesManager(this);
        this.registerActivityLifecycleCallbacks(this);
        MobileAds.initialize(this, initializationStatus -> {});

        this.manager.incrementNumSessions();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if(activity instanceof MainActivity) {
            this.currentActivity = activity;
            if(!this.manager.isFirstAppUse()) {
                final View content = activity.findViewById(android.R.id.content);
                content.getViewTreeObserver().addOnPreDrawListener(this.waitListener);

                Ads.loadInterstitial(this.getApplicationContext(), Ads.OPEN_APP_AD_ID, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        interstitialAd.show(activity);
                        content.getViewTreeObserver().removeOnPreDrawListener(waitListener);
                        content.getViewTreeObserver().addOnPreDrawListener(() -> true);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        content.getViewTreeObserver().removeOnPreDrawListener(waitListener);
                        content.getViewTreeObserver().addOnPreDrawListener(() -> true);
                    }
                });
            } else {
                activity.requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, PermissionsUtil.POST_NOTIF_CODE);
                this.manager.setIsFirstAppUse(false);
            }

            if(Objects.isNull(this.mediaPlayerService)) {
                Intent serviceIntent = new Intent(this.getApplicationContext(), MediaPlayerService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.startForegroundService(serviceIntent);
                } else {
                    this.startService(serviceIntent);
                }

                this.bindService(serviceIntent, this, AppCompatActivity.BIND_AUTO_CREATE);
            }
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {}

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if(this.shouldLoadAd)
            this.loadMusicPlayedAd(activity);
        this.isAppInBackground = false;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        this.isAppInBackground = true;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if(activity instanceof MainActivity &&  Objects.nonNull(this.mediaPlayerService)) {
            this.mediaPlayerService.terminateSelf();
            this.mediaPlayerService = null;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MediaPlayerService.MediaPlayerServiceBinder binder = (MediaPlayerService.MediaPlayerServiceBinder) service;
        this.mediaPlayerService = binder.getMediaPlayerService();
        this.mediaPlayerService.setSongsPlayedListener(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {}

    @Override
    public void onSongPlayed(int numSongs) {
        if(numSongs != 0 && numSongs % 20 == 0)
            this.shouldLoadAd = true;

        if(!this.isAppInBackground && this.shouldLoadAd)
            this.loadMusicPlayedAd(currentActivity);
    }

    /**
     *
     * @param activity The {@link Activity} to load the ad on
     */
    private void loadMusicPlayedAd(Activity activity) {
        shouldLoadAd = false;
        Ads.loadInterstitial(this.getApplicationContext(), Ads.MUSIC_PLAYED_AD_ID, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                interstitialAd.show(activity);
                shouldLoadAd = false;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                shouldLoadAd = true;
            }
        });
    }
}
