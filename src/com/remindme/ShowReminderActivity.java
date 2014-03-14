package com.remindme;

import java.io.File;
import java.util.ArrayList;
import com.google.android.glass.app.Card;
import com.google.android.glass.app.Card.ImageLayout;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ShowReminderActivity extends Activity{
	
	private static final String TAG = "ShowReminderActivity";
	private Memory mem;
	RemindMeDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = new RemindMeDatabase(this);
		ArrayList<String> voiceResults = getIntent().getExtras()
		        .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		String rememberItem = voiceResults.get(0);
		
		mem = db.searchMemory(rememberItem);
		
		Card card = new Card(this);
		/*
		 * Keeping it simple for now. Removed card list if multiple things of the same item are found.
		 * This can be added later.*/
		if (mem.getResizedImagePath() != null) {
				
			card.setFootnote(mem.getItem());
			card.setImageLayout(ImageLayout.FULL);
			Uri uri = Uri.fromFile(new File(mem.getResizedImagePath()));
			card.addImage(uri);
		
		} else {
			card.setText("Could not find any items matching that description.");
		}
		
		setContentView(card.toView());
		
	}//end of onCreate
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_reminder, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection. Menu items typically start another
        // activity, start a service, or broadcast another intent.
        switch (item.getItemId()) {
            case R.id.show_reminder_settings:
            	deleteItemResources();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /*
     * Method that deletes row in database and images stored in glass memory.
     */
    private void deleteItemResources() {
    	//Should we delete images first before removing it from the database?
    	boolean result = db.removeItem(mem);
    	if(result) {
    		Log.d(TAG, "Successfully deleted row");
    	} else {
    		Log.d(TAG, "Failed to delete item");
    	}

		try {
			
//			//Delete raw image first
//			File file = new File(mem.getRawImagePath());
//			boolean fResult = file.delete();
//			if (fResult) {
//				Log.d(TAG, "Successfully deleted raw image");
//			} else {
//				Log.d(TAG, "Failed to delete raw image.");
//			}
			
			//Delete resized image next
			File file = new File(mem.getResizedImagePath());
			boolean fResult = false;
			fResult = file.delete();
			if(fResult) {
				Log.d(TAG, "Successfully deleted resized image");
			} else {
				Log.d(TAG, "Failed to delete resized image.");
			}
 		}
		catch(Exception e) {
			Log.e(TAG, "Oh no! Error occurred when trying to delete images! \n", e);
		}

    	
    }//end of deleteItemResources
    
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
        	openOptionsMenu();
            return true;
        } 
        else if (keyCode == KeyEvent.KEYCODE_BACK) {
        	return super.onKeyDown(keyCode, event);
        }
        return false;
    }
	
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(db != null) {
			db.closeDatabase();
		}
	}
	

	
	

}//end of class

