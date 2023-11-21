package com.aaa.vibesmusic.player.services;

import com.aaa.vibesmusic.database.data.music.Song;

public interface SongsPlayedListener {
    /**
     *
     * @param numSongs Do something based on the number of {@link Song}s played
     */
    void onSongPlayed(int numSongs);
}
