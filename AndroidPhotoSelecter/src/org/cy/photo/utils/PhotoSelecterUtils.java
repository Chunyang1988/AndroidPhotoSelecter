package org.cy.photo.utils;

import java.util.ArrayList;

import org.cy.photo.PhotoPreviewActivity;
import org.cy.photo.PhotoSelecterActivity;
import org.cy.photo.controller.CheckedManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PhotoSelecterUtils {

	public static final int PHOTO_2_PREVIEW = 100;
	public static final int PHOTO_RESULT = 111;

	public static void startPreview(Activity activity, int position) {
		Intent i = new Intent(activity, PhotoPreviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(KEY.PHOTO_LIST,
				CheckedManager.getChouseList());
		bundle.putInt(KEY.POSITION, position);
		i.putExtras(bundle);
		activity.startActivityForResult(i, PHOTO_2_PREVIEW);
	}

	public static void startPreview(Activity activity,
			ArrayList<String> photos, int position) {
		Intent i = new Intent(activity, PhotoPreviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(KEY.PHOTO_LIST, photos);
		bundle.putInt(KEY.POSITION, position);
		i.putExtras(bundle);
		activity.startActivityForResult(i, PHOTO_2_PREVIEW);
	}

	public static void startPreviewDel(Activity activity,
			ArrayList<String> photos) {
		Intent i = new Intent(activity, PhotoPreviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(KEY.PHOTO_LIST, photos);
		bundle.putInt(KEY.POSITION, 0);
		bundle.putBoolean(KEY.IS_PREV_DEL, true);
		i.putExtras(bundle);
		activity.startActivityForResult(i, PHOTO_2_PREVIEW);
	}

	public static void startSelecter(Activity activity) {
		Intent i = new Intent(activity, PhotoSelecterActivity.class);
		i.putExtra(KEY.IS_SINGLE, false);
		activity.startActivityForResult(i, PHOTO_RESULT);
	}

	public static void startSelecterSingle(Activity activity) {
		Intent i = new Intent(activity, PhotoSelecterActivity.class);
		i.putExtra(KEY.IS_SINGLE, true);
		activity.startActivityForResult(i, PHOTO_RESULT);
	}

	public static void startSelecter(Activity activity, boolean isSingle) {
		Intent i = new Intent(activity, PhotoSelecterActivity.class);
		i.putExtra(KEY.IS_SINGLE, isSingle);
		activity.startActivityForResult(i, PHOTO_RESULT);
	}

	public static String getPhotoReult(Intent data) {
		boolean isSingle = data.getBooleanExtra(KEY.IS_SINGLE, false);
		if (isSingle) {
			Bundle bundle = data.getExtras();
			return bundle.getString(KEY.PHOTO_PATH);
		} else
			return null;
	}

	public static ArrayList<String> getPhotoListReult(Intent data) {
		Bundle bundle = data.getExtras();
		return bundle.getStringArrayList(KEY.PHOTO_LIST);
	}

}
