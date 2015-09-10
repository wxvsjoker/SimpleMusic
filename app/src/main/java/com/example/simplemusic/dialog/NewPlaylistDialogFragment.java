package com.example.simplemusic.dialog;

import com.example.simplemusic.R;
import com.example.simplemusic.R.id;
import com.example.simplemusic.R.layout;
import com.example.simplemusic.R.string;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

public abstract class NewPlaylistDialogFragment extends DialogFragment {

	public static final String EXTRA_NEW_PLAYLIST_NAME = "extranewplaylistname";
	
	TextView mTextView;
	
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_newplaylist, null);
		mTextView = (TextView) v.findViewById(R.id.text_new_playlist);
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.CustomDialogStyle);
		dialog.setView(v);
		dialog.setTitle(getActivity().getResources().getString(R.string.new_playlist));
		dialog.setCancelable(false);
		dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				newPlaylistOperation(mTextView.getText().toString());
			}
		});
		dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		return dialog.create();
	}

	public abstract void newPlaylistOperation(String text);
}
