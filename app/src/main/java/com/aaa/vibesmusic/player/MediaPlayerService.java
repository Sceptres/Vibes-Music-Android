package com.aaa.vibesmusic.player;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.aaa.vibesmusic.R;
import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.perms.PermissionsUtil;
import com.aaa.vibesmusic.player.mode.PlayMode;
import com.aaa.vibesmusic.player.notification.MediaControlNotification;
import com.aaa.vibesmusic.player.services.Playable;
import com.aaa.vibesmusic.player.services.SongsPlayedListener;
import com.aaa.vibesmusic.player.session.MediaSessionHolder;
import com.aaa.vibesmusic.player.shuffle.ShuffleMode;
import com.aaa.vibesmusic.player.song.SongPlayer;
import com.aaa.vibesmusic.ui.listener.OnPlaySeekListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.security.auth.Destroyable;

import io.reactivex.disposables.Disposable;

public class MediaPlayerService extends MediaBrowserServiceCompat implements MediaPlayer.OnCompletionListener,
MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
MediaPlayer.OnInfoListener, AudioManager.OnAudioFocusChangeListener, Playable, Destroyable, Disposable {

    private final BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Objects.nonNull(player) && isPlaying())
                pause();
        }
    };

    private final SongPlayer songPlayer;
    private final MediaPlayerServiceBinder binder;
    private MediaSessionHolder session;
    private MediaPlayer player;
    private AudioManager audioManager;
    private MediaTimeThread timeThread;
    private MediaControlNotification notification;
    private MediaPlayer.OnPreparedListener preparedListener;
    private SongsPlayedListener songsPlayedListener;

    /**
     *
     * @param context The {@link Context} to bind the service to
     * @param serviceConnection The {@link ServiceConnection} of this binding operation
     */
    public static void bindTo(Context context, ServiceConnection serviceConnection) {
        Intent serviceIntent = new Intent(context, MediaPlayerService.class);
        context.bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    public MediaPlayerService() {
        this.songPlayer = new SongPlayer();
        this.binder = new MediaPlayerServiceBinder();
        this.session = null;
        this.player = null;
        this.timeThread = null;
        this.notification = null;
        this.preparedListener = null;
        this.songsPlayedListener = null;
    }

    /**
     *
     * @param songs The {@link List} of {@link Song}s that will be played by the {@link MediaPlayer}
     */
    public void setSongs(List<Song> songs, int startIndex) {
        this.stop();
        this.player.reset();
        this.songPlayer.setSongs(songs, startIndex);
        Song currentSong = this.songPlayer.getCurrentSong();
        this.setSong(currentSong);
    }

    /**
     *
     * @param songs The {@link List} of {@link Song}s to update to
     */
    public void updateSongs(List<Song> songs) {
        boolean wasCurrentDeleted = this.songPlayer.updateSongs(songs);
        if(wasCurrentDeleted)
            this.setSong(this.getCurrentSong());
        this.runPreparedListener();
    }

    /**
     *
     * @param listener The {@link MediaPlayer.OnPreparedListener} to add to this {@link MediaPlayerService}
     */
    public void setPreparedListener(MediaPlayer.OnPreparedListener listener) {
        this.preparedListener = listener;
    }

    /**
     * Removes the {@link MediaPlayerService#preparedListener}
     */
    public void removePreparedListener() {
        this.preparedListener = null;
    }

    /**
     *
     * @param listener The {@link SongsPlayedListener} to add to the player
     */
    public void setSongsPlayedListener(SongsPlayedListener listener) {
        this.songsPlayedListener = listener;
    }

    /**
     * Remove the {@link SongsPlayedListener}
     */
    public void removeSongsPlayedListener() {
        this.songsPlayedListener = null;
    }

    /**
     *
     * @param seekListener The {@link OnPlaySeekListener} of this player
     */
    public void setOnSeekListener(OnPlaySeekListener seekListener) {
        if(Objects.nonNull(this.timeThread))
            this.timeThread.setOnPlaySeekListener(seekListener);
    }

    /**
     * Pause the {@link MediaTimeThread}
     */
    public void pauseSeekListener() {
        this.timeThread.pause();
    }

    /**
     * Resume the {@link MediaTimeThread}
     */
    public void resumeSeekListener() {
        this.timeThread.unpause();
    }

    /**
     *
     * @return The current position of the player in the song
     */
    public int getCurrentPosition() {
        return this.player.getCurrentPosition();
    }

    /**
     *
     * @return True if the player is currently playing a song. False otherwise
     */
    public boolean isPlaying() {
        return this.player.isPlaying();
    }

    /**
     *
     * @return The current {@link Song} being player
     */
    public Song getCurrentSong() {
        return this.songPlayer.getCurrentSong();
    }

    /**
     *
     * @return The {@link List} of {@link Song}s in this player
     */
    public List<Song> getSongs() {
        return new ArrayList<>(this.songPlayer.getSongs());
    }

    /**
     * Initialize the media player
     */
    private void initMediaPlayer() {
        this.player = new MediaPlayer();
        this.player.setOnCompletionListener(this);
        this.player.setOnErrorListener(this);
        this.player.setOnPreparedListener(this);
        this.player.setOnSeekCompleteListener(this);
        this.player.setOnInfoListener(this);

        this.player.reset();
        this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        this.timeThread = new MediaTimeThread(this.player);

        if(Objects.isNull(this.session)) {
            try {
                this.session = new MediaSessionHolder(this.getApplicationContext(), this);
                this.session.setActive(true);
                this.setSessionToken(this.session.getSessionToken());
            } catch (Exception ignored) {
                this.stop();
            }
        }

        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        this.registerReceiver(this.noisyReceiver, filter);
    }

    /**
     *
     * @return Get the {@link MediaMetadataCompat} of the {@link MediaSessionHolder}
     */
    public MediaMetadataCompat getMetadata() {
        return this.session.getMetadata();
    }

    /**
     *
     * @return The {@link MediaSessionCompat.Token} of the {@link MediaSessionHolder}
     */
    public MediaSessionCompat.Token getSessionToken() {
        return this.session.getSessionToken();
    }

    /**
     * Run {@link MediaPlayerService#preparedListener}
     */
    private void runPreparedListener() {
        if(Objects.nonNull(this.preparedListener))
            this.preparedListener.onPrepared(this.player);
    }

    /**
     *
     * @param song The {@link Song} to start playing
     */
    private void setSong(Song song) {
        try {
            this.stop();
            this.player.reset();
            this.player.setDataSource(song.getLocation());
            this.player.prepareAsync();
        } catch (IOException e) {
            this.stopSelf();
        }
    }

    /**
     *
     * @return True if the seeker thread is paused. False otherwise
     */
    public boolean isSeekerPaused() {
        return this.timeThread.isPaused();
    }

    /**
     *
     * @param time The time in milliseconds to seek to in the {@link Song}
     */
    public void seekTo(int time) {
        this.player.pause();
        this.timeThread.pause();
        this.player.seekTo(time);
        this.player.start();
        this.resumeSeekListener();
        if(Objects.nonNull(this.session))
            this.session.setMediaPlaybackState(this.session.getState(), time);
    }

    @Override
    public void play() {
        if(!this.player.isPlaying()) {
            this.player.start();
            if(!this.timeThread.isAlive()) {
                this.timeThread.start();
            } else {
                this.resumeSeekListener();
            }
            this.songPlayer.play();
            this.runPreparedListener();

            if(Objects.nonNull(this.notification) && !this.notification.isNotified())
                this.notification.show();
            if(Objects.nonNull(this.session))
                this.session.setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING, this.getCurrentPosition());
        }
    }

    @Override
    public void stop() {
        if(Objects.nonNull(this.player) && this.player.isPlaying()) {
            this.player.stop();
            this.songPlayer.stop();
            this.timeThread.pause();
            if(Objects.nonNull(this.session))
                this.session.setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED, this.getCurrentPosition());
            this.runPreparedListener();
        }
    }

    @Override
    public int resume() {
        if(!this.player.isPlaying()) {
            int resumeTime = this.songPlayer.resume();
            this.player.seekTo(resumeTime);
            this.player.start();
            this.resumeSeekListener();
            if(Objects.nonNull(this.session))
                this.session.setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING, this.getCurrentPosition());
            this.runPreparedListener();
            if(Objects.nonNull(this.notification))
                this.notification.updateNotification();
            return resumeTime;
        }
        return -1;
    }

    @Override
    public void pause() {
        if(this.player.isPlaying()) {
            this.player.pause();
            this.timeThread.pause();
            int pauseTime = this.getCurrentPosition();
            this.songPlayer.pause(pauseTime);
            if(Objects.nonNull(this.session))
                this.session.setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED, this.getCurrentPosition());
            this.runPreparedListener();
            if(Objects.nonNull(this.notification))
                this.notification.updateNotification();
        }
    }

    @Override
    public Song skipForward() {
        Song nextSong = this.songPlayer.skipForward();
        this.setSong(nextSong);
        return nextSong;
    }

    @Override
    public Song skipBackward() {
        Song nextSong = this.songPlayer.skipBackward();
        this.setSong(nextSong);
        return nextSong;
    }

    /**
     *
     * @param mode The new {@link PlayMode} of this player
     */
    public void setPlayMode(PlayMode mode) {
        this.songPlayer.setPlayMode(mode);
        this.runPreparedListener();
    }

    /**
     *
     * @return The current {@link PlayMode} of this player
     */
    public PlayMode getPlayMode() {
        return this.songPlayer.getPlayMode();
    }

    /**
     *
     * @param mode The new {@link ShuffleMode} of this player
     */
    public void setShuffleMode(ShuffleMode mode) {
        this.songPlayer.setShuffleMode(mode);
        this.runPreparedListener();
    }

    /**
     *
     * @return The current {@link ShuffleMode} of this player
     */
    public ShuffleMode getShuffleMode() {
        return this.songPlayer.getShuffleMode();
    }

    /**
     *
     * @return The current {@link PlayStatus} of this player
     */
    public PlayStatus getPlayStatus() {
        return this.songPlayer.getPlayStatus();
    }

    /**
     *
     * @return True if no {@link Song}s are currently in this player. False otherwise
     */
    public boolean isEmpty() {
        return this.songPlayer.isEmpty();
    }

    @Nullable
    @Override
    public MediaPlayerServiceBinder onBind(Intent intent) {
        return this.binder;
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if(TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }

        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    public void showNotification() {
        if(PermissionsUtil.hasPermission(this, Manifest.permission.POST_NOTIFICATIONS)) {
            this.notification = new MediaControlNotification(this.getApplicationContext(), this);
            this.notification.show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.requestAudioFocus())
            this.stopSelf();

        if(Objects.isNull(this.player))
            this.initMediaPlayer();

        MediaButtonReceiver.handleIntent(this.session.getSession(), intent);

        if(PermissionsUtil.hasPermission(this, Manifest.permission.POST_NOTIFICATIONS))
            this.notification = new MediaControlNotification(this.getApplicationContext(), this);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(!this.isEmpty()) {
            Song nextSong = this.songPlayer.getNextSong();
            this.setSong(nextSong);

            if(Objects.nonNull(this.songsPlayedListener))
                this.songsPlayedListener.onSongPlayed(this.songPlayer.getNumSongsPlayed());
            this.songPlayer.songCompleted();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ->
                    Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED ->
                    Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
            case MediaPlayer.MEDIA_ERROR_UNKNOWN ->
                    Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        this.play();

        Song currentSong = this.songPlayer.getCurrentSong();
        if(Objects.nonNull(this.session)) {
            this.session.setSong(currentSong);
            this.session.setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING, this.getCurrentPosition());
        }
        if(Objects.nonNull(this.notification))
            this.notification.updateNotification();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN -> {
                if (Objects.isNull(this.player))
                    this.initMediaPlayer();
                this.player.setVolume(1f, 1f);
                if(this.songPlayer.getPlayStatus() == PlayStatus.PAUSED)
                    this.resume();
                else
                    this.play();
            }
            case AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> this.pause();
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                    this.player.setVolume(0.1f, 0.1f);
        }
    }

    /**
     * Request audio focus from the OS
     * @return True if the service gained audio focus. False otherwise.
     */
    private boolean requestAudioFocus() {
        this.audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        int result = this.audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    /**
     * Releases the audio focus from this service, meaning that this service will not be able
     * to play any more audio until it requests it again.
     * @return True if we successfully released audio focus. False otherwise
     */
    private boolean releaseAudioFocus() {
        this.audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == this.audioManager.abandonAudioFocus(this);
    }

    /**
     * Cleans up and terminates this service
     */
    public void terminateSelf() {
        this.unregisterReceiver(this.noisyReceiver);
        this.dispose();
        if(Objects.nonNull(this.session)) {
            this.session.setActive(false);
            this.session.release();
            this.session = null;
        }
        if(Objects.nonNull(this.notification))
            this.notification.close();

        this.stopForeground(Service.STOP_FOREGROUND_REMOVE);
        this.stopSelf();
    }

    @Override
    public void dispose() {
        if(Objects.nonNull(this.player)) {
            this.removePreparedListener();
            this.timeThread.interrupt();
            this.stop();
            this.player.release();
            this.player = null;
            this.releaseAudioFocus();
        }
    }

    @Override
    public boolean isDisposed() {
        return Objects.isNull(this.player);
    }

    public class MediaPlayerServiceBinder extends Binder {
        /**
         *
         * @return The {@link MediaPlayerService}
         */
        public MediaPlayerService getMediaPlayerService() {
            return MediaPlayerService.this;
        }
    }
}
