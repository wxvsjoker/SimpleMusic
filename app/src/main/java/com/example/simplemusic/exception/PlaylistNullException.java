package com.example.simplemusic.exception;

/**
 * Created by SAKURAISHO on 2015/9/10.
 */
public class PlaylistNullException extends Exception {

    private static final String mMsg = "Playlist is null";

    public PlaylistNullException() {
        super(mMsg);
    }

    public PlaylistNullException(String detailMessage) {
        super(detailMessage);
    }
}
