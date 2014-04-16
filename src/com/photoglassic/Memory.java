package com.photoglassic;

import android.util.Log;

/*
 * Simple class that holds the info we get from the database.
 */
public class Memory {
	
	private int id;
	private String item;
	private String itemSmall;
	
	private String rawImagePath;
	
	private String resizedImagePath;
	
	private String timelineId;
	
	private String location;

	public String getItem() {
		return item;
	}
	
	public String getSmallItem() { 
		return itemSmall;
	}

	public void setItem(String item) {
		this.item = item;
	}
	
	public void setSmallItem(String itemSmall) {
		this.itemSmall = itemSmall;
	}

	/**
	 * Get the id of the item in the SQLite database.
	 * @return The id that the database gave this item.
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get the raw image path of the photo glass returns to use.
	 * The image is unusable in apps due to the size.
	 * @return The string path of the photo's location.
	 */
	public String getRawImagePath() {
		return rawImagePath;
	}

	public void setRawImagePath(String rawImagePath) {
		this.rawImagePath = rawImagePath;
	}

	/**
	 * Get the image that was resized to be used in the app.
	 * @return The string path of the photo's location.
	 */
	public String getResizedImagePath() {
		return resizedImagePath;
	}

	public void setResizedImagePath(String resizedImagePath) {
		this.resizedImagePath = resizedImagePath;
	}

	public String getTimelineId() {
		return timelineId;
	}

	public void setTimelineId(String timelineId) {
		this.timelineId = timelineId;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getLocation() {
		Log.d("MEMORY LOCATION RETURN", location);
		return location;
	}
	
	

}//end of class
