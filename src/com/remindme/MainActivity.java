package com.remindme;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.android.glass.app.Card;
import com.google.android.glass.media.CameraManager;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	private static final int RUN_CAMERA = 0;
	private final String appPhotoDirName = "RemindMe";
	/*Service stuff is currently not used but will be in order to facilitate
	 * Live card interaction whenever this functionality is requested.*/
	private boolean mBound = false;
	private TestService mService;
	private LiveCard liveCard;
	
	private RelativeLayout cardParent;
	private ImageView mImageView;
	private String rememberItem;
	String timeStamp;
	
	FileObserver observer;
	private String finalPhotoPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		cardParent = (RelativeLayout)findViewById(R.id.reminder_card_holder);
		mImageView = (ImageView)findViewById(R.id.image_to_remember);
		ArrayList<String> voiceResults = getIntent().getExtras()
		        .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		rememberItem = voiceResults.get(0);

		fireReminderPicture();
		/*
		 * Binding to a service. Ideally I should probably do this when
		 * the camera intent is returned.
		 * */
//		Intent intent = new Intent(this, TestService.class);
//		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		
//		setContentView(card.toView());
	}//end of onCreate
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK) {
			
			if(requestCode == RUN_CAMERA) {
				Log.d(TAG, "Running oResult");
	            String filePath = data.getStringExtra(CameraManager.EXTRA_PICTURE_FILE_PATH);
	            finalPhotoPath = filePath;
	            Log.d(TAG, "Le file path: "+filePath);
	            filePath = filePath.substring(0, filePath.lastIndexOf("/"));
	            Log.d(TAG, "Parent file: "+filePath);

				File leFile = new File(filePath);
				if(leFile.exists()){
					Log.d(TAG, "New file exists!");
				}
				else {
					Log.d(TAG, "New file does not exist");
				}
				observer = new FileObserver(filePath) { 
					// set up a file observer to watch this directory on sd card
				     @Override
				     public void onEvent(int event, String file) {
				        Log.d(TAG, "File created [" + file + "]");
				        if(file != null) {
					        Uri uri = Uri.fromFile(new File(finalPhotoPath));
					         
							final Card card = new Card(MainActivity.this);
							card.setText(rememberItem);
							card.addImage(uri);
							cardParent.post(new Runnable(){
								@Override
								public void run() {
									cardParent.addView(card.toView());
								}							
							});
					         
					         this.stopWatching();
				        }
				     }
				 };
				 observer.startWatching(); //START OBSERVING
//					Card card = new Card(this);
//					card.setText(rememberItem);
//					Uri uri = Uri.fromFile(photo);
//					//card.addImage(uri);
//					cardParent.addView(card.toView());
				
			}			
			
		}//end of check for RESULT_OK
		else {
			Log.d(TAG, "Oh no! Result code from Camera Intent returned not ok! The code is: "+ resultCode);
		}
		
		
	}//end of onActivityResult
	
	private void fireReminderPicture() {
		
		File createdMediaFile = getMediaFile();		
		if(createdMediaFile == null) {
			Log.d(TAG, "Failed to create le file.");
			return;
		}		
		Uri outputFileUri = Uri.fromFile(createdMediaFile);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);      
        startActivityForResult(cameraIntent, RUN_CAMERA);
		
	}//end of fireReminderPicture

	private File getMediaFile() { 
        File mediaStorageDir = getBaseAppDir();
        Log.d(TAG, "Directory name is: "+mediaStorageDir.getAbsolutePath()); 
        
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        
        // Create a media file name
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
        "REMIND_ME_IMG_"+ timeStamp + ".jpg");
        
        return mediaFile;
	}//end of getMediaFile
	
	private File getBaseAppDir() {
        File mediaStorageDir = new File(
        		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appPhotoDirName
        		);
        return mediaStorageDir;
	}//end of getBaseAppDir

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("MainActivity", "onDestory is running.");
//		liveCard.unpublish();
		//unbindService(mConnection);
		
	}
	
	/**
	 * Callback for the service
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			//cast the Binder to obtain the service.
			
			mBound = true;
		}
	};



}//end of class
