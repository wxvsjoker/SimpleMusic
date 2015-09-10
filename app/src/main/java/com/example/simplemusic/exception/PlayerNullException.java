package com.example.simplemusic.exception;

/**
 * Created by SAKURAISHO on 2015/9/10.
 */
public class PlayerNullException extends Exception {

    private static final String mMsg = "Player is NUll";

    public PlayerNullException() {
        super(mMsg);
    }

    public PlayerNullException(String detailMessage) {
        super(detailMessage);
    }
}
