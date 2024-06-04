package com.aaa.vibesmusic.player;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ServiceUtil {
    /**
     *
     * @param context The {@link Context} that wants to start the {@link MediaPlayerService}
     * @param app The {@link Application} that will run the {@link MediaPlayerService}
     * @param connection The {@link ServiceConnection} to the {@link MediaPlayerService}
     */
    public static void connectMediaPlayerService(@NonNull Context context, @NonNull Application app, @NonNull ServiceConnection connection) {
        Intent serviceIntent = new Intent(context, MediaPlayerService.class);
        app.bindService(serviceIntent, connection, AppCompatActivity.BIND_AUTO_CREATE);
        app.startService(serviceIntent);
    }
}
