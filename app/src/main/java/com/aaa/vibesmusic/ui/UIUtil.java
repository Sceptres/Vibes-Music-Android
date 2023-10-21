package com.aaa.vibesmusic.ui;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class UIUtil {
    /**
     *
     * @param context The {@link Context} to display the {@link Toast} on
     * @param msg The message the {@link Toast} should display
     */
    public static void showLongToast(@NonNull Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
