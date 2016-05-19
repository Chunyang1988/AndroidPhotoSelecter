package org.cy.photo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cy.photo.moels.PhotoAlbum;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;

public class PhotoController {

	private ContentResolver resolver;
	private final int MAX_SIZE = 1024 * 15;
	public static final String RECENT_PHOTO = "最近照片";

	public PhotoController(Context context) {
		resolver = context.getContentResolver();
	}

	/** 获取最近照片列表recent_photos */
	public List<String> getRecentPhotos() {
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI,
				new String[] { ImageColumns.DATA, ImageColumns.DATE_ADDED,
						ImageColumns.SIZE }, null, null,
				ImageColumns.DATE_ADDED);
		if (cursor == null || !cursor.moveToNext())
			return new ArrayList<String>();
		List<String> photos = new ArrayList<String>();
		cursor.moveToLast();
		do {
			if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > MAX_SIZE) {
				// Photo photo = new Photo();
				String path = cursor.getString(cursor
						.getColumnIndex(ImageColumns.DATA));
				// photo.setPath(path);
				photos.add(path);
			}
		} while (cursor.moveToPrevious());
		return photos;
	}

	/** 获取所有相册列表 */
	public List<PhotoAlbum> getPhotoAlbums() {
		List<PhotoAlbum> albums = new ArrayList<PhotoAlbum>();
		Map<String, PhotoAlbum> map = new HashMap<String, PhotoAlbum>();
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI,
				new String[] { ImageColumns.DATA,
						ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.SIZE },
				null, null, null);
		if (cursor == null || !cursor.moveToNext())
			return new ArrayList<PhotoAlbum>();
		cursor.moveToLast();
		PhotoAlbum current = new PhotoAlbum(RECENT_PHOTO, 0,
				cursor.getString(cursor.getColumnIndex(ImageColumns.DATA))); // "最近照片"相册
		albums.add(current);
		do {
			if (cursor.getInt(cursor.getColumnIndex(ImageColumns.SIZE)) < MAX_SIZE)
				continue;

			current.increaseCount();
			String name = cursor.getString(cursor
					.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME));
			if (map.keySet().contains(name))
				map.get(name).increaseCount();
			else {
				PhotoAlbum album = new PhotoAlbum(name, 1,
						cursor.getString(cursor
								.getColumnIndex(ImageColumns.DATA)));
				map.put(name, album);
				albums.add(album);
			}
		} while (cursor.moveToPrevious());
		return albums;
	}

	/** 获取对应相册下的照片 */
	public List<String> getPhotoAlbum(String name) {
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI,
				new String[] { ImageColumns.BUCKET_DISPLAY_NAME,
						ImageColumns.DATA, ImageColumns.DATE_ADDED,
						ImageColumns.SIZE }, ImageColumns.BUCKET_DISPLAY_NAME
						+ " = ?", new String[] { name },
				ImageColumns.DATE_ADDED);
		if (cursor == null || !cursor.moveToNext())
			return new ArrayList<String>();
		List<String> photos = new ArrayList<String>();
		cursor.moveToLast();
		do {
			if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > MAX_SIZE) {
				// Photo photo = new Photo();
				String path = cursor.getString(cursor
						.getColumnIndex(ImageColumns.DATA));
				// photo.setPath(path);
				photos.add(path);
			}
		} while (cursor.moveToPrevious());
		return photos;
	}
}
