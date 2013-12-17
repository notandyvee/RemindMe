package com.remindme;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.android.glass.app.Card;
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
	
	/*Service stuff is currently not used but will be in order to facilitate
	 * Live card interaction whenever this functionality is requested.*/
	private boolean mBound = false;
	private TestService mService;
	private LiveCard liveCard;
	
	private RelativeLayout cardParent;
	private ImageView mImageView;
	private String rememberItem;
	private String baseDir;
	
	FileObserver observer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		cardParent = (RelativeLayout)findViewById(R.id.reminder_card_holder);
		mImageView = (ImageView)findViewById(R.id.image_to_remember);
		ArrayList<String> voiceResults = getIntent().getExtras()
		        .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		rememberItem = voiceResults.get(0);
		
		baseDir = setBaseDir();
		
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
				
				//Log.d(TAG, data.getExtras().get("data"));
				
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://" + baseDir+rememberItem+".jpg")));
				File photo = new File(baseDir+rememberItem+".jpg");
				if(photo.isFile()) {
					Log.d(TAG, "Successfully created file.");
				}
				
				observer = new FileObserver(photo.getAbsolutePath()) { // set up a file observer to watch this directory on sd card
				     @Override
				     public void onEvent(int event, String file) {
				         Log.d(TAG, "File created [" + file + "]");

				     }
				 };
				 observer.startWatching(); //START OBSERVING
				

//			    Bundle extras = data.getExtras();
//			    Bitmap mImageBitmap = (Bitmap) extras.get("data");
//			    mImageView.setImageBitmap(mImageBitmap);
				
					Card card = new Card(this);
					card.setText(rememberItem);
					Uri uri = Uri.fromFile(photo);
					//card.addImage(uri);
					cardParent.addView(card.toView());
				
			}			
			
		}//end of check for RESULT_OK
		else {
			Log.d(TAG, "Oh no! Result code from Camera Intent returned not ok! The code is: "+ resultCode);
		}
		
		
	}//end of onActivityResult
	
	private void fireReminderPicture() {
		
		String file = baseDir+rememberItem+".jpg";
		File newPhoto = new File(file);
		try {
			boolean result = newPhoto.createNewFile();
			if(!result)
				Log.d(TAG, "File was not created to to either an error or already exists.");
		}
		catch (IOException e) {
			Log.d(TAG, "IOException when creating file.");
		}
		
		Uri outputFileUri = Uri.fromFile(newPhoto);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        
        startActivityForResult(cameraIntent, RUN_CAMERA);
		
	}//end of fireReminderPicture

	private String setBaseDir() {
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/RemindMe/"; 
        Log.d(TAG, "Directory name is: "+dir);
        File newdir = new File(dir); 
        boolean result = newdir.mkdirs();
//		if(newdir.isDirectory())
//			Log.d(TAG, "Tis a directory");
        if(result)
        	Log.d(TAG, "Successfully created this directory!");
        else
        	Log.d(TAG, "Either this directory already exists or it failed...");
        
        return dir;
	}

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
