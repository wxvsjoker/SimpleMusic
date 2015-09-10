package com.example.simplemusic;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;

import com.example.simplemusic.model.Music;
import com.example.simplemusic.model.PlayState;

public class MusicPlayer {
	private static final String TAG = "MusicPlayer";

	MediaPlayer mPlayer;
    PlayState mPlayState;
	Context mContext;
	
	public MusicPlayer(Context context) {
		mContext = context;
		mPlayState = PlayState.PLAY_STATE_RESET;
	}
	
	public PlayState pause() {
		if(mPlayer != null && mPlayer.isPlaying()) {
			mPlayer.pause();
            mPlayState = PlayState.PLAY_STATE_PAUSE;
		}
        return mPlayState;
	}

	public PlayState play() {
		if(mPlayer != null) {
            mPlayer.start();
            mPlayState = PlayState.PLAY_STATE_PLAY;
        }
        return mPlayState;
	}

	public PlayState play(Music m) {
		release();
		mPlayer = MediaPlayer.create(mContext, Uri.parse(m.getPath()));
		if(mPlayer == null) {
			mPlayState = PlayState.PLAY_STATE_ERROR;
		} else {
			mPlayer.start();
			mPlayState = PlayState.PLAY_STATE_PLAY;
		}
		return mPlayState;
	}
	
	public void seek(int pos) {
		if(mPlayer != null)
			mPlayer.seekTo(pos);
	}
	
	public void release() {
		if(mPlayer != null) {
			mPlayer.reset();
			mPlayer.release();
			mPlayer = null;
		}
        mPlayState = PlayState.PLAY_STATE_RESET;
	}

	public int getPosition() {
		if(mPlayer != null)
			return mPlayer.getCurrentPosition();
		return 0;
	}
	
	public boolean isCanPlay() {
		return mPlayState == PlayState.PLAY_STATE_PLAY || mPlayState == PlayState.PLAY_STATE_PAUSE;
	}
	
	public interface PlayOnCompleteListener {
		public void onComplete();
	}
	
	PlayOnCompleteListener mPlayOnCompleteListener;
	public void setOnCompleteListener(PlayOnCompleteListener l) {
		mPlayOnCompleteListener = l;
		mPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer arg0) {
				mPlayOnCompleteListener.onComplete();
			}
		});
	}
}
