package com.example.simplemusic.model;

/**
 * Created by SAKURAISHO on 2015/9/10.
 */
public enum PlayState {
    PLAY_STATE_RESET(-1),
    PLAY_STATE_PLAY(0),
    PLAY_STATE_PAUSE(1),
    PLAY_STATE_ERROR(2);

    private int mValue;

    PlayState(int value) {
        mValue = value;
    }

}
