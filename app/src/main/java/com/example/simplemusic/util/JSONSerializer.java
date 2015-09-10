package com.example.simplemusic.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

import com.example.simplemusic.model.Music;
import com.example.simplemusic.model.Playlist;

/**
 * Tool to load and save playlists to local file
 * @author SAKURAISHO
 * @since 2015.09.02
 *
 */
public class JSONSerializer {
	
	private Context mContext;
	private String mFilename;

	public JSONSerializer(Context context, String filename) {
		mContext = context;
		mFilename = filename;
	}
	
	private void saveToFile(JSONArray array) throws IOException {
		Writer writer = null;
		try {
			OutputStream os = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(os);
			writer.write(array.toString());
		} finally {
			if(writer != null)
				writer.close();
		}
	}

	public void saveMusics(LinkedHashMap<Integer, Music> musics) throws JSONException, IOException {
		JSONArray array = new JSONArray();
		Iterator<Map.Entry<Integer, Music>> iter = musics.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<Integer, Music> entry = iter.next();
			array.put(entry.getValue().toJson());
		}
		saveToFile(array);
	}
	
	public void savePlaylists(ArrayList<Playlist> playlists) throws JSONException, IOException {
		JSONArray array = new JSONArray();
		for(Playlist p : playlists) {
			array.put(p.toJson());
		}
		saveToFile(array);
	}
	
	private StringBuilder loadFile() throws IOException {
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while((line = reader.readLine()) != null) {
				sb.append(line);
			}
			
		} finally {
			if(reader != null)
				reader.close();
		}
		return sb;
	}
	
	public LinkedHashMap<Integer, Music> loadMusics() throws IOException, JSONException {
		StringBuilder sb = loadFile();
		LinkedHashMap<Integer, Music> musics = new LinkedHashMap<Integer, Music>();
		JSONArray array = (JSONArray) new JSONTokener(sb.toString()).nextValue();
		for(int i = 0; i < array.length(); i++) {
			Music music = new Music(array.getJSONObject(i));
			musics.put(music.getId(), music);
		}
		return musics;
	}
	
	public ArrayList<Playlist> loadPlaylists() throws IOException, JSONException {
		StringBuilder sb = loadFile();
		ArrayList<Playlist> playlists = new ArrayList<Playlist>();
		JSONArray array = (JSONArray) new JSONTokener(sb.toString()).nextValue();
		for(int i = 0; i < array.length(); i++) {
			Playlist playlist = new Playlist(array.getJSONObject(i));
			playlists.add(playlist);
		}
		return playlists;
	}
}
