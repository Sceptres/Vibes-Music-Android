package com.aaa.vibesmusic.player;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.player.mode.PlayMode;
import com.aaa.vibesmusic.player.services.Playable;
import com.aaa.vibesmusic.player.shuffle.ShuffleMode;
import com.aaa.vibesmusic.player.song.SongPlayer;
import com.aaa.vibesmusic.ui.listener.OnPlaySeekListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.security.auth.Destroyable;

import io.reactivex.disposables.Disposable;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
MediaPlayer.OnInfoListener, AudioManager.OnAudioFocusChangeListener, Playable, Destroyable, Disposable {

    private final SongPlayer songPlayer;
    private final MediaPlayerServiceBinder binder;
    private MediaPlayer player;
    private AudioManager audioManager;
    private MediaTimeThread timeThread;
    private final List<MediaPlayer.OnPreparedListener> preparedListeners;

    public MediaPlayerService() {
        this.songPlayer = new SongPlayer();
        this.binder = new MediaPlayerServiceBinder();
        this.player = null;
        this.timeThread = null;
        this.preparedListeners = new ArrayList<>();
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
     * @param listener The {@link MediaPlayer.OnPreparedListener} to add to this {@link MediaPlayerService}
     */
    public void addPreparedListener(MediaPlayer.OnPreparedListener listener) {
        this.preparedListeners.add(listener);
    }

    /**
     *
     * @param listener The {@link MediaPlayer.OnPreparedListener} to remove from this {@link MediaPlayerService}
     */
    public void removePreparedListener(MediaPlayer.OnPreparedListener listener) {
        this.preparedListeners.remove(listener);
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
     * @return The duration of the current {@link Song}
     */
    public int getCurrentSongDuration() {
        return this.player.getDuration();
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
    }

    /**
     *
     * @param song The {@link Song} to start playing
     */
    private void setSong(Song song) {
        try {
            this.player.reset();
            this.player.setDataSource(song.getLocation());
            this.player.prepareAsync();
        } catch (IOException e) {
            this.stopSelf();
        }
    }

    @Override
    public void play() {
        if(!this.player.isPlaying()) {
            this.player.start();
            if(!this.timeThread.isAlive())
                this.timeThread.start();
            this.songPlayer.play();
        }
    }

    @Override
    public void stop() {
        if(Objects.nonNull(this.player) && this.player.isPlaying()) {
            this.player.stop();
            this.songPlayer.stop();
            this.timeThread.interrupt();
        }
    }

    @Override
    public int resume() {
        if(!this.player.isPlaying()) {
            int resumeTime = this.songPlayer.resume();
            this.player.seekTo(resumeTime);
            this.player.start();
            this.timeThread.unpause();
            return resumeTime;
        }
        return -1;
    }

    @Override
    public void pause() {
        if(this.player.isPlaying()) {
            this.player.pause();
            this.timeThread.pause();
            int pauseTime = this.player.getCurrentPosition();
            this.songPlayer.pause(pauseTime);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.requestAudioFocus())
            this.stopSelf();

        if(Objects.isNull(this.player))
            this.initMediaPlayer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Song nextSong = this.songPlayer.getNextSong();
        this.setSong(nextSong);
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
        for(MediaPlayer.OnPreparedListener listener : this.preparedListeners)
            listener.onPrepared(mp);
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
                this.play();
            }
            case AudioManager.AUDIOFOCUS_LOSS -> this.dispose();
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> this.pause();
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

    @Override
    public void dispose() {
        if(Objects.nonNull(this.player)) {
            this.stop();
            this.player.release();
            this.player = null;
            this.timeThread.interrupt();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.dispose();
        this.stopSelf();
        this.releaseAudioFocus();
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
