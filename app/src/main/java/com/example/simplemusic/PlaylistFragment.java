package com.example.simplemusic;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.Toast;

import com.example.simplemusic.base.BaseApplication;
import com.example.simplemusic.dialog.AddToPlaylistDialogFragment;
import com.example.simplemusic.dialog.DeleteDialogFragment;
import com.example.simplemusic.model.CurrentIndexObserver.CurrentIndexListener;
import com.example.simplemusic.model.Music;
import com.example.simplemusic.util.ConstantUtil;

/**
 * Display music list
 * @author WangXuan
 * @since 2015.09.04
 *
 */

public class PlaylistFragment extends Fragment {

	public static final String EXTRA_PLAYLIST_INDEX = "extraplaylistindex";
	public static final String EXTRA_ACTIONBAR_NAME = "extraactionbarname";
	ListView mListView;
	TextView mEmptyView;
	MusicAdapter mAdapter;
	ArrayList<Music> mData;
	int mPlaylistIndex;
	String mActionbarName;
	
	public static PlaylistFragment newInstance(int index, String name) {
		Bundle b = new Bundle();
		b.putInt(EXTRA_PLAYLIST_INDEX, index);
		b.putString(EXTRA_ACTIONBAR_NAME, name);
		PlaylistFragment fragment = new PlaylistFragment();
		fragment.setArguments(b);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mPlaylistIndex = getArguments().getInt(EXTRA_PLAYLIST_INDEX);
		mActionbarName = getArguments().getString(EXTRA_ACTIONBAR_NAME);
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_allmusics, container, false);
		if(NavUtils.getParentActivityName(getActivity()) != null) {
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		getActivity().getActionBar().setTitle(mActionbarName);
		getCurrentPlayIndex();
		mListView = (ListView) v.findViewById(R.id.listview_base);
		mEmptyView = (TextView) v.findViewById(R.id.listview_empty);
		mListView.setEmptyView(mEmptyView);
		
		if(mPlaylistIndex == -1)
			mData = BaseApplication.getMusicCollection().getMusics();
		else {
			mData = BaseApplication.getMusicCollection().getMusics(mPlaylistIndex);
		}
		mAdapter = new MusicAdapter(R.layout.listview_item_music, mData);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				if(!isMusicExist(position)) {
					showDeleteDialog(position);
					return;
				}
				Intent i = new Intent(getActivity(), MusicPlayActivity.class);
				i.putExtra(ConstantUtil.EXTRA_CURRENT_MUSIC, position);
				i.putExtra(ConstantUtil.EXTRA_CURRENT_PLAYLIST, mPlaylistIndex);
				startActivity(i);
			}
		});
		return v;
	}
	
	@Override
	public void onResume() {
		mPrevClickItemIndex = -1;
		mAdapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if(mPlaylistIndex == ConstantUtil.PLAYLIST_NONE)
			getActivity().getMenuInflater().inflate(R.menu.music_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			if(NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		case R.id.menu_scan:
			Intent i = new Intent(getActivity(), ScanActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	@Override
	public void onDestroy() {
		removeListener();
		super.onDestroy();
	}
	
	CurrentIndexListener mListener = new CurrentIndexListener() {
		
		@Override
		public void updateCurrentIndex(int pi, int mi) {
			if(mPlaylistIndex != pi) return;
			mCurrentMusicIndex = mi;
			mAdapter.notifyDataSetChanged();
		}
	};
	
	private void getCurrentPlayIndex() {
		int mci = BaseApplication.getCurrentIndexObserver().getMusicIndex();
		int pci = BaseApplication.getCurrentIndexObserver().getPlaylistIndex();
		if(mPlaylistIndex == pci)
			mCurrentMusicIndex = mci;
		BaseApplication.getCurrentIndexObserver().registerListener(mListener);
	}
	
	private void removeListener() {
		BaseApplication.getCurrentIndexObserver().unregisterListener(mListener);
	}

	private boolean isMusicExist(int index) {
		File f = new File(mData.get(index).getPath());
		return f.exists();
	}

	private int mCurrentMusicIndex = -1;	
	int mPrevClickItemIndex = -1;
	// Some other features remained like delete local music
	private class MusicAdapter extends ArrayAdapter<Music> {

		private int mResourceId;
		public MusicAdapter(int rid,
				List<Music> musics) {
			super(getActivity(), rid, musics);
			mResourceId = rid;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final Music music = getItem(position);
			View v;
			ViewHolder vh;
			if(convertView == null) {
				v = LayoutInflater.from(getActivity()).inflate(mResourceId, null);
				vh = new ViewHolder();
				vh.mTitleTextView = (TextView) v.findViewById(R.id.title);
				vh.mArtistView = (TextView) v.findViewById(R.id.artist);
				vh.mControlView = (RelativeLayout) v.findViewById(R.id.image_control);
				vh.mLinearLayout = (LinearLayout) v.findViewById(R.id.add_delete);
				vh.mAdd = (LinearLayout) v.findViewById(R.id.ll_add);
				vh.mDelete = (LinearLayout) v.findViewById(R.id.ll_delete);
				vh.mImageControl = (ImageView) v.findViewById(R.id.img_control);
				vh.mImageSpeaker = (ImageView) v.findViewById(R.id.img_speaker);
				v.setTag(vh);
			} else {
				v = convertView;
				vh = (ViewHolder) v.getTag();
			}
			
			vh.mTitleTextView.setText(music.getTitle());
			vh.mArtistView.setText(music.getArtist());
			vh.mControlView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(position == mPrevClickItemIndex) {
						mPrevClickItemIndex = -1;
					} else {
						mPrevClickItemIndex = position;
					}
					mAdapter.notifyDataSetChanged();
				}
			});
			vh.mAdd.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					showAddToPlaylistDialog(music);
				}
			});

			if(mPlaylistIndex == ConstantUtil.PLAYLIST_NONE) {
				vh.mDelete.setVisibility(View.GONE);
			} else {
				vh.mDelete.setVisibility(View.VISIBLE);
				vh.mDelete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						showDeleteDialog(position);
					}
				});
			}
			if(mPrevClickItemIndex != position) {
				vh.mLinearLayout.setVisibility(View.GONE);
			} else {
				vh.mLinearLayout.setVisibility(View.VISIBLE);
			}
			if(mCurrentMusicIndex == position && mPlaylistIndex != ConstantUtil.PLAYLIST_NONE) {
				vh.mImageControl.setVisibility(View.GONE);
				vh.mImageSpeaker.setVisibility(View.VISIBLE);
				vh.mDelete.setVisibility(View.GONE);
			} else {
				vh.mImageControl.setVisibility(View.VISIBLE);
				vh.mImageSpeaker.setVisibility(View.GONE);
			}
			return v;
		}
		
		private class ViewHolder {
			TextView mTitleTextView;
			TextView mArtistView;
			RelativeLayout mControlView;
			LinearLayout mLinearLayout;
			LinearLayout mAdd;
			LinearLayout mDelete;
			ImageView mImageControl;
			ImageView mImageSpeaker;
		}
		
		
	}
	
	public void showDeleteDialog(final int position) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		DeleteDialogFragment dialog = new DeleteDialogFragment() {
			
			@Override
			public void deleteOperation() {
				mPrevClickItemIndex = -1;
				Music m = mData.get(position);
				mData.remove(position);
				mAdapter.notifyDataSetChanged();
				if(mPlaylistIndex == ConstantUtil.PLAYLIST_NONE) {
					BaseApplication.getMusicCollection().deleteMusic(m);
					PreferenceManager.getDefaultSharedPreferences(getActivity())
					.edit()
					.putInt(ConstantUtil.PREF_ALL_MUSICS_COUNT, BaseApplication.getMusicCollection().getCount())
					.commit();
				} else {
					BaseApplication.getPlaylistCollection().getPlaylist(mPlaylistIndex).deleteMusic(m.getId());
				}
				BaseApplication.getCurrentIndexObserver().deleteMusicIndex(mPlaylistIndex, position);
			}
		};
		dialog.show(fm, ConstantUtil.DIALOG_DELETE);
	}
	
	public void showAddToPlaylistDialog(final Music music) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		AddToPlaylistDialogFragment dialog = new AddToPlaylistDialogFragment() {
			
			@Override
			public void addToPlaylistOperation(int index) {
				BaseApplication.getPlaylistCollection().getPlaylist(index).addNewMusic(music.getId());
				Toast.makeText(getActivity(), R.string.msg_success_add_playlist, Toast.LENGTH_SHORT).show();
			}
		};
		dialog.show(fm, ConstantUtil.DIALOG_ADD_TO_PLAYLIST);
	}

}
