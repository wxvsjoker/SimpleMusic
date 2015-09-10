package com.example.simplemusic.base;

import android.app.Application;

import com.example.simplemusic.model.CurrentIndexObserver;
import com.example.simplemusic.model.MusicCollection;
import com.example.simplemusic.model.PlaylistCollection;

public class BaseApplication extends Application {
	
	private static PlaylistCollection sPlaylistCollection;
	private static MusicCollection sMusicCollection;
	private static CurrentIndexObserver sObserver;
	
	@Override
	public void onCreate() {
		sPlaylistCollection = PlaylistCollection.getInstance(getApplicationContext());
		sMusicCollection = MusicCollection.getInstance(getApplicationContext());
		sObserver = CurrentIndexObserver.getInstance();
		super.onCreate();
	}

	public static PlaylistCollection getPlaylistCollection() {
		return sPlaylistCollection;
	}
	
	public static MusicCollection getMusicCollection() {
		return sMusicCollection;
	}
	
	public static CurrentIndexObserver getCurrentIndexObserver() {
		return sObserver;
	}
	
}
