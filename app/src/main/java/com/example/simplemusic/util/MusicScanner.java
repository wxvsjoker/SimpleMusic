package com.example.simplemusic.util;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

/**
 * Music scanner
 * @author SAKURAISHO
 * @since 2015.09.03
 *
 */

public class MusicScanner implements MediaScannerConnectionClient {

	public static final int SCAN_PATH = 1;
    public static final int SCAN_FINISH = 2;
    public static final int SCAN_CANCEL = 3;
    
    private MediaScannerConnection mConn;
    private Handler mHandler;
    private File mFile;
    private boolean mIsCanceled;
    
    public MusicScanner(Context context, Handler handler) {
        mConn = new MediaScannerConnection(context, this);
    	mHandler = handler;
    }
    
    public void startScanFile(File path) {
    	mIsCanceled = false;
    	scan(path);
    	if(mIsCanceled) 
    		mHandler.sendEmptyMessage(SCAN_CANCEL);
    	else
    		mHandler.sendEmptyMessage(SCAN_FINISH);
    	
    }
    
    private void scan(File path) {
    	Queue<File> queue = new LinkedList<File>();
        queue.offer(path);
        while (!queue.isEmpty()) {
            if (mIsCanceled) {
                mConn.disconnect();
                break;
            }
            File curFile = queue.poll();
            if (curFile.isDirectory() && curFile.length() != 0) {
                for (File f : curFile.listFiles()) {
                    if (f.isDirectory()) queue.offer(f);
                    else {
                        mFile = f;
                        mConn.connect();
                    }
                }
            }
        }
    }
    
    public void cancelScan() {
    	mIsCanceled = true;
    }
    
	@Override
	public void onMediaScannerConnected() {
		if (mIsCanceled || mFile == null) return;
		String path = mFile.getAbsolutePath();
        if (path.length() > 30) path = "..." + path.substring(path.length() - 30);
		Message msg = mHandler.obtainMessage();
		msg.what = SCAN_PATH;
		msg.obj = path;
		mHandler.sendMessage(msg);
		
		mConn.scanFile(mFile.getAbsolutePath(), "audio/x-mpeg");
		mFile = null;
	}

	@Override
	public void onScanCompleted(String arg0, Uri arg1) {
		mConn.disconnect();
	}

}
