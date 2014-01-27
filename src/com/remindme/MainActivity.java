package com.remindme;

import java.util.ArrayList;
import com.google.android.glass.app.Card;
import com.google.android.glass.media.CameraManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	private static final int RUN_CAMERA = 0;
	private RelativeLayout cardParent;
	private ImageView mImageView;
	private String rememberItem;	
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
		Log.d(TAG, "Result code is: "+resultCode);
		if(resultCode == RESULT_OK) {
			
			if(requestCode == RUN_CAMERA) {
				RemindMeDatabase db = new RemindMeDatabase(this);
				db.deleteItems();
				db.closeDatabase();
				
				String filePath = data.getStringExtra(CameraManager.EXTRA_PICTURE_FILE_PATH);
				Log.d(TAG, "Successfully got filePath: "+filePath);
				Intent intent = new Intent(this, TestService.class);
				intent.putExtra("pic_file_path", filePath);
				intent.putExtra("remember_item", rememberItem);
				startService(intent);
				
			
				Card card = new Card(this);
				card.setText("Reminding you where you put your: "+rememberItem);
				cardParent.addView(card.toView());
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




}//end of class
