package com.aaa.vibesmusic;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aaa.vibesmusic.monetization.Ads;
import com.aaa.vibesmusic.player.MediaPlayerService;
import com.aaa.vibesmusic.preferences.PreferencesManager;
import com.aaa.vibesmusic.ui.activity.MainActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Objects;

public class VibesMusicApp extends Application implements Application.ActivityLifecycleCallbacks, ServiceConnection {
    private MediaPlayerService mediaPlayerService;
    private PreferencesManager manager;
    private final ViewTreeObserver.OnPreDrawListener waitListener;

    public VibesMusicApp() {
        super();
        this.mediaPlayerService = null;
        this.manager = null;
        this.waitListener = () -> false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.manager = new PreferencesManager(this);
        this.registerActivityLifecycleCallbacks(this);
        MobileAds.initialize(this, initializationStatus -> {});
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if(activity instanceof MainActivity) {
            if(!this.manager.isFirstAppUse()) {
                final View content = activity.findViewById(android.R.id.content);
                content.getViewTreeObserver().addOnPreDrawListener(this.waitListener);

                Ads.loadInterstitial(this.getApplicationContext(), Ads.OPEN_APP_AD_ID, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        content.getViewTreeObserver().removeOnPreDrawListener(waitListener);
                        content.getViewTreeObserver().addOnPreDrawListener(() -> true);
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                content.getViewTreeObserver().removeOnPreDrawListener(waitListener);
                                content.getViewTreeObserver().addOnPreDrawListener(() -> true);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                content.getViewTreeObserver().removeOnPreDrawListener(waitListener);
                                content.getViewTreeObserver().addOnPreDrawListener(() -> true);
                            }
                        });
                        interstitialAd.show(activity);
                    }
                });
            } else {
                this.manager.setIsFirstAppUse(false);
            }

            Intent serviceIntent = new Intent(this.getApplicationContext(), MediaPlayerService.class);
            this.bindService(serviceIntent, this, AppCompatActivity.BIND_AUTO_CREATE);
            this.startService(serviceIntent);
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {}

    @Override
    public void onActivityResumed(@NonNull Activity activity) {}

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if(activity instanceof MainActivity &&  Objects.nonNull(this.mediaPlayerService))
            this.unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MediaPlayerService.MediaPlayerServiceBinder binder = (MediaPlayerService.MediaPlayerServiceBinder) service;
        this.mediaPlayerService = binder.getMediaPlayerService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if(Objects.nonNull(this.mediaPlayerService)) {
            this.mediaPlayerService.onDestroy();
            this.mediaPlayerService = null;
        }
    }
}
