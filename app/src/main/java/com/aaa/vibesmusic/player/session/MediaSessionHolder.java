package com.aaa.vibesmusic.player.session;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.media.session.MediaButtonReceiver;

import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.player.MediaPlayerService;

import java.util.Objects;

public class MediaSessionHolder {
    private final MediaSessionCompat session;

    /**
     *
     * @param appContext The application {@link Context}
     * @param service The {@link MediaPlayerService}
     */
    public MediaSessionHolder(@NonNull Context appContext, MediaPlayerService service) {
        ComponentName componentName = new ComponentName(appContext, MediaButtonReceiver.class);
        this.session = new MediaSessionCompat(appContext, "MEDIA_SESSION", componentName, null);
        this.session.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                service.resume();
            }

            @Override
            public void onStop() {
                super.onStop();
                service.stop();
            }

            @Override
            public void onPause() {
                super.onPause();
                service.pause();
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                service.skipForward();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                service.skipBackward();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                service.seekTo((int) pos);
            }
        });
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(service, MediaButtonReceiver.class);
        PendingIntent mbrIntent = PendingIntent.getBroadcast(service, 0, mediaButtonIntent, PendingIntent.FLAG_IMMUTABLE);
        this.session.setMediaButtonReceiver(mbrIntent);
    }

    /**
     *
     * @return The {@link MediaSessionCompat.Token} of the {@link MediaSessionCompat}
     */
    public MediaSessionCompat.Token getSessionToken() {
        return this.session.getSessionToken();
    }

    /**
     *
     * @return The {@link MediaMetadataCompat} of the current {@link Song} from the {@link MediaSessionCompat}
     */
    public MediaMetadataCompat getMetadata() {
        return this.session.getController().getMetadata();
    }

    /**
     *
     * @param isActive Should we activate the {@link MediaSessionCompat}
     */
    public void setActive(boolean isActive) {
        this.session.setActive(isActive);
    }

    /**
     *
     * @param song The current playing {@link Song}
     */
    public void setSong(Song song) {
        String artUri = Objects.nonNull(song.getImageLocation()) ?
                song.getImageLocation() :
                "R.drawable.music_cover_image";
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getName());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, artUri);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.getDuration());
        this.session.setMetadata(metadataBuilder.build());
    }

    /**
     *
     * @param state The new state of the {@link MediaSessionCompat}
     */
    public void setMediaPlaybackState(int state) {
        long actions;
        PlaybackStateCompat.Builder playBackStateBuilder = new PlaybackStateCompat.Builder();
        if( state == PlaybackStateCompat.STATE_PLAYING )
            actions = PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE;
        else
            actions = PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY;
        actions |= PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SEEK_TO |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_SEEK_TO |
                PlaybackStateCompat.ACTION_STOP;
        playBackStateBuilder.setActions(actions);
        playBackStateBuilder.setState(state, 0, 1);
        this.session.setPlaybackState(playBackStateBuilder.build());
    }

    /**
     * Release the {@link MediaSessionCompat}
     */
    public void release() {
        this.setActive(false);
        this.session.release();
    }
}
