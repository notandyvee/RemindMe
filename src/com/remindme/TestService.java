package com.remindme;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class TestService extends Service{
	
	private final IBinder mBinder = new LocalBinder();
	
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
	public void onDestroy() {
		super.onDestroy();
		//Not really necessary because this service will be a bound service
	}//end of onDestroy
	
	
	
	
	
	

}//end of class
