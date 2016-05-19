package org.cy.photo.crop;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.opengl.GLES10;

public class CropHelp {

	private static final int SIZE_DEFAULT = 2048;
	private static final int SIZE_LIMIT = 4096;
	Activity activity;
	private int sampleSize;
	private Bitmap cropBitmap;
	private Uri sourceUri;

	public CropHelp(Activity activity) {
		this.activity = activity;
	}

	public Bitmap getBitmap() {
		return cropBitmap;
	}

	public Bitmap getDegreeBitmap() {
		if (cropBitmap == null)
			return null;
		int degree = readPictureDegree(sourceUri.getPath());
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap resizedBitmap = Bitmap.createBitmap(cropBitmap, 0, 0,
				cropBitmap.getWidth(), cropBitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public void loadInput(Uri sourceUri) {

		if (sourceUri != null) {
			this.sourceUri = sourceUri;
			// exifRotation = CropUtil.getExifRotation(CropUtil.getFromMediaUri(
			// this, getContentResolver(), sourceUri));
			InputStream is = null;
			try {
				sampleSize = calculateBitmapSampleSize(sourceUri);
				is = activity.getContentResolver().openInputStream(sourceUri);
				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inSampleSize = sampleSize;
				cropBitmap = BitmapFactory.decodeStream(is, null, option);
				// rotateBitmap = new RotateBitmap(cropBitmap, exifRotation);
			} catch (IOException e) {
				setResultException(e);
			} catch (OutOfMemoryError e) {
				setResultException(e);
			} finally {
				CropUtil.closeSilently(is);
			}
		}
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	private int calculateBitmapSampleSize(Uri bitmapUri) throws IOException {
		InputStream is = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		try {
			is = activity.getContentResolver().openInputStream(bitmapUri);
			BitmapFactory.decodeStream(is, null, options); // Just get image
		} finally {
			CropUtil.closeSilently(is);
		}
		int maxSize = getMaxImageSize();
		int sampleSize = 1;
		while (options.outHeight / sampleSize > maxSize
				|| options.outWidth / sampleSize > maxSize) {
			sampleSize = sampleSize << 1;
		}
		return sampleSize;
	}

	private int getMaxImageSize() {
		int textureLimit = getMaxTextureSize();
		if (textureLimit == 0) {
			return SIZE_DEFAULT;
		} else {
			return Math.min(textureLimit, SIZE_LIMIT);
		}
	}

	private int getMaxTextureSize() {
		// The OpenGL texture size is the maximum size that can be drawn in an
		// ImageView
		int[] maxSize = new int[1];
		GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
		return maxSize[0];
	}

	private void setResultException(Throwable throwable) {
		activity.setResult(Crop.RESULT_ERROR,
				new Intent().putExtra(Crop.Extra.ERROR, throwable));
	}
}
