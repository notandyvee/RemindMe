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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ShowReminderActivity extends Activity{
	
	private String rememberItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		RemindMeDatabase db = new RemindMeDatabase(this);
		ArrayList<String> voiceResults = getIntent().getExtras()
		        .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		rememberItem = voiceResults.get(0);
		
		String lePath = db.getSingleItemImagePath(rememberItem);
		
		Card card = new Card(this);
		/*
		 * Keeping it simple for now. Removed card list if multiple things of the same item are found.
		 * This can be added later.*/
		if (lePath != null) {
				
			card.setFootnote(rememberItem);
			card.setImageLayout(ImageLayout.FULL);
			Uri uri = Uri.fromFile(new File(lePath));
			card.addImage(uri);
		
		} else {
			card.setText("Could not find any items matching that description.");
		}
		
		
		//Clean up resources
		db.closeDatabase();
		setContentView(card.toView());
		
		
	}
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_reminder, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	// Implement if needed
		return false;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection. Menu items typically start another
        // activity, start a service, or broadcast another intent.
        switch (item.getItemId()) {
            case R.id.show_reminder_settings:
                //TODO: Put deleting database stuff and photos
            	Log.d("ShowReminderActivity", "Should be deleting stuff.");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//          if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//              openOptionsMenu();
//              return true;
//          }
//          return false;
//    }
	
	
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	

	
	

}//end of class

