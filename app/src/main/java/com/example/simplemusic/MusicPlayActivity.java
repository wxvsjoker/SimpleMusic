package com.example.simplemusic;

import com.example.simplemusic.base.BaseActivity;
import com.example.simplemusic.util.ConstantUtil;

import android.support.v4.app.Fragment;

public class MusicPlayActivity extends BaseActivity {

	@Override
	protected Fragment createFragment() {
		int pindex = getIntent().getIntExtra(ConstantUtil.EXTRA_CURRENT_PLAYLIST, ConstantUtil.PLAYLIST_NONE);
		int mindex = getIntent().getIntExtra(ConstantUtil.EXTRA_CURRENT_MUSIC, 0);
		return MusicPlayFragment.newInstance(pindex, mindex);
	}

}
