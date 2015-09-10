package com.example.simplemusic.model;

import java.util.ArrayList;

/**
 * Observer to update playing music index
 * @author SAKURAISHO
 * @since 2015.09.06
 *
 */
public class CurrentIndexObserver {

	public static CurrentIndexObserver sObserver;
	private ArrayList<CurrentIndexListener> mListeners;
	private int mPlaylistIndex;
	private int mMusicIndex;
	
	private CurrentIndexObserver () {
		mListeners = new ArrayList<CurrentIndexListener>();
		mPlaylistIndex = -2;
		mMusicIndex = -1;
	}
	
	public static CurrentIndexObserver getInstance() {
		if(sObserver == null) {
			sObserver = new CurrentIndexObserver();
		}
		return sObserver;
	}
	
	public void registerListener(CurrentIndexListener listener) {
		if(!mListeners.contains(listener))
			mListeners.add(listener);
	}
	
	public void unregisterListener(CurrentIndexListener listener) {
		if(mListeners.contains(listener))
			mListeners.remove(listener);
	}
	
	private void notifyToAll() {
		for(CurrentIndexListener listener : mListeners) {
			listener.updateCurrentIndex(mPlaylistIndex, mMusicIndex);
		}
	}
	
	public void deletePlaylistIndex(int pi) {
		if(pi < mPlaylistIndex) {
			mPlaylistIndex --;
			notifyToAll();
		}
	}
	
	public void deleteMusicIndex(int pi, int mi) {
		if(pi == mPlaylistIndex && mi < mMusicIndex) {
			mMusicIndex --;
			notifyToAll();
		}
	}
	
	public void updatePlayIndex(int pi, int mi) {
		mPlaylistIndex = pi;
		mMusicIndex = mi;
		notifyToAll();
	}
	
	public void unregisterListeners() {
		mListeners = null;
		sObserver = null;
	}
	
	public interface CurrentIndexListener {
		public void updateCurrentIndex(int pi, int mi);
	}
	
	public int getMusicIndex() {
		return mMusicIndex;
	}
	
	public int getPlaylistIndex() {
		return mPlaylistIndex;
	}
	
}
