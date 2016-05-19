package org.cy.photo.moels;

import java.util.List;

public class PhotoSelecter {

	List<PhotoAlbum> photoAlbums;
	List<String> photos;

	public void setPhotoAlbums(List<PhotoAlbum> photoAlbums) {
		this.photoAlbums = photoAlbums;
	}

	public void setPhotos(List<String> photos) {
		this.photos = photos;
	}

	public List<PhotoAlbum> getPhotoAlbums() {
		return photoAlbums;
	}

	public List<String> getPhotos() {
		return photos;
	}

}
