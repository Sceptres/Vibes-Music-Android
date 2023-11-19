package com.aaa.vibesmusic.player.services;

import com.aaa.vibesmusic.database.data.music.Song;

public interface Playable {
    /**
     * Start playing
     */
    void play();

    /**
     * Stop playing
     */
    void stop();

    /**
     * Pause playing
     */
    void pause();

    /**
     *
     * @return Where to resume playing
     */
    int resume();

    /**
     *
     * @return The next {@link Song} to play after a skip forward
     */
    Song skipForward();

    /**
     *
     * @return The next {@link Song} to play after a skip backward
     */
    Song skipBackward();
}
