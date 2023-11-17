package com.aaa.vibesmusic.player.song;

import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.player.mode.PlayMode;
import com.aaa.vibesmusic.player.shuffle.ShuffleMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongPlayer {
    private int currentSongIndex;
    private int pauseTime;
    private PlayMode playMode;
    private ShuffleMode shuffleMode;
    private final List<Song> songs;
    private final List<Song> originalSongs;

    public SongPlayer() {
        this.currentSongIndex = 0;
        this.originalSongs = new ArrayList<>();
        this.songs = new ArrayList<>();
        this.setPlayMode(PlayMode.REPEAT);
        this.setShuffleMode(ShuffleMode.UNSHUFFLED);
    }

    /**
     *
     * @param songs The {@link List} of {@link Song}s managed by this {@link SongPlayer}
     */
    public SongPlayer(List<Song> songs) {
        this();
        this.originalSongs.addAll(songs);
        this.songs.addAll(songs);
        this.applyShuffleMode();
    }

    public synchronized void setSongs(List<Song> songs, int index) {
        this.setOriginalSongs(songs);
        this.songs.clear();
        this.songs.addAll(songs);
        Song song = this.songs.get(index);
        this.applyShuffleMode();
        this.currentSongIndex = this.findSong(song);
    }

    private void setOriginalSongs(List<Song> originalSongs) {
        this.originalSongs.clear();
        this.originalSongs.addAll(originalSongs);
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    public void setShuffleMode(ShuffleMode shuffleMode) {
        this.shuffleMode = shuffleMode;
        Song currentSong = null;
        if(!this.isEmpty())
            currentSong = this.songs.get(this.currentSongIndex);
        this.applyShuffleMode();
        if(!this.isEmpty())
            this.currentSongIndex = this.songs.indexOf(currentSong);
    }

    public void setPauseTime(int pauseTime) {
        this.pauseTime = pauseTime;
    }

    public int getPauseTime() {
        return this.pauseTime;
    }

    public boolean isEmpty() {
        return this.songs.isEmpty();
    }

    public List<Song> getSongs() {
        return this.songs;
    }

    public Song getCurrentSong() {
        return this.songs.get(this.currentSongIndex);
    }

    public Song getNextSong() {
        this.currentSongIndex = this.playMode.getCalc().getNextSong(this.songs, this.currentSongIndex);
        return this.songs.get(this.currentSongIndex);
    }

    private void applyShuffleMode() {
        switch (this.shuffleMode) {
            case SHUFFLED -> {
                Collections.shuffle(this.songs);
            }
            case UNSHUFFLED -> {
                this.songs.clear();
                this.songs.addAll(this.originalSongs);
            }
        }
    }

    private int findSong(Song song) {
        return this.songs.indexOf(song);
    }

    public void reset() {
        this.currentSongIndex = 0;
        this.playMode = PlayMode.REPEAT;
        this.shuffleMode = ShuffleMode.UNSHUFFLED;
        this.songs.clear();
        this.originalSongs.clear();
    }
}
