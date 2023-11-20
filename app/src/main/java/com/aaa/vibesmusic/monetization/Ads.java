package com.aaa.vibesmusic.monetization;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class Ads {
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
}
