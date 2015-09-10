package com.example.simplemusic;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.simplemusic.MusicPlayer.PlayOnCompleteListener;
import com.example.simplemusic.base.BaseApplication;
import com.example.simplemusic.exception.PlaylistNullException;
import com.example.simplemusic.model.CurrentIndexObserver.CurrentIndexListener;
import com.example.simplemusic.model.Music;
import com.example.simplemusic.model.PlayState;
import com.example.simplemusic.model.Playlist;
import com.example.simplemusic.util.ConstantUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Music play control
 * @author SAKURAISHO
 * @since 2015.09.04
 *
 */
public class MusicPlayService extends Service {
	
	private static final String TAG = "MusicPlayService";
	
	int mMusicIndex;
	int mPlaylistIndex;
	PlayState mPlayState;
	MusicPlayer mPlayer;
	Music mMusic;
	Timer mTimer;
	TimerTask mTimerTask;
	MusicControlBinder mBinder = new MusicControlBinder();
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mMusicIndex = -1;
		mPlaylistIndex = -1;
		mPlayState = PlayState.PLAY_STATE_RESET;
		mPlayer = new MusicPlayer(this);
		BaseApplication.getCurrentIndexObserver().registerListener(mListener);
		Log.d(TAG, "Service onCreate");
	}

	@Override
	public void onDestroy() {
		mPlayer.release();
		removeListener();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Service onStartCommand");
		int mi = intent.getIntExtra(ConstantUtil.EXTRA_CURRENT_MUSIC, mMusicIndex);
		int pi = intent.getIntExtra(ConstantUtil.EXTRA_CURRENT_PLAYLIST, mPlaylistIndex);
		if(mi != mMusicIndex || pi != mPlaylistIndex) {
			playNewMusic(pi, mi);
		} else if(mPlayer.isCanPlay()){
			sendMusicInfoBroadCast();
			startSeekBarTask();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void startSeekBarTask() {
		stopSeekBarTask();
		if(mPlayState == PlayState.PLAY_STATE_PLAY) {
			mTimer = new Timer();
			mTimerTask = new TimerTask() {

				@Override
				public void run() {
					sendSeekBarBroadCast();
				}
			};
			mTimer.schedule(mTimerTask, 0, 1000);
		}
	}

	private void stopSeekBarTask() {
		if(mTimer != null && mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
			mTimer = null;
		}
	}

	private void sendMusicInfoBroadCast() {
		Intent intent = new Intent(ConstantUtil.INTENT_ACTION_MUSIC_CONTROL);
		intent.putExtra(ConstantUtil.EXTRA_MUSIC_NAME, mMusic.getTitle());
		intent.putExtra(ConstantUtil.EXTRA_MUSIC_DURATION, mMusic.getDuration());
		intent.putExtra(ConstantUtil.EXTRA_MUSIC_SEEKBAR_POS, mPlayer.getPosition());
		intent.putExtra(ConstantUtil.EXTRA_PLAY_STATE, mPlayState.toString());
		sendBroadcast(intent);
	}

	private void sendPlayControlBroadCast() {
		Intent intent = new Intent(ConstantUtil.INTENT_ACTION_MUSIC_CONTROL);
		intent.putExtra(ConstantUtil.EXTRA_PLAY_STATE, mPlayState.toString());
		sendBroadcast(intent);
	}
	
	private void sendSeekBarBroadCast() {
		Intent intent = new Intent(ConstantUtil.INTENT_ACTION_MUSIC_CONTROL);
		intent.putExtra(ConstantUtil.EXTRA_MUSIC_SEEKBAR_POS, mPlayer.getPosition());
		sendBroadcast(intent);
	}

	private Music getMusicFromPlaylist(int pi, int mi) throws PlaylistNullException {

		mPlaylistIndex = pi;
		if (pi == ConstantUtil.PLAYLIST_NONE) {
			int count = BaseApplication.getMusicCollection().getCount();
			if (count == 0) throw new PlaylistNullException();
			mMusicIndex = (mi + count) % count;
			return BaseApplication.getMusicCollection().getMusics().get(mMusicIndex);
		}

		Playlist p = BaseApplication.getPlaylistCollection().getPlaylist(pi);
		if (p.getCount() == 0) throw new PlaylistNullException();
		mMusicIndex = (mi + p.getCount()) % p.getCount();
		return BaseApplication.getMusicCollection().getMusics(p).get(mMusicIndex);
	}
	
	private void playNewMusic(int pi, int mi) {
		try {
			Music m = getMusicFromPlaylist(pi, mi);
			mMusic = m;
			mPlayState = mPlayer.play(m);
			sendMusicInfoBroadCast();
			startSeekBarTask();
			mPlayer.setOnCompleteListener(new PlayOnCompleteListener() {

				@Override
				public void onComplete() {
					playNewMusic(mPlaylistIndex, ++mMusicIndex);
				}
			});
			notifyToPlaylist();
		} catch (PlaylistNullException e) {
			e.printStackTrace();
			mPlayer.release();
			mPlayState = PlayState.PLAY_STATE_ERROR;
		}
	}

	CurrentIndexListener mListener = new CurrentIndexListener() {
		
		@Override
		public void updateCurrentIndex(int pi, int mi) {
			mPlaylistIndex = pi;
			mMusicIndex = mi;
		}
	};

	private void removeListener() {
		BaseApplication.getCurrentIndexObserver().unregisterListener(mListener);
	}
	
	private void notifyToPlaylist() {
		BaseApplication.getCurrentIndexObserver().updatePlayIndex(mPlaylistIndex, mMusicIndex);
	}
	
	class MusicControlBinder extends Binder {
		public void play() {
			mPlayState = mPlayer.play();
			sendPlayControlBroadCast();
			startSeekBarTask();
		}
		
		public void pause() {
			mPlayState = mPlayer.pause();
			sendPlayControlBroadCast();
			stopSeekBarTask();
		}
		
		public void prev() {
			playNewMusic(mPlaylistIndex, --mMusicIndex);
		}
		
		public void next() {
			playNewMusic(mPlaylistIndex, ++mMusicIndex);
		}
		
		public void seek(int pos) {
			mPlayer.seek(pos);
			sendSeekBarBroadCast();
		}
	} 
	
}
