package com.remindme;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.android.glass.app.Card;
import com.google.android.glass.app.Card.ImageLayout;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowReminderActivity extends Activity{
	
	private String rememberItem;
	private List<Card> mCards;
	private CardScrollView mCardScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCards = new ArrayList<Card>();
		RemindMeDatabase db = new RemindMeDatabase(this);
		ArrayList<String> voiceResults = getIntent().getExtras()
		        .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		rememberItem = voiceResults.get(0);
		
		ArrayList<String> imagePaths = db.getItemImagePath(rememberItem);
		if(imagePaths != null && !imagePaths.isEmpty()) {
			for (String s : imagePaths) {
				Card card = new Card(this);
				
				
				setContentView(R.layout.show_reminder);
				ImageView image = (ImageView)findViewById(R.id.item_image);
				
				
			
			//TODO: Get Image path and resize.
			

	//		card.setText(rememberItem +"\n" +"Image Path is: "+imagePath);
			
				Bitmap bitmap = BitmapFactory.decodeFile(s);
				bitmap = getResizedBitmap(bitmap, 640);
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
				String path = Images.Media.insertImage(this.getContentResolver(), bitmap, "title", null);
				Uri uri = Uri.parse(path);
				//image.setImageBitmap(bitmap);
				
		
				card.setFootnote(rememberItem);
				Log.d("SHOW", "73 " + uri.getPath());
				card.setImageLayout(ImageLayout.FULL);
				card.addImage(uri);
				
				mCards.add(card);
			}
			mCardScrollView = new CardScrollView(this);
			memoriesCardScrollAdapter adapter = new memoriesCardScrollAdapter();
			mCardScrollView.setAdapter(adapter);
			mCardScrollView.activate();
			setContentView(mCardScrollView);
		}
		else {
//			setContentView(R.layout.show_reminder);
//			ImageView image = (ImageView)findViewById(R.id.item_image);
//			TextView text = (TextView)findViewById(R.id.where_did_i_item_text);
//			text.setText("Could not find any items matching that description.");
			Card card = new Card(this);
			card.setText("Could not find any items matching that description.");
			setContentView(card.toView());
			
		}
		//Finally set remembered item to the screen
//		RelativeLayout parent = (RelativeLayout)findViewById(R.id.show_reminder_card_holder);
//		parent.addView(card.toView());
		
		//Clean up resources
		db.closeDatabase();
		
		
		
	}
//		Card card = new Card(this);
	
	
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
	

	private class memoriesCardScrollAdapter extends CardScrollAdapter {

		@Override
		public int findIdPosition(Object arg0) {
			// TODO Auto-generated method stub
			return -1;
		}

		@Override
		public int findItemPosition(Object arg0) {
			// TODO Auto-generated method stub
			return mCards.indexOf(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mCards.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mCards.get(arg0);
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return mCards.get(arg0).toView();
		}
	
	}	
	
	

}//end of class

