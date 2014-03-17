package com.remindme;

import java.io.ByteArrayOutputStream;
import java.io.File;

import com.google.android.glass.app.Card;
import com.google.android.glass.app.Card.ImageLayout;
import com.google.android.glass.timeline.TimelineManager;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Binder;
import android.os.FileObserver;
import android.os.IBinder;
import android.provider.MediaStore.Images;
import android.util.Log;

public class TestService extends Service{
	
	private static final String TAG = "TestService";
	private final IBinder mBinder = new LocalBinder();
	private String resizedPhotoPath;
	private String rawPhotoPath;
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
        
        rawPhotoPath = filePath;
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
						
			        	//Convert here 
			        	saved = true;
			        	RemindMeDatabase db = new RemindMeDatabase(TestService.this);
			        	

			        	Bitmap bitmap = BitmapFactory.decodeFile(rawPhotoPath);
						bitmap = getResizedBitmap(bitmap, 640);
						ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
						String path = Images.Media.insertImage(getContentResolver(), bitmap, "title", "Photo of item to remember.");

						Uri uri = Uri.parse(path);
						if (uri.getScheme().equals("content")) {
							Cursor cursor = getContentResolver().query(uri, new String [] {android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
							cursor.moveToFirst();
							resizedPhotoPath = cursor.getString(0);
							uri = Uri.fromFile(new File(resizedPhotoPath));
							cursor.close();
						}	
			        	
			        	TimelineManager tm = TimelineManager.from(getApplicationContext());
			        	Card card = new Card(getApplicationContext());
			        	card.setText(itemRemember);
			        	card.setImageLayout(ImageLayout.FULL);
			        	card.addImage(uri);
			        	
			        	long tId = tm.insert(card);
			        	
			        	db.addReminder(itemRemember, rawPhotoPath, resizedPhotoPath, tId);
			        	
			        	db.closeDatabase();
			        	Log.d("SERVICE", "this.stopWatching()");
			        	this.stopWatching();
			        	stopSelf();
			        }
		        }
		    	 catch (Exception e) {
		    		 Log.d(TAG, "Exception in Observer when getting back the image.", e);
		    	 }
		     }
		 };
		 new Thread(new Runnable() {	
			@Override
			public void run() {
				//TODO: Move the separate thread location to the beginning of onStartCommand. Although that may not be neccessary since the bulk of the code is run while file observing.
				observer.startWatching(); //START OBSERVING
			}
		}).start();		 	
		return START_NOT_STICKY;
	}//end of onStartCommand
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "TestService is actually being destroyed!.");
	}//end of onDestroy
	
	
	/*
	 * Method that returns a resized bitmap so our glas app can use it for
	 * displaying.
	 * */
	private Bitmap getResizedBitmap(Bitmap bm, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();

	    float aspect = (float)width / height;	
	    float scaleWidth = newWidth;
	    float scaleHeight = scaleWidth / aspect;
	
	    // create a matrix for the manipulation
	    Matrix matrix = new Matrix();
	
	    // resize the bit map	
	    matrix.postScale(scaleWidth / width, scaleHeight / height);
	
	    // recreate the new Bitmap	
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);	
	    bm.recycle();
	
	    return resizedBitmap;
    }
	
	
	

}//end of class
