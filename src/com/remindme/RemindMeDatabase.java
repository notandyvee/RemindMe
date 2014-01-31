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

	private String DB_NAME = "remind_me.db";
	private int DB_VERSION = 1;
	private RemindMeOpenHelper openHelper;
	private SQLiteDatabase database;
	
	/*Table Constants*/
	private String THINGS = "things";
	
	public RemindMeDatabase(Context context) {
		openHelper = new RemindMeOpenHelper(context);
		database = openHelper.getWritableDatabase();
	}//end of RemindMeDatabase
	
	
	public void addReminder(String itemRemember, String path) {
		ContentValues c = new ContentValues();
		c.put("thing_to_remember", itemRemember);
		c.put("photo_path", path);
		database.insert(THINGS, null, c);
	}
	
	/*
	 * Using this method just to make sure database isn't getting filled while testing.
	 */
	public void deleteItems() {
		int rows = database.delete(THINGS, null, null);
		Log.d("TestService", "Rows deleted: "+rows);
	}
	
	
	public void closeDatabase() {
		if(database != null) {
			database.close();
		}
	}	
	
	
	public String getItemImagePath(String itemRemember) {
		Cursor resultQuery = database.query(THINGS, new String[] {"thing_to_remember", "photo_path"},
				"thing_to_remember MATCH ?", new String[] {itemRemember}, null, null, null);
		//Should really either be 1 or 0 in size. Will worry about getting a larger set if other similar items exist.
//		Cursor resultQuery = database.rawQuery("SELECT * FROM things WHERE thing_to_remember MATCH ?", new String[]{"'"+itemRemember+"'"});
		//Log.d("Test", msg)
		if(resultQuery.getCount() == 1) {
			resultQuery.moveToFirst();
			String path = resultQuery.getString(1);
			return path;
		}
		
		return null;
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
			String thingsToRemember = 
					"CREATE TABLE things (_id INTEGER PRIMARY KEY," +
					"thing_to_remember text, photo_path text)";
			db.execSQL(thingsToRemember);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
		
	}//end of RemindMeOpenHelper
	
	
}//end of class
