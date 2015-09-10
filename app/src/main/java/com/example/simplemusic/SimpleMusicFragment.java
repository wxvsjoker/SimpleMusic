package com.example.simplemusic;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.simplemusic.base.BaseApplication;
import com.example.simplemusic.dialog.DeleteDialogFragment;
import com.example.simplemusic.dialog.NewPlaylistDialogFragment;
import com.example.simplemusic.model.CurrentIndexObserver.CurrentIndexListener;
import com.example.simplemusic.model.Playlist;
import com.example.simplemusic.util.ConstantUtil;

/**
 * Main operation UI
 * @author WangXuan
 * @since 2015.09.02
 * Future feature: recent played musics
 */

public class SimpleMusicFragment extends Fragment {

	ArrayList<Playlist> mPlaylists;
	ListView mListViewPlaylists;
	PlaylistAdapter mPlaylistAdapater;
	ImageView mSettings;
	LinearLayout mLocalMusic;
	
	OnClickListener mLocalMusicListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int count = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(ConstantUtil.PREF_ALL_MUSICS_COUNT, 0);
			if(count == 0) {
				Intent i = new Intent(getActivity(), ScanActivity.class);
				startActivity(i);
			} else {
				Intent i = new Intent(getActivity(), PlaylistActivity.class);
				i.putExtra(ConstantUtil.EXTRA_ACTIONBAR_NAME, getResources().getString(R.string.all_musics));
				startActivity(i);
			}
		}
	};
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		BaseApplication.getMusicCollection().loadMusics();
		mPlaylists = BaseApplication.getPlaylistCollection().getPlaylists();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_simplemusic, container, false);

		getCurrentPlayIndex();
		
		mLocalMusic = (LinearLayout) v.findViewById(R.id.local_music);
		mLocalMusic.setOnClickListener(mLocalMusicListener);
		
		mListViewPlaylists = (ListView) v.findViewById(R.id.listview_playlists);
		mPlaylistAdapater = new PlaylistAdapter(R.layout.listview_item_playlist, mPlaylists);
		mListViewPlaylists.setAdapter(mPlaylistAdapater);
		mListViewPlaylists.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent i = new Intent(getActivity(), PlaylistActivity.class);
				i.putExtra(ConstantUtil.EXTRA_PLAYLIST_INDEX, position);
				i.putExtra(ConstantUtil.EXTRA_ACTIONBAR_NAME, mPlaylists.get(position).getTitle());
				startActivity(i);
			}
		});
		
		mSettings = (ImageView) v.findViewById(R.id.playlist_settings);
		mSettings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showNewPlaylistDialog();
			}

		});
		return v;
	}

	@Override
	public void onResume() {
		mPrevClickItemIndex = -1;
		mPlaylistAdapater.notifyDataSetChanged();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		BaseApplication.getPlaylistCollection().savePlaylists();
	}
	
	@Override
	public void onDestroy() {
		removeListener();
		super.onDestroy();
	}
	
	CurrentIndexListener mListener = new CurrentIndexListener() {
		
		@Override
		public void updateCurrentIndex(int pi, int mi) {
			mCurrentPlaylistIndex = pi;
			mPlaylistAdapater.notifyDataSetChanged();
		}
	};
	
	private void getCurrentPlayIndex() {
		mCurrentPlaylistIndex = BaseApplication.getCurrentIndexObserver().getPlaylistIndex();
		BaseApplication.getCurrentIndexObserver().registerListener(mListener);
	}

	private void removeListener() {
		BaseApplication.getCurrentIndexObserver().unregisterListener(mListener);
	}

	private void showNewPlaylistDialog() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		NewPlaylistDialogFragment dialog = new NewPlaylistDialogFragment() {
			
			@Override
			public void newPlaylistOperation(String text) {
				if(text.isEmpty()) text = getResources().getString(R.string.default_playlist);
				Playlist p = new Playlist(text);
				mPlaylists.add(p);
				mPlaylistAdapater.notifyDataSetChanged();
			}
		};
		dialog.show(fm, ConstantUtil.DIALOG_NEW_PLAYLIST);
	}
	
	int mPrevClickItemIndex = -1;
	int mCurrentPlaylistIndex = -1;
	private class PlaylistAdapter extends ArrayAdapter<Playlist> {

		private int mResourceId;
		public PlaylistAdapter(int rid, List<Playlist> objects) {
			super(getActivity(), rid, objects);
			mResourceId = rid;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Playlist p = getItem(position);
			View v;
			ViewHolder vh;
			if(convertView == null) {
				v = LayoutInflater.from(getActivity()).inflate(mResourceId, null);
				vh = new ViewHolder();
				vh.mTitleTextView = (TextView) v.findViewById(R.id.title);
				vh.mCountTextView = (TextView) v.findViewById(R.id.count);
				vh.mControlView = (RelativeLayout) v.findViewById(R.id.image_control);
				vh.mLinearLayout = (LinearLayout) v.findViewById(R.id.ll_delete);
				vh.mImageControl = (ImageView) v.findViewById(R.id.img_control);
				vh.mImageSpeaker = (ImageView) v.findViewById(R.id.img_speaker);
				v.setTag(vh);
			} else {
				v = convertView;
				vh = (ViewHolder) v.getTag();
			}
			vh.mTitleTextView.setText(p.getTitle());
			vh.mCountTextView.setText(Integer.toString(p.getCount()));
			if(mPrevClickItemIndex != position)
				vh.mLinearLayout.setVisibility(View.GONE);
			else
				vh.mLinearLayout.setVisibility(View.VISIBLE);
			if(mCurrentPlaylistIndex == position) {
				vh.mImageControl.setVisibility(View.GONE);
				vh.mImageSpeaker.setVisibility(View.VISIBLE);
				vh.mControlView.setOnClickListener(null);
			} else {
				vh.mImageControl.setVisibility(View.VISIBLE);
				vh.mImageSpeaker.setVisibility(View.GONE);
				vh.mControlView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						if(mPrevClickItemIndex == position) {
							mPrevClickItemIndex = -1;
						} else {
							mPrevClickItemIndex = position;
						}
						mPlaylistAdapater.notifyDataSetChanged();
					}
				});
				vh.mLinearLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						showAlertDialog(position);
					}
				});
			}
			return v;
		}
		
		private class ViewHolder{
			TextView mTitleTextView;
			TextView mCountTextView;
			RelativeLayout mControlView;
			LinearLayout mLinearLayout;
			ImageView mImageControl;
			ImageView mImageSpeaker;
		}
		
		public void showAlertDialog(final int position) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			DeleteDialogFragment dialog = new DeleteDialogFragment() {
				
				@Override
				public void deleteOperation() {
					mPrevClickItemIndex = -1;
					mPlaylists.remove(position);
					mPlaylistAdapater.notifyDataSetChanged();
					BaseApplication.getCurrentIndexObserver().deletePlaylistIndex(position);
				}
			};
			dialog.show(fm, ConstantUtil.DIALOG_DELETE);
		}
	}
}
