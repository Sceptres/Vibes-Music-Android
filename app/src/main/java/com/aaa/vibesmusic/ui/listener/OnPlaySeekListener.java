package com.aaa.vibesmusic.ui.listener;

import android.media.MediaPlayer;

public interface OnPlaySeekListener {
    /**
     *
     * @param player The {@link MediaPlayer} to get play time
     */
    void onPlaySeek(MediaPlayer player);
}
