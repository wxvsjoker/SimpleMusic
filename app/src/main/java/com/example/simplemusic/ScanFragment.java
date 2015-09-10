package com.example.simplemusic;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.simplemusic.base.BaseApplication;
import com.example.simplemusic.model.Music;
import com.example.simplemusic.util.ConstantUtil;
import com.example.simplemusic.util.MusicScanner;

/**
 * Scan local musics
 * @author SAKURAISHO
 * @since 2015.09.03
 *
 */

public class ScanFragment extends Fragment {
	
	private String mPath;
	private static final String TAG = "ScanFragment";
	Button mButton;
	TextView mTextView;
	ProgressBar mProgressBar;
	ImageView mImageFinish;
	boolean mRegistered;
	LinkedHashMap<Integer, Music> mMusics;
	Thread mThread;
	MusicScanner mScanner;
	ScanHandler mHandler;
	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mPath = Environment.getExternalStorageDirectory().toString();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_scan, container, false);
		mButton = (Button) v.findViewById(R.id.btn_scan);
		mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
		mImageFinish = (ImageView) v.findViewById(R.id.image_finish);
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mButton.getText().equals(getResources().getString(R.string.start_scan))) {
					if(mThread != null) stopThread();
					mThread = new Thread(new Runnable() {
				        @Override
				        public void run() {
				        	mScanner.startScanFile(new File(mPath));
				        }
				    });
					mThread.start();
					mProgressBar.setVisibility(View.VISIBLE);
					mImageFinish.setVisibility(View.GONE);
					mButton.setText(getResources().getString(R.string.stop_scan));
				} else if(mButton.getText().equals(getResources().getString(R.string.return_to_musics))) {
					Intent intent = new Intent(getActivity(), PlaylistActivity.class);
					startActivity(intent);
				} else {
					mScanner.cancelScan();
				}
			}
		});
		mTextView = (TextView) v.findViewById(R.id.scan_path);
		mHandler = new ScanHandler(this);
		mScanner = new MusicScanner(getActivity(), mHandler);
		return v;
	}

	private void stopThread() {
		mProgressBar.setVisibility(View.GONE);
		
        if (mThread != null) {
            Thread temp = mThread;
            mThread = null;
            temp.interrupt();
            mHandler.removeCallbacksAndMessages(null);
        }
    }
	
	static class ScanHandler extends Handler {

		WeakReference<Fragment> ref;
		
		public ScanHandler(Fragment f) {
			ref = new WeakReference<Fragment>(f);
		}
		
		@Override
		public void handleMessage(Message msg) {
			ScanFragment f = (ScanFragment) ref.get();
			Resources res = f.getActivity().getResources();
            switch (msg.what) {
                case MusicScanner.SCAN_PATH:
                    f.mTextView.setText((String)msg.obj);
                    break;
                case MusicScanner.SCAN_FINISH:
                    f.stopThread();
                    f.mImageFinish.setVisibility(View.VISIBLE);
                    BaseApplication.getMusicCollection().loadMusics();
                    f.mTextView.setText(String.format(res.getString(R.string.find_musics_count), BaseApplication.getMusicCollection().getCount()));
                    f.mButton.setText(res.getString(R.string.start_scan));
                    PreferenceManager.getDefaultSharedPreferences(f.getActivity())
                    					.edit()
                    					.putInt(ConstantUtil.PREF_ALL_MUSICS_COUNT, BaseApplication.getMusicCollection().getCount())
                    					.commit();
                    f.mButton.setText(res.getString(R.string.return_to_musics));
                    break;
                case MusicScanner.SCAN_CANCEL:
                    f.stopThread();
                    f.mTextView.setText("");
                    f.mButton.setText(res.getString(R.string.start_scan));
                    break;
            }
		}
		
	}
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		mScanner.cancelScan();
		stopThread();
	}


}
