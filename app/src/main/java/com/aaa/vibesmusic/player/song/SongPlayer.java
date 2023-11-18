package com.aaa.vibesmusic.player.song;

import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.player.PlayStatus;
import com.aaa.vibesmusic.player.mode.PlayMode;
import com.aaa.vibesmusic.player.services.Playable;
import com.aaa.vibesmusic.player.shuffle.ShuffleMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SongPlayer implements Playable {
    private int currentSongIndex;
    private int pauseTime;
    private PlayStatus playStatus;
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

    private synchronized void setSongs(List<Song> songs) {
        this.songs.clear();
        this.songs.addAll(songs);
    }

    /**
     *
     * @param songs The {@link List} of {@link Song}s to add to this player
     * @param index The index of the {@link Song} to start playing
     */
    public synchronized void setSongs(List<Song> songs, int index) {
        this.setOriginalSongs(songs);
        this.setSongs(songs);
        Song song = this.songs.get(index);
        this.applyShuffleMode();
        this.currentSongIndex = this.findSong(song);
    }

    /**
     *
     * @param songs The new {@link List} of {@link Song}s to update to
     * @return True if the current song was deleted. False otherwise.
     */
    public synchronized boolean updateSongs(List<Song> songs) {
        boolean wasCurrentDeleted = false;

        this.setOriginalSongs(songs);
        List<Song> deletedSongs = this.songs.stream().filter(s -> !this.originalSongs.contains(s)).collect(Collectors.toList());

        for(Song deletedSong : deletedSongs) {
            if(deletedSong.equals(this.getCurrentSong()))
                wasCurrentDeleted = true;
            this.songs.remove(deletedSong);
        }

        for(int i=0; i < this.songs.size(); i++) {
            Song currentSong = this.songs.get(i);
            int songIndex = this.originalSongs.indexOf(currentSong);
            Song newSong = this.originalSongs.get(songIndex);

            if(!Song.isSameSong(currentSong, newSong)) {
                this.songs.remove(i);
                this.songs.add(i, newSong);
            }
        }

        return wasCurrentDeleted;
    }

    /**
     *
     * @return The current {@link PlayStatus} of this {@link SongPlayer}
     */
    public synchronized PlayStatus getPlayStatus() {
        return this.playStatus;
    }

    /**
     *
     * @param originalSongs Setter for {@link SongPlayer#originalSongs}
     */
    private synchronized void setOriginalSongs(List<Song> originalSongs) {
        this.originalSongs.clear();
        this.originalSongs.addAll(originalSongs);
    }

    /**
     *
     * @param playMode Set the {@link PlayMode} of this player
     */
    public synchronized void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    /**
     *
     * @return The {@link PlayMode} of this player
     */
    public synchronized PlayMode getPlayMode() {
        return this.playMode;
    }

    /**
     *
     * @param shuffleMode Set the {@link ShuffleMode} of this player
     */
    public synchronized void setShuffleMode(ShuffleMode shuffleMode) {
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
     * @return The {@link ShuffleMode} of the player
     */
    public synchronized ShuffleMode getShuffleMode() {
        return this.shuffleMode;
    }

    @Override
    public synchronized void play() {
        this.playStatus = PlayStatus.PLAYING;
    }

    @Override
    public synchronized void stop() {
        this.pause();
    }

    @Override
    public void pause() {
        this.playStatus = PlayStatus.PAUSED;
    }

    /**
     *
     * @param pauseTime Set the time at which the {@link Song} was paused
     */
    public synchronized void pause(int pauseTime) {
        this.pauseTime = pauseTime;
        this.pause();
    }

    @Override
    public synchronized int resume() {
        this.play();
        return this.pauseTime;
    }

    @Override
    public synchronized Song skipForward() {
        this.currentSongIndex = (this.currentSongIndex+1) % this.songs.size();
        return this.songs.get(this.currentSongIndex);
    }

    @Override
    public synchronized Song skipBackward() {
        int nextIndex = this.currentSongIndex - 1;

        if(nextIndex > -1)
            this.currentSongIndex = nextIndex;
        else
            this.currentSongIndex = this.songs.size() + nextIndex;

        return this.songs.get(this.currentSongIndex);
    }

    /**
     *
     * @return True if this player has no songs. False otherwise.
     */
    public synchronized boolean isEmpty() {
        return this.songs.isEmpty();
    }

    /**
     *
     * @return The {@link List} of {@link Song}s in this player
     */
    public synchronized List<Song> getSongs() {
        return new ArrayList<>(this.songs);
    }

    /**
     *
     * @return The current {@link Song} being played
     */
    public synchronized Song getCurrentSong() {
        return this.songs.get(this.currentSongIndex);
    }

    /**
     *
     * @return Get the next {@link Song} to be played based on the {@link PlayMode} of this player
     */
    public synchronized Song getNextSong() {
        this.currentSongIndex = this.playMode.getCalc().getNextSong(this.songs, this.currentSongIndex);
        return this.songs.get(this.currentSongIndex);
    }

    /**
     * Apply the shuffle mode of the player to the {@link List} of {@link Song}s
     */
    private synchronized void applyShuffleMode() {
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
    public synchronized void reset() {
        this.currentSongIndex = 0;
        this.playMode = PlayMode.REPEAT;
        this.shuffleMode = ShuffleMode.UNSHUFFLED;
        this.songs.clear();
        this.originalSongs.clear();
    }
}
