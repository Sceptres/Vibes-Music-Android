package com.aaa.vibesmusic.player.mode;

import com.aaa.vibesmusic.player.mode.modes.RepeatOnePlayMode;
import com.aaa.vibesmusic.player.mode.modes.RepeatPlayMode;

public enum PlayMode {
    REPEAT(new RepeatPlayMode()),
    REPEAT_ONE(new RepeatOnePlayMode());

    private final PlayModeCalc calc;

    /**
     *
     * @param calc The {@link PlayModeCalc} of this {@link PlayMode}
     */
    PlayMode(PlayModeCalc calc) {
        this.calc = calc;
    }

    /**
     *
     * @return The {@link PlayModeCalc} of this {@link PlayMode}
     */
    public PlayModeCalc getCalc() {
        return this.calc;
    }
}
