package com.aaa.vibesmusic.player.mode.modes;

import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.player.mode.PlayModeCalc;

import java.util.List;

public class RepeatPlayMode implements PlayModeCalc {
    @Override
    public Song getNextSong(List<Song> songs, int current) {
        int nextIndex = (current + 1) % songs.size();
        return songs.get(nextIndex);
    }
}
