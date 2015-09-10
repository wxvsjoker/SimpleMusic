package com.example.simplemusic.dialog;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.simplemusic.R;
import com.example.simplemusic.R.id;
import com.example.simplemusic.R.layout;
import com.example.simplemusic.R.string;
import com.example.simplemusic.base.BaseApplication;
import com.example.simplemusic.model.Playlist;

public abstract class AddToPlaylistDialogFragment extends DialogFragment {

	ListView mListView;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
//		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_addtoplaylist, null);
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_addtoplaylist, null);
		mListView = (ListView) v.findViewById(R.id.listview_addplaylist);
		PlaylistAdapter adapter = new PlaylistAdapter(R.layout.listview_item_add_playlist, BaseApplication.getPlaylistCollection().getPlaylists());
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				addToPlaylistOperation(position);
				dismiss();
				
			}
		});
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.CustomDialogStyle);
		dialog.setView(v);
		dialog.setCancelable(false);
		return dialog.create();
	}

	public abstract void addToPlaylistOperation(int index);
	
	private class PlaylistAdapter extends ArrayAdapter<Playlist> {

		int mResourceId;
		public PlaylistAdapter(int textViewResourceId, List<Playlist> objects) {
			super(getActivity(), textViewResourceId, objects);
			mResourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = null;
			if(convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(mResourceId, null);
				tv = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(tv);
			} else {
				tv = (TextView) convertView.getTag();
			}
			tv.setText(getItem(position).getTitle());
			return convertView;
		}

	}
}
