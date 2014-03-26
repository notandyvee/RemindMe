package com.remindme;

import java.io.File;
import java.util.ArrayList;

import com.google.android.glass.app.Card;
import com.google.android.glass.app.Card.ImageLayout;
import com.google.android.glass.timeline.TimelineManager;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ShowReminderActivity extends Activity{
	
	private static final String TAG = "ShowReminderActivity";
	private Memory mem;
	private RemindMeDatabase db;
	private ArrayList<Card> mCards;
	private ArrayList<Memory> mMemories;
	MemoriesCardScrollAdapter adapter;
	CardScrollView scrolly;
	/*
	 * 0 = None
	 * 1 = Found item
	 * 2 = More than 1 item
	 * */
	private int status = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = new RemindMeDatabase(this);
		ArrayList<String> voiceResults = getIntent().getExtras()
		        .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		String rememberItem = voiceResults.get(0);
		
		Cursor c = db.searchMemory(rememberItem);
		
		//setViews is below. Does all initialization.
		setViews(c);
		
		Card card = null;
		
		if (status == 1) {
			//Item was found so show	
			card = memoryToCard(mem);
		}
		else if (status == 2) {
			scrolly = new CardScrollView(this);
			adapter = new MemoriesCardScrollAdapter();
			scrolly.setAdapter(adapter);
			scrolly.activate();
			scrolly.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					Log.d(TAG, "Click is being registered. \nPosition is: "+position+ " \nID: "+ id);
					adapter.currentSelectedPosition = position;
					openOptionsMenu();
				}
			});
			
		}
		else {
			//TODO: No items match the description. Right now I will show this.
			// Not sure if showing everything in a list makes sense.
			// Showing a list of the most similar items makes even more sense.
			card = new Card(this);
			card.setText("Could not find any items matching that description.");	
			
		}
		
		//setContentView(mem != null ? card.toView() : scrolly);
		setContentView(status == 2 ? scrolly : card.toView());
		
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
            	if(status == 1) {
            		deleteItemResources(mem);
            	}
            	else if(status == 2) {
            		deleteItemResources(mMemories.get(adapter.currentSelectedPosition));
            		mCards.remove(adapter.currentSelectedPosition);
            		mMemories.remove(adapter.currentSelectedPosition);
            		adapter.notifyDataSetChanged();
            	}
            	
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /*
     * Method that deletes row in database and images stored in glass memory.
     */
    private void deleteItemResources(Memory memory) {
    	//TODO: Error checking needs to be done. Currently if 1 thing fails the activity will still finish, even thought all together the actions were not completed.
    	
    	//Should we delete images first before removing it from the database?
    	boolean result = db.removeItem(memory);
    	
    	TimelineManager man = TimelineManager.from(this);
    	boolean staticCardRemoveResult = man.delete(Long.parseLong(memory.getTimelineId()));
    	
    	if(result) {
    		Log.d(TAG, "Successfully deleted row");
    	} else {
    		Log.d(TAG, "Failed to delete item");
    	}
    	
    	if(staticCardRemoveResult) {
    		Log.d(TAG, "Successfully removed item from timeline.");
    	} else {
    		Log.d(TAG, "Failed to remove static timeline card.");
    	}

		try {
			
//			//Delete raw image first
			File file = new File(memory.getRawImagePath());
			boolean fResult = file.delete();
			if (fResult) {
				Log.d(TAG, "Successfully deleted raw image");
			} else {
				Log.d(TAG, "Failed to delete raw image.");
			}
			
			//Delete resized image next
			file = new File(memory.getResizedImagePath());
			fResult = false;
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

    	this.finish();
    }//end of deleteItemResources
    
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
        	
        	if (status != 0) {
        		openOptionsMenu();
        		return true;
        	}
        } 
        else if (keyCode == KeyEvent.KEYCODE_BACK) {
        	return super.onKeyDown(keyCode, event);
        }
        return false;
    }
	
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(db != null) {
			db.closeDatabase();
		}
	}
	
	
	/**
	 * Helper method to figure out how to set the view.
	 * @param resultQuery
	 */
	private void setViews(Cursor resultQuery) {
		
		if(resultQuery.getCount() == 1) {
			resultQuery.moveToFirst();
			Log.d(TAG, "CURSOR STRING: "+resultQuery.getInt(0));
			
			mem = cursorToMemory(resultQuery);
			
			Log.d(TAG, "Memory ob id: "+mem.getId());
			status = 1;
			
		} else if (resultQuery.getCount() > 1) {
			mCards = new ArrayList<Card>();
			mMemories = new ArrayList<Memory>();
			for (int i = 0; i < resultQuery.getCount(); i++) {
				resultQuery.moveToPosition(i);
				Memory tempMemory = cursorToMemory(resultQuery);
				mMemories.add(tempMemory);
				Card tempCard = memoryToCard(tempMemory);
				mCards.add(tempCard);
			}
			status = 2;
		}
		resultQuery.close();
		
		//Setting flags to quickly do checks.
		
	}//end of setViews
	
	/**
	 * Helper method to make taking a cursor of the memory and
	 * creating a Memory object from it easy.
	 * @param resultQuery
	 * @return resulting memory object from the values of the cursor.
	 */
	private Memory cursorToMemory(Cursor resultQuery) {
		Memory memory = new Memory();
		
		memory.setId(resultQuery.getInt(0));
		memory.setItem(resultQuery.getString(1));
		memory.setResizedImagePath(resultQuery.getString(2));
		memory.setRawImagePath(resultQuery.getString(3));
		memory.setTimelineId(resultQuery.getString(4));
		
		return memory;
	}
	
	private Card memoryToCard(Memory memory) {
		Card card = new Card(this);
		
		card.setFootnote(memory.getItem());
		card.setImageLayout(ImageLayout.FULL);
		Uri uri = Uri.fromFile(new File(memory.getResizedImagePath()));
		card.addImage(uri);
		
		return card;
	}
	
	
	
	private class MemoriesCardScrollAdapter extends CardScrollAdapter {
		
		//Integer used by Scroll Adapter's onlick listener  to tell which once is currently selected
		public int currentSelectedPosition;
		
		@Override
		public int findIdPosition(Object ob) {
			return -1;
		}

		@Override
		public int findItemPosition(Object ob) {
			return mCards.indexOf(ob);
		}

		@Override
		public int getCount() {
			return mCards.size();
		}

		@Override
		public Object getItem(int pos) {
			return mCards.get(pos);
		}

		@Override
		public View getView(int pos, View arg1, ViewGroup arg2) {
			return mCards.get(pos).toView();
		}
		
		
	}//end of inner class

}//end of class

