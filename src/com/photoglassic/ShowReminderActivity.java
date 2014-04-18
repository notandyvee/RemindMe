package com.photoglassic;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.glass.app.Card;
import com.google.android.glass.app.Card.ImageLayout;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.photoglassic.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.widget.ImageView;

public class ShowReminderActivity extends Activity{
	
	private static final String TAG = "ShowReminderActivity";
	private Memory mem;
	private RemindMeDatabase db;
	private ArrayList<Card> mCards;
	private ArrayList<Memory> mMemories;
	MemoriesCardScrollAdapter adapter;
	CardScrollView scrolly;
	private String rememberItem;
	private View currentView;
	/*
	 * 0 = None
	 * 1 = Found item
	 * 2 = More than 1 item
	 * */
	private int status = 0;
	private ImageView mMapView;
	private static final int ZOOM = 17;
	private Card card;
	private static final String STATIC_MAP_URL_TEMPLATE =
            "https://maps.googleapis.com/maps/api/staticmap"
            + "?center=%.5f,%.5f"
            + "&zoom=%d"
            + "&sensor=true"
            + "&size=640x360"
            + "&scale=1"
            + "&style=element:geometry%%7Cinvert_lightness:true"
            + "&style=feature:landscape.natural.terrain%%7Celement:geometry%%7Cvisibility:on"
            + "&style=feature:landscape%%7Celement:geometry.fill%%7Ccolor:0x303030"
            + "&style=feature:poi%%7Celement:geometry.fill%%7Ccolor:0x404040"
            + "&style=feature:poi.park%%7Celement:geometry.fill%%7Ccolor:0x0a330a"
            + "&style=feature:water%%7Celement:geometry%%7Ccolor:0x00003a"
            + "&style=feature:transit%%7Celement:geometry%%7Cvisibility:on%%7Ccolor:0x101010"
            + "&style=feature:road%%7Celement:geometry.stroke%%7Cvisibility:on"
            + "&style=feature:road.local%%7Celement:geometry.fill%%7Ccolor:0x606060"
            + "&style=feature:road.arterial%%7Celement:geometry.fill%%7Ccolor:0x888888";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new RemindMeDatabase(this);
		mMapView = new ImageView(this);
		ArrayList<String> voiceResults = getIntent().getExtras()
		        .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		rememberItem = voiceResults.get(0);
		Cursor c = db.searchMemory(rememberItem);
		
		//setViews is below. Does all initialization.
		setViews(c);
		
		card = null;
		
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
		setContentView(status == 2 ? scrolly : card.getView());
		currentView = status == 2 ? scrolly : card.getView();
		
		
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
            		Log.d("delete", "status 1");
            	}
            	else if(status == 2) {
            		mCards.remove(adapter.currentSelectedPosition);
            		adapter.notifyDataSetChanged();
            		deleteItemResources(mMemories.get(adapter.currentSelectedPosition));
            		mMemories.remove(adapter.currentSelectedPosition);
            	}
            	
                return true;
		case R.id.show_reminder_map:
            	//get map
			String location = null;
			if(status == 1) {
				location = mem.getLocation();
        	}
        	else if(status == 2) {
        		location = mMemories.get(adapter.currentSelectedPosition).getLocation();
        	}
            	
        	if(location != null) {
        		String[] latLong = location.split(",");
            	String lat = latLong[0];
            	String longitude = latLong[1];
        		setContentView(mMapView);
        		currentView = mMapView;
        		loadMap(lat, longitude, ZOOM);
        	}
        	else {
        		Card nopeCard = new Card(getApplicationContext());
        		nopeCard.setText("Location for this item is unavailable.");
        		setContentView(nopeCard.getView());
        		currentView = mMapView;
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
    	
    	//TimelineManager man = TimelineManager.from(this);
    	//boolean staticCardRemoveResult = man.delete(Long.parseLong(memory.getTimelineId()));
    	
    	if(result) {
    		Log.d(TAG, "Successfully deleted row");
    	} else {
    		Log.d(TAG, "Failed to delete item");
    	}
    	
//    	if(staticCardRemoveResult) {
//    		Log.d(TAG, "Successfully removed item from timeline.");
//    	} else {
//    		Log.d(TAG, "Failed to remove static timeline card.");
//    	}

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
		
    	if (currentView.equals(scrolly) && mMemories.size() > 1) {
    		setContentView(scrolly);
    	}
    	else {
    		this.finish();
    	}
    }//end of deleteItemResources
    
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
        	
        	if (status != 0) {
        		if (mMapView.equals(currentView)) {
        			return true;
        		}
        		openOptionsMenu();
        		return true;
        	}
        } 
        else if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if (mMapView.equals(currentView)) {
        		if (scrolly != null) {
        			setContentView(scrolly);
        			currentView = scrolly;
        		}
        		else {
        			setContentView(card.getView());
        			currentView = card.getView();
        		}
        		return true;
			}
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
		memory.setSmallItem(resultQuery.getString(2));
		memory.setResizedImagePath(resultQuery.getString(3));
		memory.setRawImagePath(resultQuery.getString(4));
		memory.setLocation(resultQuery.getString(5));
		
		return memory;
	}
	
	private Card memoryToCard(Memory memory) {
		Card card = new Card(this);
		
		card.setFootnote(memory.getItem());
		card.setImageLayout(ImageLayout.FULL);
		//Uri uri = Uri.fromFile(new File(memory.getResizedImagePath()));
		card.addImage(BitmapFactory.decodeFile(memory.getResizedImagePath()));
		
		return card;
	}
	
	
	@SuppressLint("DefaultLocale")
	private static String makeStaticMapsUrl(String latitude, String longitude, int zoom) {
		try {
			return String.format(STATIC_MAP_URL_TEMPLATE, Double.parseDouble(latitude), Double.parseDouble(longitude), zoom)
			        + "&markers=icon:" + URLEncoder.encode("http://mirror-api.appspot.com/glass/images/map_dot.png",
			            "UTF-8") + "%7Cshadow:false%7C" + latitude + "," + "" + longitude.substring(1);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return String.format(STATIC_MAP_URL_TEMPLATE, Double.parseDouble(latitude), Double.parseDouble(longitude), zoom);
    }
	
	private void loadMap(String latitude, String longitude, int zoom) {
        String url = makeStaticMapsUrl(latitude, longitude, zoom);
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... urls) {
                try {
                    HttpResponse response = new DefaultHttpClient().execute(new HttpGet(urls[0]));
                    InputStream is = response.getEntity().getContent();
                    return BitmapFactory.decodeStream(is);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to load image", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    mMapView.setImageBitmap(bitmap);
                }
            }
        }.execute(url);
	}
	
	
	private class MemoriesCardScrollAdapter extends CardScrollAdapter {
		
		//Integer used by Scroll Adapter's onlick listener  to tell which once is currently selected
		public int currentSelectedPosition;

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
			return mCards.get(pos).getView();
		}

		@Override
		public int getPosition(Object ob) {
			return mCards.indexOf(ob);
		}
		
		
	}//end of inner class

}//end of class

