package com.aaa.vibesmusic.player.services;

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
}
