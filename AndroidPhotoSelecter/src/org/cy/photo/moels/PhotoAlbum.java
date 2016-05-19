package org.cy.photo.moels;

public class PhotoAlbum {

	private String name;
	private int count;
	private String recent;

	// private boolean isChecked;

	public PhotoAlbum() {
	}

	public PhotoAlbum(String name, int count, String recent) {
		this.name = name;
		this.count = count;
		this.recent = recent;
	}

	// public boolean isChecked() {
	// return isChecked;
	// }
	//
	// public void setChecked(boolean isChecked) {
	// this.isChecked = isChecked;
	// }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setRecent(String recent) {
		this.recent = recent;
	}

	public String getRecent() {
		return recent;
	}

	public void increaseCount() {
		count++;
	}

}
