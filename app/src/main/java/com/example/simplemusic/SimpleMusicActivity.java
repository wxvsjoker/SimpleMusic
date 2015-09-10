package com.example.simplemusic;

import com.example.simplemusic.base.BaseActivity;

import android.support.v4.app.Fragment;


public class SimpleMusicActivity extends BaseActivity {

	@Override
	protected Fragment createFragment() {
		return new SimpleMusicFragment();
	}


}
