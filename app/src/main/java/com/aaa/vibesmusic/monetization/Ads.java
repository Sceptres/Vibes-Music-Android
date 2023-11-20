package com.aaa.vibesmusic.monetization;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class Ads {
    private static final String INTERSTITIAL_ID = "ca-app-pub-1417462071241776/1449546569";
    public static final long IMPORT_AD_TIME_DIFF = 600000; // 10 minutes

    /**
     *
     * @param adView The {@link AdView} to load the banner ad into
     * @param context The {@link Context} to initialize the ad on
     */
    public static void loadBanner(AdView adView, @NonNull Context context) {
        MobileAds.initialize(context);
        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);
    }

    /**
     *
     * @param context The {@link Context} to load the interstitial ad on
     * @param callback The {@link InterstitialAdLoadCallback} to use when loading the add
     */
    public static void loadInterstitial(@NonNull Context context, InterstitialAdLoadCallback callback) {
        AdRequest request = new AdRequest.Builder().build();
        InterstitialAd.load(context, Ads.INTERSTITIAL_ID, request, callback);
    }
}
