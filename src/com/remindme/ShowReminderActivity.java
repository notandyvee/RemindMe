package com.remindme;

import java.util.ArrayList;

import com.google.android.glass.app.Card;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowReminderActivity extends Activity{
	
	private String rememberItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.show_reminder);
		
//		Card card = new Card(this);
		
		ImageView image = (ImageView)findViewById(R.id.item_image);
		TextView text = (TextView)findViewById(R.id.where_did_i_item_text);
		
		RemindMeDatabase db = new RemindMeDatabase(this);
		
		ArrayList<String> voiceResults = getIntent().getExtras()
		        .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		rememberItem = voiceResults.get(0);
		
		//TODO: Get Image path and resize.
		String imagePath = db.getItemImagePath(rememberItem);
		
//		card.setText(rememberItem +"\n" +"Image Path is: "+imagePath);
		
		//TODO: set resized image here
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
		bitmap = getResizedBitmap(bitmap, 480);
		image.setImageBitmap(bitmap);
		
		text.setText(rememberItem);
		//Finally set remembered item to the screen
//		RelativeLayout parent = (RelativeLayout)findViewById(R.id.show_reminder_card_holder);
//		parent.addView(card.toView());
		
		//Clean up resources
		db.closeDatabase();
		
	}
	
	
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
