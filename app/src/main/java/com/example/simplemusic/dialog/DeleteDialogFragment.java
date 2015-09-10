package com.example.simplemusic.dialog;

import com.example.simplemusic.R;
import com.example.simplemusic.R.string;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public abstract class DeleteDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.CustomDialogStyle);
		dialog.setTitle(getActivity().getResources().getString(R.string.delete));
		dialog.setMessage(getActivity().getResources().getString(R.string.msg_delete));
		dialog.setCancelable(false);
		dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				deleteOperation();
			}
		});
		dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		return dialog.create();
	}
	
	public abstract void deleteOperation();

}
