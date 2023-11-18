package com.aaa.vibesmusic.player;

import android.media.MediaPlayer;
import android.util.Log;

import com.aaa.vibesmusic.ui.listener.OnPlaySeekListener;

import java.util.Objects;

public class MediaTimeThread extends Thread {

    private final MediaPlayer player;
    private boolean paused;
    private OnPlaySeekListener listener;

    /**
     *
     * @param player The {@link MediaPlayer} to pass into the {@link OnPlaySeekListener}
     */
    public MediaTimeThread(MediaPlayer player) {
        this.player = player;
        this.paused = false;
        this.listener = null;
    }

    /**
     *
     * @param listener The new {@link OnPlaySeekListener} of this thread
     */
    public synchronized void setOnPlaySeekListener(OnPlaySeekListener listener) {
        this.listener = listener;
    }

    /**
     *
     * @return The {@link OnPlaySeekListener} of this thread
     */
    public synchronized OnPlaySeekListener getOnPlaySeekListener() {
        return this.listener;
    }

    /**
     * Pause the thread
     */
    public synchronized void pause() {
        this.paused = true;
    }

    /**
     * Unpause the thread
     */
    public synchronized void unpause() {
        this.paused = false;
        this.notify();
    }

    @Override
    public void run() {
        try {
            while (true) {
                    while (this.paused)
                        synchronized (this) {
                            this.wait();
                        }

                    if(Objects.nonNull(this.listener))
                        this.getOnPlaySeekListener().onPlaySeek(this.player);

                    Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Log.d("Media Time Thread", "INTERRUPTED");
        }
    }
}
