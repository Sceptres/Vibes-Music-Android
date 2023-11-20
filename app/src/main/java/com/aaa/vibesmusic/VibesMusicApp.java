package com.aaa.vibesmusic;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aaa.vibesmusic.player.MediaPlayerService;

import java.util.Objects;

public class VibesMusicApp extends Application implements Application.ActivityLifecycleCallbacks, ServiceConnection {
    private MediaPlayerService mediaPlayerService;

    public VibesMusicApp() {
        super();
        this.mediaPlayerService = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Intent serviceIntent = new Intent(this.getApplicationContext(), MediaPlayerService.class);
        this.bindService(serviceIntent, this, AppCompatActivity.BIND_AUTO_CREATE);
        this.startService(serviceIntent);
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
        if(Objects.nonNull(this.mediaPlayerService))
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
