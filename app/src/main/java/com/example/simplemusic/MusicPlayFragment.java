package com.example.simplemusic;

import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simplemusic.model.PlayState;
import com.example.simplemusic.util.ConstantUtil;

/**
 * Play music
 * @author WangXuan
 * @since 2015.09.03
 *
 */

public class MusicPlayFragment extends Fragment {
	
	public static final String EXTRA_CURRENT_MUSIC = "extracurrentmusic";
	public static final String EXTRA_CURRENT_PLAYLIST = "extracurrentplaylist";
	private static final String TAG = "MusicPlayFragment";
	ImageView mPlayButton;
	ImageView mPauseButton;
	ImageView mPrevButton;
	ImageView mNextButton;
	ImageView mDisk;
	SeekBar mSeekBar;
	TextView mDuration;
	TextView mElapsedDuration;
	int mDurationInt;
	int mMusicIndex;
	int mPlaylistIndex;

	OnClickListener mListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.btn_play:
				mBinder.play();
				break;
			case R.id.btn_pause:
				mBinder.pause();
				break;
			case R.id.btn_prev:
				mBinder.prev();
				break;
			case R.id.btn_next:
				mBinder.next();
				break;
			}
			
		}
	};
	
	private void updatePlayingControl(PlayState ps) {
		switch (ps) {
			case PLAY_STATE_PLAY:
				showRotateAnimation();
				mPauseButton.setVisibility(View.VISIBLE);
				mPlayButton.setVisibility(View.GONE);
				break;
			case PLAY_STATE_ERROR:
				hideRotateAnimation();
				Toast.makeText(getActivity(), R.string.msg_play_error, Toast.LENGTH_SHORT).show();
			case PLAY_STATE_PAUSE:
			case PLAY_STATE_RESET:
				mPauseButton.setVisibility(View.GONE);
				mPlayButton.setVisibility(View.VISIBLE);
				break;
		}
	}
	
	private MusicPlayService.MusicControlBinder mBinder;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBinder = (MusicPlayService.MusicControlBinder) service;
		}
	};
	
	public static MusicPlayFragment newInstance(int pindex, int mindex) {
		Bundle b = new Bundle();
		b.putInt(EXTRA_CURRENT_PLAYLIST, pindex);
		b.putInt(EXTRA_CURRENT_MUSIC, mindex);
		MusicPlayFragment fragment = new MusicPlayFragment();
		fragment.setArguments(b);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPlaylistIndex = getArguments().getInt(EXTRA_CURRENT_PLAYLIST);
		mMusicIndex = getArguments().getInt(EXTRA_CURRENT_MUSIC);
		Intent i = new Intent(getActivity(), MusicPlayService.class);
		i.putExtra(ConstantUtil.EXTRA_CURRENT_PLAYLIST, mPlaylistIndex);
		i.putExtra(ConstantUtil.EXTRA_CURRENT_MUSIC, mMusicIndex);
		getActivity().startService(i);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_musicplay, container, false);
		mPlayButton = (ImageView) v.findViewById(R.id.btn_play);
		mPauseButton = (ImageView) v.findViewById(R.id.btn_pause);
		mPrevButton = (ImageView) v.findViewById(R.id.btn_prev);
		mNextButton = (ImageView) v.findViewById(R.id.btn_next);		
		mDisk = (ImageView) v.findViewById(R.id.disk);
		mSeekBar = (SeekBar) v.findViewById(R.id.seekbar);
		mDuration = (TextView) v.findViewById(R.id.duration);
		mElapsedDuration = (TextView) v.findViewById(R.id.elapsed_duration);
		
		mPlayButton.setOnClickListener(mListener);
		mPauseButton.setOnClickListener(mListener);
		mPrevButton.setOnClickListener(mListener);
		mNextButton.setOnClickListener(mListener);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				mBinder.seek(arg0.getProgress());
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				
			}
		});
		
		Intent bi = new Intent(getActivity(), MusicPlayService.class);
		getActivity().bindService(bi, mConnection, 0);
		IntentFilter filter = new IntentFilter(ConstantUtil.INTENT_ACTION_MUSIC_CONTROL);
		getActivity().registerReceiver(mMusicControlReceiver, filter);
		
		return v;
	}

	
	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mMusicControlReceiver);
		getActivity().unbindService(mConnection);
		super.onDestroy();
	}


	private BroadcastReceiver mMusicControlReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.hasExtra(ConstantUtil.EXTRA_MUSIC_NAME))
				getActivity().getActionBar().setTitle(intent.getStringExtra(ConstantUtil.EXTRA_MUSIC_NAME));
			if(intent.hasExtra(ConstantUtil.EXTRA_MUSIC_DURATION)) {
				int duration = intent.getIntExtra(ConstantUtil.EXTRA_MUSIC_DURATION, 0);
				mDurationInt = duration;
				mDuration.setText(getStringDuration(duration));
				mElapsedDuration.setText(getStringDuration(0));
				mSeekBar.setMax(duration);
			}
			if(intent.hasExtra(ConstantUtil.EXTRA_MUSIC_SEEKBAR_POS)) {
				int elapsed = intent.getIntExtra(ConstantUtil.EXTRA_MUSIC_SEEKBAR_POS, 0);
				mSeekBar.setProgress(elapsed);
				mElapsedDuration.setText(getStringDuration(elapsed));
			}
			if(intent.hasExtra(ConstantUtil.EXTRA_PLAY_STATE)) {
				PlayState ps = PlayState.valueOf(intent.getStringExtra(ConstantUtil.EXTRA_PLAY_STATE));
				updatePlayingControl(ps);
			}
		}
		
	};

	private String getStringDuration(int duration) {
		duration /= 1000;
        int minute = duration / 60;
        int second = duration % 60;
        minute %= 60;
        return String.format(Locale.US, "%02d:%02d", minute, second);
	}

	private void showRotateAnimation() {
        hideRotateAnimation();
		Animation mRotateAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_rotate);
		mRotateAnim.setRepeatMode(Animation.RESTART);
		mRotateAnim.setRepeatCount(Animation.INFINITE);
        mDisk.startAnimation(mRotateAnim);
    }
	
	private void hideRotateAnimation() {
		mDisk.clearAnimation();
    }
}
