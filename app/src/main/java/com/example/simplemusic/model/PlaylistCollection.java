package com.example.simplemusic.model;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.example.simplemusic.R;
import com.example.simplemusic.util.JSONSerializer;

/**
 * Data collection to save playlists
 * @author SAKURAISHO
 * @since 2015.09.02
 *
 */
public class PlaylistCollection {
	
	private static final String TAG = "PlaylistCollection";
	private static final String PLAYLISTFILENAME = "playlistfilename";
	private static PlaylistCollection sPlaylistCollection;
	private ArrayList<Playlist> mPlaylists;
	private Context mContext;
	private JSONSerializer mJSONSerializer;
	
	private PlaylistCollection(Context context) {
		mContext = context;
		mJSONSerializer = new JSONSerializer(mContext, PLAYLISTFILENAME);
		try {
			mPlaylists = mJSONSerializer.loadPlaylists();
		} catch (Exception e) {
			mPlaylists = new ArrayList<Playlist>();
		}
		if(mPlaylists.size() == 0)
			createMyFavoritePlaylist();
	}
	
	private void createMyFavoritePlaylist() {
		addNewPlaylist(mContext.getResources().getString(R.string.my_favorite_playlist));
	}
	
	public void addNewPlaylist(String title) {
		mPlaylists.add(new Playlist(title));
	}
	
	public void deletePlaylist(int index) {
		mPlaylists.remove(index);
	}
	
	public static PlaylistCollection getInstance(Context context) {
		if(sPlaylistCollection == null) {
			sPlaylistCollection = new PlaylistCollection(context);
		}
		return sPlaylistCollection;
	}
	
	public ArrayList<Playlist> getPlaylists() {
		return mPlaylists;
	}
	
	public boolean savePlaylists() {
		try {
			mJSONSerializer.savePlaylists(mPlaylists);
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Unable to save playlists");
			return false;
		}
	}
	
	public Playlist getPlaylist(int index) {
		return mPlaylists.get(index);
	}
}
