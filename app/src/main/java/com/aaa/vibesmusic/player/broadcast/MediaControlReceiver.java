package com.aaa.vibesmusic.player.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.aaa.vibesmusic.player.MediaPlayerService;

public class MediaControlReceiver extends BroadcastReceiver {
    private final MediaPlayerService service;

    public MediaControlReceiver(MediaPlayerService service) {
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY -> this.service.resume();
                    case KeyEvent.KEYCODE_MEDIA_PAUSE -> this.service.pause();
                    case KeyEvent.KEYCODE_MEDIA_NEXT -> this.service.skipForward();
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS -> this.service.skipBackward();
                }
            }
        }
    }
}
