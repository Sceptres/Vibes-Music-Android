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

    /**
     *
     * @param songs The {@link List} of {@link Song}s to add to this player
     * @param index The index of the {@link Song} to start playing
     */
    public void setSongs(List<Song> songs, int index) {
        this.setOriginalSongs(songs);
        this.songs.clear();
        this.songs.addAll(songs);
        Song song = this.songs.get(index);
        this.applyShuffleMode();
        this.currentSongIndex = this.findSong(song);
    }

    /**
     *
     * @param originalSongs Setter for {@link SongPlayer#originalSongs}
     */
    private void setOriginalSongs(List<Song> originalSongs) {
        this.originalSongs.clear();
        this.originalSongs.addAll(originalSongs);
    }

    /**
     *
     * @param playMode Set the {@link PlayMode} of this player
     */
    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    /**
     *
     * @param shuffleMode Set the {@link ShuffleMode} of this player
     */
    public void setShuffleMode(ShuffleMode shuffleMode) {
        this.shuffleMode = shuffleMode;
        Song currentSong = null;
        if(!this.isEmpty())
            currentSong = this.songs.get(this.currentSongIndex);
        this.applyShuffleMode();
        if(!this.isEmpty())
            this.currentSongIndex = this.songs.indexOf(currentSong);
    }

    /**
     *
     * @param pauseTime Set the time at which the {@link Song} was paused
     */
    public void setPauseTime(int pauseTime) {
        this.pauseTime = pauseTime;
    }

    /**
     *
     * @return Get the time at which the {@link Song} was paused
     */
    public int getPauseTime() {
        return this.pauseTime;
    }

    /**
     *
     * @return True if this player has no songs. False otherwise.
     */
    public boolean isEmpty() {
        return this.songs.isEmpty();
    }

    /**
     *
     * @return The {@link List} of {@link Song}s in this player
     */
    public List<Song> getSongs() {
        return new ArrayList<>(this.songs);
    }

    /**
     *
     * @return The current {@link Song} being played
     */
    public Song getCurrentSong() {
        return this.songs.get(this.currentSongIndex);
    }

    /**
     *
     * @return Get the next {@link Song} to be played based on the {@link PlayMode} of this player
     */
    public Song getNextSong() {
        this.currentSongIndex = this.playMode.getCalc().getNextSong(this.songs, this.currentSongIndex);
        return this.songs.get(this.currentSongIndex);
    }

    /**
     * Apply the shuffle mode of the player to the {@link List} of {@link Songs}
     */
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

    /**
     *
     * @param song The {@link Song} to look for
     * @return The index of the {@link Song} in {@link SongPlayer#songs}
     */
    private int findSong(Song song) {
        return this.songs.indexOf(song);
    }

    /**
     * Empty the player and reset
     */
    public void reset() {
        this.currentSongIndex = 0;
        this.playMode = PlayMode.REPEAT;
        this.shuffleMode = ShuffleMode.UNSHUFFLED;
        this.songs.clear();
        this.originalSongs.clear();
    }
}
