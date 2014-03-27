package com.remindme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper class that manages the SQLite database
 * @author Andy Valdez
 *
 */
public class RemindMeDatabase {

	private String DB_NAME = "memories.db";
	private int DB_VERSION = 1;
	private RemindMeOpenHelper openHelper;
	private SQLiteDatabase database;
	
	/*Table Constants*/
	private String THINGS = "things";
	
	/* Column constants */
	private String ITEM = "thing_to_remember";
	private String RAW_PHOTO = "raw_photo_path";
	private String RESIZED_PHOTO = "resized_photo_path";
	private String TIMELINE_CARD_ID = "timeline_card_id";
	private String ITEM_MINI = "thing_to_remember_small";
	private String LOCATION = "lat_long";
	
	Context context;
	
	public RemindMeDatabase(Context context) {
		this.context = context;
		openHelper = new RemindMeOpenHelper(context);
		database = openHelper.getWritableDatabase();
	}//end of RemindMeDatabase
	
	
	public void addReminder(String itemRemember, String rawPath, String resizedPath, long timelineId, String latLong) {
		ContentValues c = new ContentValues();
		String itemSmall = minifySentence.stripSentence(itemRemember);
		Log.d("DATABASE", itemSmall);
		c.put(ITEM, itemRemember);
		c.put(ITEM_MINI, itemSmall);
		c.put(RAW_PHOTO, rawPath);
		c.put(RESIZED_PHOTO, resizedPath);
		c.put(TIMELINE_CARD_ID, timelineId);
		c.put(LOCATION, latLong);
		Log.d("STORING", latLong);
		database.insert(THINGS, null, c);
	}
	
	/*
	 * Using this method just to make sure database isn't getting filled while testing.
	 */
	public void deleteItems() {
		int rows = database.delete(THINGS, null, null);
		Log.d("TestService", "Rows deleted: " + rows);
	}
	
	
	public void closeDatabase() {
		if(database != null) {
			database.close();
		}
	}
	
	/**
	 * Method that takes a the String item to search for, and uses one of two
	 * empty objects also sent in for setting the return value. If both of those objects
	 * are still null afterwards, then the database returned nothing. Show appropriate method.
	 * 
	 * @param item String of the item to search for.
	 * @param mem null Memory object
	 * @param mCards null Card ArrayList
	 * @return Nothing. If both mem and mCards is null, then absolutely nothing was returned. Database is most likely empty.
	 */
	public Cursor searchMemory(String item) {
		item = minifySentence.stripSentence(item);
		Log.d("DATABASE", item);
		Cursor resultQuery = database.query(THINGS, new String[] {"rowid",ITEM, ITEM_MINI, RESIZED_PHOTO, RAW_PHOTO, TIMELINE_CARD_ID, LOCATION},
				"thing_to_remember_small MATCH ?", new String[] {"'" + item + "'"}, null, null, null);	
		return resultQuery;
		
	}//end of searchMemory()
	
	public boolean removeItem(Memory mem) {
		
		if(mem != null) {
			
			int result = database.delete(THINGS, "rowid = ?", new String[] {String.valueOf(mem.getId())});
			
			if (result == 1) {
				return true;
			}
		}
		
		return false;
	}
	
	
	
	/**
	 * Private inner class that creates the database instance.
	 * 
	 * @author Andy Valdez
	 *
	 */
	private class RemindMeOpenHelper extends SQLiteOpenHelper {
		
		public RemindMeOpenHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d("Test", "Creating table " + THINGS);
			String thingsToRemember = 
					"CREATE VIRTUAL TABLE " + THINGS + " USING fts4(thing_to_remember text, thing_to_remember_small," 
							+ " raw_photo_path text, resized_photo_path text, timeline_card_id text, lat_long text)";
			db.execSQL(thingsToRemember);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
		
	}//end of RemindMeOpenHelper
	
	
}//end of class
