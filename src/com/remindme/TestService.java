package com.remindme;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

public class TestService extends Service{
	
	private static final String TAG = "TestService";
	private final IBinder mBinder = new LocalBinder();
	private String finalPhotoPath;
	private FileObserver observer;
	private boolean saved = false;
	/*
	 * My Inner class that allows the calling Activity access to 
	 * the Service instance
	 * */
	public class LocalBinder extends Binder {		
		TestService getService() {
			return TestService.this;
		}	
	}//end of LocalBinder inner class
	
	@Override
	public void onCreate() {
		super.onCreate();
		//Do some instantiating here if necessary. Maybe creating a separate thread.
	}//end of onCreate

	@Override
	public IBinder onBind(Intent intent) {		
		return mBinder;
	}//end of onBind
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Running onStartCommand");
		final String itemRemember = intent.getStringExtra("remember_item");
        String filePath = intent.getStringExtra("pic_file_path");
        
        finalPhotoPath = filePath;
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));

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
		    	 try {
			        if(file != null && saved == false) {
			        	saved = true;
			        	RemindMeDatabase db = new RemindMeDatabase(TestService.this);
			        	db.addReminder(itemRemember, finalPhotoPath);
			        	Log.d(TAG, "Added item to database successfully. \nItem: "+itemRemember+"\n PhotoPath: "+finalPhotoPath);
			        	db.closeDatabase();
			        	this.stopWatching();

			        }
		        }
		    	 catch (Exception e) {
		    		 Log.d(TAG, "Exception", e);
		    	 }
		     }
		 };
		 observer.startWatching(); //START OBSERVING
		
		return START_NOT_STICKY;
	}//end of onStartCommand
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf();
	}//end of onDestroy
	
	
	
	
	
	

}//end of class
