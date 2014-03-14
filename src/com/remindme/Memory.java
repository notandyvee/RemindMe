package com.remindme;

/*
 * Simple class that holds the info we get from the database.
 */
public class Memory {
	
	private int id;
	private String item;
	
	//TODO: Jake, use this var to put the rawImage returned by glass.
	private String rawImagePath;
	
	private String resizedImagePath;

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
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
	
	

}//end of class
