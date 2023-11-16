package com.aaa.vibesmusic.player.mode;

import com.aaa.vibesmusic.database.data.music.Song;

import java.util.List;

public interface PlayModeCalc {
    /**
     *
     * @param songs The {@link List} of {@link Song}s to get the next song from
     * @param current The index of the current {@link Song}
     * @return The next {@link Song} to play
     */
    Song getNextSong(List<Song> songs, int current);
}
