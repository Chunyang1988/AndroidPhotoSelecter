package org.cy.photo.controller;

import java.util.ArrayList;

import android.content.Context;
import android.widget.Toast;

public class CheckedManager {

	public static int MAX_CHOUSE = 6;
	private static ArrayList<String> chousePhotos = new ArrayList<String>();

	public static void setMaxChouse(int count) {
		MAX_CHOUSE = count;
	}

	public static int getChouseSize() {
		return chousePhotos.size();
	}

	public static void clean() {
		chousePhotos.clear();
	}

	public static ArrayList<String> getChouseList() {
		return chousePhotos;
	}

	public static boolean addChousPath(String path) {
		if (chousePhotos.size() == 6)
			return false;
		if (!chousePhotos.contains(path)) {
			chousePhotos.add(path);
			return true;
		} else
			return false;
	}

	public static boolean removeChousPath(String path) {
		if (chousePhotos.contains(path)) {
			chousePhotos.remove(path);
			return true;
		} else
			return false;
	}

	public static boolean contains(String path) {
		return chousePhotos.contains(path);
	}

	public static void toastShow(Context context) {
		Toast.makeText(context, "超出最大限制", Toast.LENGTH_SHORT).show();
	}
}
