package com.example.simplemusic.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.simplemusic.base.BaseApplication;
/**
 * Data collection to load musics
 * @author SAKURAISHO
 * @since 2015.09.02
 *
 */
public class MusicCollection {

	private static final String TAG = "MusicCollection";
	private static MusicCollection sMusicCollection;
	private LinkedHashMap<Integer, Music> mMusics;
	private Context mContext;
	private String[] mMusicInfo = new String[] {
			MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE
    };
	private String mQuery = MediaStore.Audio.Media.SIZE + ">" + 60 * 1024;
	
	private MusicCollection(Context context) {
		mContext = context;
		mMusics = new LinkedHashMap<Integer, Music>();
	}
	
	public static MusicCollection getInstance(Context context) {
		if(sMusicCollection == null) {
			sMusicCollection = new MusicCollection(context);
		}
		return sMusicCollection;
	}
	
	public ArrayList<Music> getMusics(Playlist playlist) {
		ArrayList<Music> musics = new ArrayList<Music>();
		ArrayList<Integer> ids = playlist.getIds();
		for(int id : ids) {
			if(mMusics.containsKey(id))
				musics.add(mMusics.get(id));
		}
		return musics;
	}

	public ArrayList<Music> getMusics(int playlistIndex) {
		Playlist p = BaseApplication.getPlaylistCollection().getPlaylist(playlistIndex);
		return getMusics(p);
	}
	
	public ArrayList<Music> getMusics() {
		ArrayList<Music> list = new ArrayList<Music>();
		Iterator<Map.Entry<Integer, Music>> iter = mMusics.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<Integer, Music> entry = iter.next();
			list.add(entry.getValue());
		}
		return list;
	}
	
	private void addNewMusic(Music music) {
		mMusics.put(music.getId(), music);
	}
	
	public void loadMusics() {
		Cursor cursor = mContext.getContentResolver()  
	            .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
	            		mMusicInfo,  
	            		mQuery, null, null);
		cursor.moveToFirst();
		for(int i = 0; i < cursor.getCount(); i++) {
			int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
			if(!mMusics.containsKey(id)) {
				String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
				String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
				String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
				long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
				Music music = new Music(id, title, artist, path, duration, size);
				sMusicCollection.addNewMusic(music);
			}
			cursor.moveToNext();
		}
	}
	
	public int getCount() {
		return mMusics.size();
	}
	
	public void deleteMusic(Music m) {
		if(mMusics.containsKey(m.getId())) {
			mMusics.remove(m.getId());
		}
	}
}
