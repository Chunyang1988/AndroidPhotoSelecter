package org.cy.photo.presenter;

import java.util.List;

import org.cy.photo.controller.PhotoController;
import org.cy.photo.moels.PhotoAlbum;
import org.cy.photo.moels.PhotoSelecter;

import android.content.Context;
import android.os.AsyncTask;

public class PhotoSelecterPresenter {

	private PhotoController controller;
	private PhotoSelecterIble listener;

	public PhotoSelecterPresenter(Context context) {
		controller = new PhotoController(context);
	}

	public void setPhotoSelecterIble(PhotoSelecterIble listener) {
		this.listener = listener;
	}

	public void getPhotos() {
		new TaskPhoto().execute();
	}

	private class TaskPhoto extends AsyncTask<Void, Void, PhotoSelecter> {

		@Override
		protected PhotoSelecter doInBackground(Void... params) {
			List<String> photos = controller.getRecentPhotos();
			List<PhotoAlbum> photoAlbums = controller.getPhotoAlbums();
			PhotoSelecter ps = new PhotoSelecter();
			ps.setPhotoAlbums(photoAlbums);
			ps.setPhotos(photos);
			return ps;
		}

		@Override
		protected void onPostExecute(PhotoSelecter result) {
			super.onPostExecute(result);
			listener.onPhotoSelecter(result);
		}
	}

	public void getRecentPhotos() {
		new TaskRecent().execute();
	}

	private class TaskRecent extends AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... params) {
			return controller.getRecentPhotos();
		}

		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			listener.onPhotos(result);
		}
	}

	public void getPhotoAlumbs(String name) {
		new TaskAlumbs().execute(name);
	}

	private class TaskAlumbs extends AsyncTask<String, Void, List<String>> {

		@Override
		protected List<String> doInBackground(String... params) {
			String name = params[0];
			return controller.getPhotoAlbum(name);
		}

		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			listener.onPhotos(result);
		}
	}

}
