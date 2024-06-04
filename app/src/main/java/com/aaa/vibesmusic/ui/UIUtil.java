package com.aaa.vibesmusic.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.aaa.vibesmusic.R;
import com.aaa.vibesmusic.ui.activity.MainActivity;
import com.aaa.vibesmusic.ui.viewgroup.PlaySongViewGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class UIUtil {
    public static final String PLAYLISTSONGS_KEY = "PLAYLISTSONGS";

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

    /**
     *
     * @param context The {@link Context} of the {@link View} that will contain the {@link PlaySongViewGroup}
     * @param playSongsView The {@link PlaySongViewGroup} instance to open
     * @param binding The {@link ViewBinding} of the {@link View} that will contain the {@link PlaySongViewGroup}
     * @param activity The {@link Activity} that will contain the {@link PlaySongViewGroup}
     */
    public static void openSongPlayer(Context context, PlaySongViewGroup playSongsView, ViewBinding binding, Activity activity) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        playSongsView.setOnCloseListener(() -> binding.getRoot().setVisibility(View.VISIBLE));
        playSongsView.startAnimation(animation);
        activity.addContentView(
                playSongsView,
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                )
        );
        binding.getRoot().postDelayed(() -> binding.getRoot().setVisibility(View.GONE), animation.getDuration());
    }

    /**
     *
     * @param activity The {@link Activity} to change whose status bar color will be changed
     * @param color The color resource to change the status bar to
     */
    public static void setStatusBarColor(@NonNull Activity activity, int color) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(color, null));
    }
}
