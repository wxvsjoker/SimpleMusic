package com.example.simplemusic.model;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Playlist data
 * @author SAKURAISHO
 * @since 2015.09.02
 *
 */

public class Playlist {

	public static final String JSON_PLAYLIST_ID = "id";
	public static final String JSON_PLAYLIST_TITLE = "title";
	public static final String JSON_PLAYLIST_MUSICIDS = "musicids";
	private UUID mId;
	private String mTitle;
	private ArrayList<Integer> mMusicIds;
	
	public Playlist(String title) {
		mId = UUID.randomUUID();
		mTitle = title;
		mMusicIds = new ArrayList<Integer>();
	}

	public UUID getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public int getCount() {
		return mMusicIds.size();
	}

	public ArrayList<Integer> getIds() {
		return mMusicIds;
	}
	
	public void addNewMusic(int musicid) {
		for(int id : mMusicIds) {
			if(id == musicid)
				return;
		}
		mMusicIds.add(musicid);
	}
	
	public void deleteMusic(Integer musicid) {
		int index = 0;
		for(int id : mMusicIds) {
			if(id == musicid) {
				break;
			}
			index++;
		}
		if(index < mMusicIds.size())
			mMusicIds.remove(index);
	}
	
	public Playlist(JSONObject json) throws JSONException {
		mId = UUID.fromString(json.getString(JSON_PLAYLIST_ID));
		mTitle = json.getString(JSON_PLAYLIST_TITLE);
		mMusicIds = new ArrayList<Integer>();
		JSONArray array = json.getJSONArray(JSON_PLAYLIST_MUSICIDS);
		for(int i = 0; i < array.length(); i++) {
			mMusicIds.add(array.getInt(i));
		}
	}

	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_PLAYLIST_ID, mId.toString());
		json.put(JSON_PLAYLIST_TITLE, mTitle);
		JSONArray array = new JSONArray();
		for(int id : mMusicIds)
			array.put(id);
		json.put(JSON_PLAYLIST_MUSICIDS, array);
		return json;
	}
}
