package com.aaa.vibesmusic.ui.services.songs;

import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.music.Song;

public interface SongMenuOperator {
    void operate(Song song, VibesMusicDatabase db);
}
