package com.example.simplemusic.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.example.simplemusic.R;

public abstract class BaseActivity extends FragmentActivity {
	
	
	protected int getActivityLayoutId() {
		return R.layout.activity_fragment;
	}
	
	protected abstract Fragment createFragment();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayoutId());
        
        
        FragmentManager fm = getSupportFragmentManager();
        
        // Don't consider screen rotate now
        Fragment fragment = createFragment();
        fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        
    }
	
}
