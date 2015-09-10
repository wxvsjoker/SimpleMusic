package com.example.simplemusic.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Music data 
 * @author SAKURAISHO
 * @since 2015.09.02
 *
 */

public class Music {

	public static final String JSON_MUSIC_ID = "id";
	public static final String JSON_MUSIC_TITLE = "title";
	public static final String JSON_MUSIC_ARTIST = "artist";
	public static final String JSON_MUSIC_PATH = "path";
	public static final String JSON_MUSIC_DURATION = "duration";
	public static final String JSON_MUSIC_SIZE = "size";
	
	private int mId;
	private String mTitle;
	private String mArtist;
	private String mPath;
	private int mDuration;
	private long mSize;
	
	public Music(int id, String title, String artist, String path,
			int duration, long size) {
		super();
		mId = id;
		mTitle = title;
		mArtist = artist;
		mPath = path;
		mDuration = duration;
		mSize = size;
	}
	
	public Integer getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getArtist() {
		return mArtist;
	}

	public String getPath() {
		return mPath;
	}

	public int getDuration() {
		return mDuration;
	}

	public long getSize() {
		return mSize;
	}

	public Music(JSONObject json) throws JSONException {
		mId = json.getInt(JSON_MUSIC_ID);
		mTitle = json.getString(JSON_MUSIC_TITLE);
		mArtist = json.getString(JSON_MUSIC_ARTIST);
		mPath = json.getString(JSON_MUSIC_PATH);
		mDuration = json.getInt(JSON_MUSIC_DURATION);
		mSize = json.getLong(JSON_MUSIC_SIZE);
	}

	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_MUSIC_ID, mId);
		json.put(JSON_MUSIC_TITLE, mTitle);
		json.put(JSON_MUSIC_ARTIST, mArtist);
		json.put(JSON_MUSIC_PATH, mPath);
		json.put(JSON_MUSIC_DURATION, mDuration);
		json.put(JSON_MUSIC_SIZE, mSize);
		return json;
	}

}
