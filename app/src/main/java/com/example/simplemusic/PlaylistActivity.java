package com.example.simplemusic;

import com.example.simplemusic.base.BaseActivity;
import com.example.simplemusic.util.ConstantUtil;

import android.support.v4.app.Fragment;

public class PlaylistActivity extends BaseActivity {

	@Override
	protected Fragment createFragment() {
		int index = getIntent().getIntExtra(ConstantUtil.EXTRA_PLAYLIST_INDEX, -1);
		String name = getIntent().getStringExtra(ConstantUtil.EXTRA_ACTIONBAR_NAME);
		return PlaylistFragment.newInstance(index, name);
	}

}
