package com.remindme;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TestService extends Service{
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}//end of class
