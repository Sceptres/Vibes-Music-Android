package com.aaa.vibesmusic.ui;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aaa.vibesmusic.ui.activity.MainActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class UIUtil {
    /**
     *
     * @param context The {@link Context} to display the {@link Toast} on
     * @param msg The message the {@link Toast} should display
     */
    public static void showLongToast(@NonNull Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     *
     * @param msg The message the {@link Snackbar} should show
     * @param backgroundColor The background color of the {@link Snackbar}
     */
    public static void showLongSnackBar(String msg, int backgroundColor) {
        Snackbar snackbar = Snackbar.make(
                Objects.requireNonNull(MainActivity.Companion.getSNACK_BAR_VIEW()),
                msg,
                Snackbar.LENGTH_LONG
        );
        snackbar.getView().setBackgroundColor(backgroundColor);
        snackbar.show();
    }
}
