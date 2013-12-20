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
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.BitmapFactory.Options;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	private static final int RUN_CAMERA = 0;
	private boolean mBound = false;
	private TestService mService;
	private RelativeLayout cardParent;
	private ImageView mImageView;
	private String rememberItem;	
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
					private Bitmap bitmap;

					// set up a file observer to watch this directory on sd card
				     @Override
				     public void onEvent(int event, String file) {
				    	 try {
					        if(file != null) {
					        	File forBitmap = new File(finalPhotoPath);
					        	if(forBitmap.exists()) {
				        			BitmapFactory.Options op = new BitmapFactory.Options();
				        			op.inSampleSize = 2;
					        		bitmap = BitmapFactory.decodeFile(forBitmap.getAbsolutePath(), op);
					        		
					        		if(bitmap != null) {
						        		mImageView.post(new Runnable() {
											@Override
											public void run() {
												mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
												mImageView.setImageBitmap(bitmap);
											}				
						        		});
						        		
										final Card card = new Card(MainActivity.this);
										card.setText(rememberItem);
										//card.addImage(uri);
										cardParent.post(new Runnable(){
											@Override
											public void run() {
												cardParent.addView(card.toView());
											}							
										});
								         this.stopWatching();					        		
					        		}
					        	}
					        }
				        }
				    	 catch (Exception e) {
				    		 Log.d(TAG, "Exception", e);
				    	 }
				     }
				 };
				 observer.startWatching(); //START OBSERVING
				
			}			
			
		}//end of check for RESULT_OK
		else {
			Log.d(TAG, "Oh no! Result code from Camera Intent returned not ok! The code is: "+ resultCode);
		}
		
		
	}//end of onActivityResult
	
	private void fireReminderPicture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);      
        startActivityForResult(cameraIntent, RUN_CAMERA);
		
	}//end of fireReminderPicture

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("MainActivity", "onDestory is running.");
		if(observer != null)
			observer.stopWatching();
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
